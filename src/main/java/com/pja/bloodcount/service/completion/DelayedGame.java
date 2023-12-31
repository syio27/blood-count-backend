package com.pja.bloodcount.service.completion;

import com.pja.bloodcount.model.Game;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DelayedGame implements Delayed {
    @Getter
    private final Game game;
    private final long startTime;
    private final long delayTime;

    public DelayedGame(Game game, long delayTime, TimeUnit timeUnit) {
        this.game = game;
        this.startTime = System.currentTimeMillis();
        this.delayTime = TimeUnit.MILLISECONDS.convert(delayTime, timeUnit);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = (startTime + delayTime) - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }
}

