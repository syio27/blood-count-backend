package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.AnswerRequest;
import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.exceptions.GameCompleteException;
import com.pja.bloodcount.exceptions.GameNotFoundException;
import com.pja.bloodcount.exceptions.GameStartException;
import com.pja.bloodcount.mapper.CaseMapper;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.GameCaseDetailsRepository;
import com.pja.bloodcount.repository.GameRepository;
import com.pja.bloodcount.repository.UserRepository;
import com.pja.bloodcount.validation.CaseValidator;
import com.pja.bloodcount.validation.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class GameService {

    private final GameRepository repository;
    private final UserRepository userRepository;
    private final GameCaseDetailsRepository caseDetailsRepository;
    private final GenerationService generationService;
    private final CaseValidator caseValidator;
    private final QnAService qnAService;
    private final UserValidator userValidator;

    public GameResponse createGame(Long caseId, UUID userId) {
        User user = userValidator.validateIfExistsAndGet(userId);
        List<Game> gamesOfUser = repository.findByUser_Id(userId);
        gamesOfUser.forEach(game -> {
            if(game.getStatus().equals(Status.IN_PROGRESS)){
                throw new GameStartException("You already have running game session, please complete it before starting new");
            }
        });

        Patient patient = generationService.generatePatient(caseId);
        Case aCase = caseValidator.validateIfExistsAndGet(caseId);
        generationService.generateBloodCount(caseId, patient.getId());
        int durationInMin = 1;
        int durationInSec = durationInMin * 60;
        Instant endTime = Instant.now().plusSeconds(durationInSec);

        GameCaseDetails caseDetails = GameCaseDetails
                .builder()
                .anemiaType(aCase.getAnemiaType())
                .diagnosis(aCase.getDiagnosis())
                .build();

        caseDetailsRepository.save(caseDetails);

        Game game = Game
                .builder()
                // end time not applicable yet, should be updated when Status.COMPLETED
                .endTime(null)
                .estimatedEndTime(Date.from(endTime))
                .status(Status.IN_PROGRESS)
                .testDuration(durationInMin)
                .caseDetails(caseDetails)
                .build();

        game.addPatient(patient);
        repository.save(game);
        List<BCAssessmentQuestion> qnAForBCAssessment = qnAService.createQnAForBCAssessment(game.getId());
        List<MSQuestion> qnAForMSQ = qnAService.createMSQuestions(game.getId());
        List<MSQuestion> anAForTrueFalseMSQ = qnAService.createTrueFalseMSQuestions(game.getId());
        qnAForBCAssessment.forEach(game::addQuestion);
        qnAForMSQ.forEach(game::addQuestion);
        anAForTrueFalseMSQ.forEach(game::addQuestion);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game is created");
        return GameMapper.mapToResponseDTO(game);
    }

    public GameResponse completeGame(Long gameId, List<AnswerRequest> answerRequestList){
        Optional<Game> optionalGame = repository.findById(gameId);
        if(optionalGame.isEmpty()){
            throw new GameNotFoundException(gameId);
        }
        Game game = optionalGame.get();
        if(game.getStatus().equals(Status.COMPLETED)){
            throw new GameCompleteException("Game is already submitted");
        }
        int score = qnAService.score(answerRequestList, gameId);
        game.setStatus(Status.COMPLETED);
        game.setScore(score);
        if(game.getStatus() == Status.COMPLETED){
            Instant completedTime = Instant.now();
            game.setEndTime(Date.from(completedTime));
        }
        repository.save(game);
        return GameMapper.mapToResponseDTO(game);
    }
}
