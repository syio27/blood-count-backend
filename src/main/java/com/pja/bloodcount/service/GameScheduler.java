package com.pja.bloodcount.service;

import com.pja.bloodcount.model.Game;
import com.pja.bloodcount.model.enums.Status;
import com.pja.bloodcount.repository.GameRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class GameScheduler {

    private final GameRepository gameRepository;

    @Scheduled(fixedRate = 60000)
    public void endCompletedGames() {
        log.info("scheduler run");
        Instant now = Instant.now();
        List<Game> gamesToBeCompleted = gameRepository.findAllByEstimatedEndTimeBeforeAndStatus(Date.from(now), Status.IN_PROGRESS);
        List<Game> completedGames = new ArrayList<>();
        gamesToBeCompleted.forEach(game -> {
            game.setScore(0);
            game.setEndTime(Date.from(now));
            game.setStatus(Status.COMPLETED);
            completedGames.add(game);
        });
        gameRepository.saveAll(completedGames);
        log.info("scheduler complete");
    }
}
