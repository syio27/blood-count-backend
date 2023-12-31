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

    private static final String QUESTION_NOT_PART_EX_MESSAGE = "Question is not part game: %s";
    private static final String ANSWER_NOT_PART_EX_MESSAGE = "Answer is not part of answers set of question: %s";
    private static final String GAME_START_EX_MESSAGE = "You already have running game session, please complete it before starting new";
    private static final String GAME_SUBMITTED_EX_MESSAGE = "Game with id - %s already completed";
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
        gamesOfUser.forEach(game -> game.ifInProgressThenThrow(() -> new GameStartException(GAME_START_EX_MESSAGE)));
        Patient patient = generationService.generatePatient(caseId);
        Case aCase = caseValidator.validateIfExistsAndGet(caseId);
        generationService.generateBloodCount(caseId, patient.getId());
        int durationInMin = 30;
        int durationInSec = durationInMin * 60;
        Instant endTime = Instant.now().plusSeconds(durationInSec);
        GameCaseDetails caseDetails = makeGameCaseDetails(caseId, aCase);
        caseDetailsRepository.save(caseDetails);
        Game game = makeGame(language, endTime, durationInMin, caseDetails);
        game.addPatient(patient);
        repository.save(game);
        List<Question> allQuestions = makeQuestionsWithAnswers(language, game);
        game.addAllQuestions(allQuestions);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game session is being started");
        delayedGameQueue.put(new DelayedGame(game, durationInMin, TimeUnit.MINUTES));
    }

    private List<Question> makeQuestionsWithAnswers(Language language, Game game) {
        List<BCAssessmentQuestion> qnAForBCAssessment = qnAService.createQnAForBCAssessment(game.getId());
        List<MSQuestion> qnAForMSQ = qnAService.createMSQuestions(game.getId(), language);
        List<MSQuestion> qnAForTrueFalseMSQ = qnAService.createTrueFalseMSQuestions(language);
        return mergeQuestions(qnAForBCAssessment, qnAForMSQ, qnAForTrueFalseMSQ);
    }

    private static GameCaseDetails makeGameCaseDetails(Long caseId, Case aCase) {
        return GameCaseDetails
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
    }

    private static Game makeGame(Language language, Instant endTime, int durationInMin, GameCaseDetails caseDetails) {
        return Game
                .builder()
                .endTime(null)
                .language(language)
                .estimatedEndTime(Date.from(endTime))
                .status(Status.IN_PROGRESS)
                .currentPage(Pages.ONE)
                .testDuration(durationInMin)
                .caseDetails(caseDetails)
                .build();
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
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new QuestionNotFoundException(answerRequest.getAnswerId()))
                    .isPartOfGameOrThrow(gameId, () -> new QuestionNotPartException(QUESTION_NOT_PART_EX_MESSAGE.formatted(gameId)));

            Answer answer = answerRepository.findById(answerRequest.getAnswerId())
                    .orElseThrow(() -> new AnswerNotFoundException(answerRequest.getAnswerId()))
                    .isPartOfQuestionOrThrow(answerRequest.getQuestionId(),
                            () -> new AnswerNotPartException(ANSWER_NOT_PART_EX_MESSAGE.formatted(answerRequest.getQuestionId())));

            // Look for existing UserAnswer for the question to update, or create new
            updateOrCreateUserAnswer(question, game, answer, userAnswers);
        });
        userAnswerRepository.saveAll(userAnswers);
    }

    private void updateOrCreateUserAnswer(Question question, Game game, Answer answer, List<UserAnswer> userAnswers) {
        userAnswerRepository.findByQuestionAndGame(question, game)
                .ifPresentOrElse(existingUserAnswerToUpdate -> {
                    existingUserAnswerToUpdate.setAnswer(answer);
                    userAnswers.add(existingUserAnswerToUpdate);
                }, () -> {
                    UserAnswer userAnswer = UserAnswer
                            .builder()
                            .game(question.getGame())
                            .user(question.getGame().getUser())
                            .answer(answer)
                            .question(question).build();
                    userAnswers.add(userAnswer);
                });
    }

    /**
     * Move the Game to the next page
     */
    @Override
    public GameCurrentSessionState next(UUID userId, Long gameId, List<AnswerRequest> answerRequestList) {
        User user = userValidator.validateIfExistsAndGet(userId);

        return user.getGames().stream()
                .filter(game -> Objects.equals(game.getId(), gameId))
                .findFirst()
                .map(game -> {
                    checkIfGameCompleted(game);
                    saveSelectedAnswers(gameId, answerRequestList);
                    Pages currentPage = Pages.next(game.getCurrentPage());
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
                .findFirst()
                .orElseThrow(() -> new GameCompleteException(GAME_SUBMITTED_EX_MESSAGE.formatted(gameId)));
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
        game.ifCompletedThenThrow(() -> new GameCompleteException(GAME_SUBMITTED_EX_MESSAGE.formatted(game.getId())));
    }
}
