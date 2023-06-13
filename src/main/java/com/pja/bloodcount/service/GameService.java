package com.pja.bloodcount.service;

import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.Patient;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GameService {

    private final GameRepository repository;
    private final GenerationService generationService;

    public Game createGame(Long caseId) {
        Patient patient = generationService.generatePatient(caseId);
        generationService.generateBloodCount(patient.getId(), patient.getId());

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
                .build();

        game.addPatient(patient);
        repository.save(game);
        log.info("Game is created");
        return game;
    }

    public Game completeGame(Long gameId){
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
        return game;
    }
}
