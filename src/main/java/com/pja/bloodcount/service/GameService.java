package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.response.GameResponse;
import com.pja.bloodcount.mapper.CaseMapper;
import com.pja.bloodcount.mapper.GameMapper;
import com.pja.bloodcount.model.*;
import com.pja.bloodcount.model.enums.Status;
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
    private final GenerationService generationService;
    private final CaseValidator caseValidator;
    private final QnAService qnAService;
    private final UserValidator userValidator;

    public GameResponse createGame(Long caseId, UUID userId) {
        Patient patient = generationService.generatePatient(caseId);
        Case aCase = caseValidator.validateIfExistsAndGet(caseId);
        generationService.generateBloodCount(patient.getId(), patient.getId());
        User user = userValidator.validateIfExistsAndGet(userId);
        int durationInMin = 30;
        int durationInSec = durationInMin * 60;
        Instant endTime = Instant.now().plusSeconds(durationInSec);

        Game game = Game
                .builder()
                // end time not applicable yet, should be updated when Status.COMPLETED
                .endTime(null)
                .estimatedEndTime(Date.from(endTime))
                .status(Status.IN_PROGRESS)
                .testDuration(durationInMin)
                .gameCase(aCase)
                .build();

        game.addPatient(patient);
        repository.save(game);
        List<BCAssessmentQuestion> qnAForBCAssessment = qnAService.createQnAForBCAssessment(game.getId());
        qnAForBCAssessment.forEach(game::addBCAssessmentQuestion);
        repository.save(game);
        user.addGame(game);
        userRepository.save(user);
        log.info("Game is created");
        return GameMapper.mapToResponseDTO(game);
    }

    public GameResponse completeGame(Long gameId){
        Optional<Game> optionalGame = repository.findById(gameId);
        if(optionalGame.isEmpty()){
            // TODO: change to Game related Exception class
            throw new RuntimeException("game is not found");
        }
        Game game = optionalGame.get();
        game.setStatus(Status.COMPLETED);
        if(game.getStatus() == Status.COMPLETED){
            Instant completedTime = Instant.now();
            game.setEndTime(Date.from(completedTime));
        }
        repository.save(game);
        return GameMapper.mapToResponseDTO(game);
    }
}
