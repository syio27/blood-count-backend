package com.pja.bloodcount.config;

import com.pja.bloodcount.service.completion.DelayedGame;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

@Configuration
public class QueueBeanConfig {

    @Bean
    public DelayQueue<DelayedGame> delayedGameQueue() {
        return new DelayQueue<>();
    }
}
