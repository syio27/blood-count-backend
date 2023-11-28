package com.pja.bloodcount.service;

import com.pja.bloodcount.constant.MailMessageConstants;
import com.pja.bloodcount.constant.MailSubjectConstants;
import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.*;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.htmlcontent.MailHtmlContent;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.model.enums.Pages;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.*;
import com.pja.bloodcount.service.completion.DelayedGame;
import com.pja.bloodcount.service.contract.GameService;
import com.pja.bloodcount.service.contract.NotifierService;
import com.pja.bloodcount.service.contract.QnAService;
import com.pja.bloodcount.service.contract.ScoreService;
import com.pja.bloodcount.validation.CaseValidator;
import com.pja.bloodcount.validation.GameValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository repository;
    private final UserRepository userRepository;
    private final GameCaseDetailsRepository caseDetailsRepository;
    private final GenerationService generationService;
    private final CaseValidator caseValidator;
    private final QnAService qnAService;
    private final UserValidator userValidator;
    private final UserAnswerRepository userAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final ScoreService scoreService;
    private final DelayQueue<DelayedGame> delayedGameQueue;
    private final NotifierService notifierService;
    private final GameValidator gameValidator;
    @Value("${app.url}")
    private String url;

    @Override
    public void startGameSession(Long caseId, UUID userId, Language language) {
        User user = userValidator.validateIfExistsAndGet(userId);
        List<Game> gamesOfUser = repository.findByUser_Id(userId);
        gamesOfUser.forEach(game -> {
            if (game.isInProgress()) {
                throw new GameStartException("You already have running game session, please complete it before starting new");
            }
        });

        Patient patient = generationService.generatePatient(caseId);
        Case aCase = caseValidator.validateIfExistsAndGet(caseId);
        generationService.generateBloodCount(caseId, patient.getId());
        int durationInMin = 30;
        int durationInSec = durationInMin * 60;
        Instant endTime = Instant.now().plusSeconds(durationInSec);

        GameCaseDetails caseDetails = GameCaseDetails
                .builder()
                .anActualCaseId(caseId)
                .anemiaType(aCase.getAnemiaType())
                .diagnosis(aCase.getDiagnosis())
                .hr(aCase.getHr())
                .rr(aCase.getRr())
                .description(aCase.getDescription())
                .infoCom(aCase.getInfoCom())
                .language(aCase.getLanguage())
                .caseName(aCase.getCaseName())
                .bmi(aCase.getBmi())
                .height(aCase.getHeight())
                .bodyMass(aCase.getBodyMass())
                .build();

        caseDetailsRepository.save(caseDetails);

        Game game = Game
                .builder()
                .endTime(null)
                .language(language)
                .estimatedEndTime(Date.from(endTime))
                .status(Status.IN_PROGRESS)
                .currentPage(Pages.ONE)
                .testDuration(durationInMin)
                .caseDetails(caseDetails)
                .build();

        game.addPatient(patient);
        repository.save(game);

        List<BCAssessmentQuestion> qnAForBCAssessment = qnAService.createQnAForBCAssessment(game.getId());
        List<MSQuestion> qnAForMSQ = qnAService.createMSQuestions(game.getId(), language);
        List<MSQuestion> qnAForTrueFalseMSQ = qnAService.createTrueFalseMSQuestions(language);
        qnAForBCAssessment.forEach(game::addQuestion);
        qnAForMSQ.forEach(game::addQuestion);
        qnAForTrueFalseMSQ.forEach(game::addQuestion);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game session is being started");
        delayedGameQueue.put(new DelayedGame(game, durationInMin, TimeUnit.MINUTES));
    }

    @Override
    public SimpleGameResponse completeGame(Long gameId) {
        Game game = gameValidator.validateIfExistsAndGet(gameId);
        if (game.isCompleted()) {
            throw new GameCompleteException("Game is already submitted");
        }
        int score = scoreService.score(gameId);
        game.setStatus(Status.COMPLETED);
        game.setScore(score);
        Instant completedTime = Instant.now();
        game.setEndTime(Date.from(completedTime));
        repository.save(game);
        delayedGameQueue.removeIf(delayedGame -> delayedGame.getGame().getId().equals(gameId));
        return GameMapper.mapToSimpleResponseDTO(game);
    }

    @Override
    @Transactional
    public void queueCompleteGame(Long gameId) {
        Game game = gameValidator.validateIfExistsAndGet(gameId);
        String userEmail = game.getUser().getEmail();
        if (game.isCompleted()) {
            delayedGameQueue.removeIf(delayedGame -> delayedGame.getGame().getId().equals(gameId));
            throw new GameCompleteException("Game is already submitted");
        }
        int score = scoreService.score(gameId);
        game.setStatus(Status.COMPLETED);
        game.setScore(score);
        Instant completedTime = Instant.now();
        game.setEndTime(Date.from(completedTime));
        repository.save(game);
        final String historyPagePath = url + "/history";
        final String buttonLabel = "Check History";
        notifierService.notifyUser(userEmail, MailSubjectConstants.getGameCompleteSubject(),
                MailHtmlContent.getHtmlMessage(
                        MailMessageConstants.getGameCompleteMessage(),
                        historyPagePath,
                        buttonLabel,
                        true));
    }

    @Override
    public void saveSelectedAnswers(Long gameId, List<AnswerRequest> answerRequestList) {
        Game game = gameValidator.validateIfExistsAndGet(gameId);
        if (game.isCompleted()) {
            throw new GameCompleteException("Game is already submitted");
        }
        List<UserAnswer> userAnswers = new ArrayList<>();
        answerRequestList.forEach(answerRequest -> {
            Optional<Question> optionalQuestion = questionRepository.findById(answerRequest.getQuestionId());
            if (optionalQuestion.isEmpty()) {
                throw new QuestionNotFoundException(answerRequest.getAnswerId());
            }
            Question question = optionalQuestion.get();
            if (!Objects.equals(question.getGame().getId(), gameId)) {
                throw new QuestionNotPartException("Question is not part game: " + gameId);
            }
            Optional<Answer> optionalAnswer = answerRepository.findById(answerRequest.getAnswerId());
            if (optionalAnswer.isEmpty()) {
                throw new AnswerNotFoundException(answerRequest.getAnswerId());
            }
            Answer answer = optionalAnswer.get();
            log.info("Answer's question id: {}", answer.getQuestion().getId());
            log.info("question id from request: {}", answerRequest.getQuestionId());
            if (!Objects.equals(answer.getQuestion().getId(), answerRequest.getQuestionId())) {
                throw new AnswerNotPartException("Answer is not part of answers set of question: " + answerRequest.getQuestionId());
            }

            // Look for existing UserAnswer for the question
            Optional<UserAnswer> existingUserAnswer = userAnswerRepository.findByQuestionAndGame(question, game);

            if (existingUserAnswer.isPresent()) {
                // Update the existing answer with the new one
                UserAnswer userAnswerToUpdate = existingUserAnswer.get();
                userAnswerToUpdate.setAnswer(answer);
                userAnswers.add(userAnswerToUpdate);
            } else {
                // Create a new UserAnswer
                UserAnswer userAnswer = UserAnswer
                        .builder()
                        .game(question.getGame())
                        .user(question.getGame().getUser())
                        .answer(answer)
                        .question(question).build();
                userAnswers.add(userAnswer);
            }
        });
        userAnswerRepository.saveAll(userAnswers);
    }

    @Override
    public GameCurrentSessionState next(UUID userId, Long gameId, List<AnswerRequest> answerRequestList) {
        Game game = gameValidator.validateIfExistsAndGet(gameId);
        if (game.getStatus().equals(Status.COMPLETED)) {
            throw new GameCompleteException("Game is already submitted");
        }
        this.saveSelectedAnswers(gameId, answerRequestList);
        Pages currentPage = game.getCurrentPage();
        currentPage = this.next(currentPage);
        game.setCurrentPage(currentPage);
        repository.save(game);

        return GameCurrentSessionState
                .builder()
                .gameId(gameId)
                .estimatedEndTime(game.getEstimatedEndTime())
                .status(game.getStatus())
                .currentPage(game.getCurrentPage())
                .savedUserAnswers(getSavedAnswersOfGame(userId, gameId))
                .build();
    }

    private Pages next(Pages currentPage) {
        if (currentPage != Pages.FOUR) {
            currentPage = currentPage.getNextPage();
        } else {
            log.warn("User is on last page of Game");
        }
        return currentPage;
    }

    @Override
    public List<SimpleGameResponse> getAllCompletedGamesOfUser(UUID userId) {
        userValidator.validateIfExistsAndGet(userId);
        List<Game> games = repository.findByUser_Id(userId);
        return GameMapper.mapToSimpleResponseListDTO(games.stream().filter(Game::isCompleted).toList());
    }

    private List<SavedUserAnswerResponse> getSavedAnswersOfGame(UUID userId, Long gameId) {
        List<SavedUserAnswerResponse> savedUserAnswers = new ArrayList<>();
        userValidator.validateIfExistsAndGet(userId);
        gameValidator.validateIfExistsAndGet(gameId);
        List<UserAnswer> selectedAnswers = userAnswerRepository.findByUser_IdAndGame_Id(userId, gameId);
        selectedAnswers.forEach(savedUserAnswer -> {
            SavedUserAnswerResponse savedUserAnswerResponse = SavedUserAnswerResponse
                    .builder()
                    .answerId(savedUserAnswer.getAnswer().getId())
                    .questionId(savedUserAnswer.getQuestion().getId())
                    .build();
            savedUserAnswers.add(savedUserAnswerResponse);
        });

        return savedUserAnswers;
    }

    @Override
    public GameResponse getInProgressGame(Long gameId, UUID userId) {
        userValidator.validateIfExistsAndGet(userId);
        Game game = gameValidator.validateIfExistsAndGet(gameId);
        if (game.isCompleted()) {
            throw new GameCompleteException("Game with id - " + game.getId() + " already completed");
        }
        Instant currentTimeInstant = Instant.now();
        Date currentDate = Date.from(currentTimeInstant);
        log.info("BC question set size: {}", game.getBcAssessmentQuestions().size());
        log.info("MS question set size: {}", game.getMsQuestions().size());
        return GameMapper.mapToResponseDTO(game, currentDate, getSavedAnswersOfGame(userId, gameId));
    }

    @Override
    public GameInProgress hasGameInProgress(UUID userId) {
        GameInProgress gameInProgress = GameInProgress.builder().build();
        User user = userValidator.validateIfExistsAndGet(userId);
        List<Game> games = user.getGames();
        AtomicBoolean hasGameInProgress = new AtomicBoolean(false);
        games.forEach(game -> {
            if (game.isInProgress()) {
                hasGameInProgress.set(true);
                gameInProgress.setInProgress(hasGameInProgress.get());
                gameInProgress.setGameId(game.getId());
            }
        });
        return gameInProgress;
    }
}
