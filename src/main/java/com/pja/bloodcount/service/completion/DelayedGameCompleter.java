package com.pja.bloodcount.service.completion;

import com.pja.bloodcount.exceptions.GameCompleteException;
import com.pja.bloodcount.exceptions.GameNotFoundException;
import com.pja.bloodcount.service.contract.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;

@Service
@Slf4j
public class DelayedGameCompleter implements Runnable {

    private final DelayQueue<DelayedGame> delayQueue;
    private final GameService gameService;

    @Autowired
    public DelayedGameCompleter(DelayQueue<DelayedGame> delayQueue, GameService gameService) {
        this.delayQueue = delayQueue;
        this.gameService = gameService;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DelayedGame delayedGame = delayQueue.take();
                gameService.queueCompleteGame(delayedGame.getGame().getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            catch (GameCompleteException | GameNotFoundException e) {
                log.info("Delayed game was completed already, removing it from queue");
            }
        }
    }

    @PostConstruct
    public void init() {
        new Thread(this).start();
    }
}

