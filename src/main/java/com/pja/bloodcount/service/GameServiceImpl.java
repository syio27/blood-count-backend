package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.*;
import com.pja.bloodcount.exceptions.*;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Language;
import com.pja.bloodcount.model.enums.Pages;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.*;
import com.pja.bloodcount.service.contract.GameService;
import com.pja.bloodcount.validation.CaseValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@AllArgsConstructor
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

    @Override
    public GameResponse createGame(Long caseId, UUID userId, Language language) {
        User user = userValidator.validateIfExistsAndGet(userId);
        List<Game> gamesOfUser = repository.findByUser_Id(userId);
        gamesOfUser.forEach(game -> {
            if (game.getStatus().equals(Status.IN_PROGRESS)) {
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

        Instant currentTimeInstant = Instant.now();
        Date currentDate = Date.from(currentTimeInstant);

        List<BCAssessmentQuestion> qnAForBCAssessment = qnAService.createQnAForBCAssessment(game.getId());
        List<MSQuestion> qnAForMSQ = qnAService.createMSQuestions(game.getId(), language);
        List<MSQuestion> anAForTrueFalseMSQ = qnAService.createTrueFalseMSQuestions(game.getId(), language);
        qnAForBCAssessment.forEach(game::addQuestion);
        qnAForMSQ.forEach(game::addQuestion);
        anAForTrueFalseMSQ.forEach(game::addQuestion);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game is created");
        return GameMapper.mapToResponseDTO(game, currentDate, getSavedAnswersOfGame(userId, game.getId()));
    }

    @Override
    public SimpleGameResponse completeGame(Long gameId) {
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
        if (game.getStatus().equals(Status.COMPLETED)) {
            throw new GameCompleteException("Game is already submitted");
        }
        int score = qnAService.score(gameId);
        game.setStatus(Status.COMPLETED);
        game.setScore(score);
        if (game.getStatus() == Status.COMPLETED) {
            Instant completedTime = Instant.now();
            game.setEndTime(Date.from(completedTime));
        }
        repository.save(game);
        return GameMapper.mapToSimpleResponseDTO(game);
    }

    @Override
    public void saveSelectedAnswers(Long gameId, List<AnswerRequest> answerRequestList) {
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
        if (game.getStatus().equals(Status.COMPLETED)) {
            throw new GameCompleteException("Game is already submitted");
        }
        List<UserAnswer> userAnswers = new ArrayList<>();
        answerRequestList.forEach(
                answerRequest -> {
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
                                .question(question)
                                .build();

                        userAnswers.add(userAnswer);
                    }
                }
        );
        userAnswerRepository.saveAll(userAnswers);
    }

    @Override
    public GameCurrentSessionState next(UUID userId, Long gameId, List<AnswerRequest> answerRequestList) {
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
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
        return GameMapper.mapToSimpleResponseListDTO(
                games.stream()
                        .filter(game -> game.getStatus().equals(Status.COMPLETED))
                        .toList());
    }

    private List<SavedUserAnswerResponse> getSavedAnswersOfGame(UUID userId, Long gameId) {
        List<SavedUserAnswerResponse> savedUserAnswers = new ArrayList<>();
        userValidator.validateIfExistsAndGet(userId);
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
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

    @Deprecated
    @Override
    public List<UserSelectedAnswerResponse> getSelectedAnswersOfGame(UUID userId, Long gameId) {
        List<UserSelectedAnswerResponse> selectedAnswerResponses = new ArrayList<>();
        userValidator.validateIfExistsAndGet(userId);
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
        List<UserAnswer> selectedAnswers = userAnswerRepository.findByUser_IdAndGame_Id(userId, gameId);
        List<Question> questions = game.getQuestions();
        selectedAnswers.forEach(selectedAnswer -> {

            Optional<Question> optionalQuestion = questions.stream().filter(q -> Objects.equals(q.getId(), selectedAnswer.getQuestion().getId())).findFirst();
            if (optionalQuestion.isEmpty()) {
                throw new RuntimeException("bad error");
            }
            Question question = optionalQuestion.get();
            Question unproxiedQuestion = initializeAndUnproxy(question);
            UserSelectedAnswerResponse selectedAnswerResponse = UserSelectedAnswerResponse
                    .builder()
                    .id(selectedAnswer.getId())
                    .build();
            if (unproxiedQuestion instanceof MSQuestion msQuestion) {
                selectedAnswerResponse.setQuestionText(msQuestion.getText());
            } else if (unproxiedQuestion instanceof BCAssessmentQuestion bcAssessmentQuestion) {
                selectedAnswerResponse.setQuestionText(
                        bcAssessmentQuestion.getParameter() + "(" +
                                bcAssessmentQuestion.getUnit() + ") - value: " +
                                bcAssessmentQuestion.getValue());
            }
            selectedAnswerResponse.setAnswer(selectedAnswer.getAnswer().getText());
            selectedAnswerResponses.add(selectedAnswerResponse);
        });
        return selectedAnswerResponses;
    }

    @Override
    public GameResponse getInProgressGame(Long gameId, UUID userId) {
        userValidator.validateIfExistsAndGet(userId);
        Optional<Game> optionalGame = repository.findById(gameId);
        if (optionalGame.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
        if (game.getStatus().equals(Status.COMPLETED)) {
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
            if (game.getStatus().equals(Status.IN_PROGRESS)) {
                hasGameInProgress.set(true);
                gameInProgress.setInProgress(hasGameInProgress.get());
                gameInProgress.setGameId(game.getId());
            }
        });
        return gameInProgress;
    }

    private static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            return null;
        }

        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

}
