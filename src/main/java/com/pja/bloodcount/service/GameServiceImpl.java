package com.pja.bloodcount.service;

import com.pja.bloodcount.constant.MailMessageConstants;
import com.pja.bloodcount.constant.MailSubjectConstants;
import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.*;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.htmlcontent.MailHtmlContent;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.mapper.QnAMapper;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Question> allQuestions = mergeQuestions(qnAForBCAssessment, qnAForMSQ, qnAForTrueFalseMSQ);
        game.addAllQuestions(allQuestions);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game session is being started");
        delayedGameQueue.put(new DelayedGame(game, durationInMin, TimeUnit.MINUTES));
    }

    @SafeVarargs
    private static List<Question> mergeQuestions(List<? extends Question>... questionLists) {
        return Stream.of(questionLists)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Override
    public SimpleGameResponse completeGame(Long gameId) {
        return repository.findById(gameId)
                .map(game -> {
                    checkIfGameCompleted(game);
                    makeGameCompleted(game);
                    repository.save(game);
                    removeGameFromQueue(game.getId());
                    return GameMapper.mapToSimpleResponseDTO(game);
                })
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @Override
    @Transactional
    public void queueCompleteGame(Long gameId) {
        repository.findById(gameId)
                .ifPresentOrElse(game -> {
                    checkIfGameCompleted(game);
                    makeGameCompleted(game);
                    repository.save(game);
                    notifyUserViaEmail(game.getUser().getEmail());
                }, () -> {
                    removeGameFromQueue(gameId);
                    throw new GameNotFoundException(gameId);
                });
    }

    private void makeGameCompleted(Game game) {
        int score = scoreService.score(game.getId());
        game.setStatus(Status.COMPLETED);
        game.setScore(score);
        Instant completedTime = Instant.now();
        game.setEndTime(Date.from(completedTime));
    }

    private void removeGameFromQueue(Long gameId) {
        delayedGameQueue.removeIf(delayedGame -> delayedGame.getGame().getId().equals(gameId));
    }

    private void notifyUserViaEmail(String userEmail) {
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
        checkIfGameCompleted(game);
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
        User user = userValidator.validateIfExistsAndGet(userId);

        return user.getGames().stream()
                .filter(game -> Objects.equals(game.getId(), gameId))
                .findFirst()
                .map(game -> {
                    checkIfGameCompleted(game);
                    saveSelectedAnswers(gameId, answerRequestList);
                    Pages currentPage = next(game.getCurrentPage());
                    game.setCurrentPage(currentPage);
                    repository.save(game);
                    return makeGameCurrentSessionState(game);
                })
                .orElseThrow(() -> new GameNotFoundException(gameId));
    }

    private GameCurrentSessionState makeGameCurrentSessionState(Game game) {
        return GameCurrentSessionState
                .builder()
                .gameId(game.getId())
                .status(game.getStatus())
                .currentPage(game.getCurrentPage())
                .build();
    }

    private Pages next(Pages currentPage) {
        if (currentPage == Pages.FOUR) {
            log.warn("User is on last page of Game");
            return currentPage;
        }
        return currentPage.getNextPage();
    }

    @Override
    public List<SimpleGameResponse> getAllCompletedGamesOfUser(UUID userId) {
        User user = userValidator.validateIfExistsAndGet(userId);
        return user.getGames().stream()
                .filter(Game::isCompleted)
                .map(GameMapper::mapToSimpleResponseDTO)
                .toList();
    }

    private List<SavedUserAnswerResponse> getSavedAnswersOfGame(UUID userId, Long gameId) {
        List<UserAnswer> selectedAnswers = userAnswerRepository.findByUser_IdAndGame_Id(userId, gameId);
        return selectedAnswers.stream()
                .map(QnAMapper::mapToSavedUserAnswerResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public GameResponse getInProgressGame(Long gameId, UUID userId) {
        User user = userValidator.validateIfExistsAndGet(userId);
        return user.getGames().stream()
                .filter(game -> Objects.equals(game.getId(), gameId) && game.isInProgress())
                .map(game -> {
                    log.info("In Progress game: {}", game);
                    return GameMapper.mapToResponseDTO(game, Date.from(Instant.now()), getSavedAnswersOfGame(user.getId(), game.getId()));
                })
                .findAny()
                .orElseThrow(() -> new GameCompleteException("Game with id - " + gameId + " already completed"));
    }

    @Override
    public GameInProgress hasGameInProgress(UUID userId) {
        User user = userValidator.validateIfExistsAndGet(userId);
        List<Game> games = user.getGames();
        return games.stream()
                .filter(Game::isInProgress)
                .map(GameMapper::mapToGameInProgressDTO)
                .findFirst()
                .orElseGet(
                        () -> GameInProgress
                                .builder()
                                .build()
                );
    }

    private static void checkIfGameCompleted(Game game) {
        if (game.isCompleted()) {
            throw new GameCompleteException("Game is already submitted");
        }
    }
}
