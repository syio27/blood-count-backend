package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.TokenValidationRequest;
import com.pja.bloodcount.model.Token;
import com.pja.bloodcount.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetTokenService {

    private final TokenRepository repository;

    public Token createToken(String email) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder token = new StringBuilder();

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 20; i++) {
            token.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }

        LocalDateTime expirationTime = LocalDateTime.now().plusHours(3);
        Token tokenEntity = Token
                .builder()
                .token(token.toString())
                .email(email)
                .expirationTime(expirationTime)
                .build();

        repository.save(tokenEntity);

        return tokenEntity;
    }

    public boolean validateToken(TokenValidationRequest request) {
        // Fetch the token from the database
        Token tokenEntity = repository.findByToken(request.getToken());

        // Check if the token exists
        if (tokenEntity == null) {
            return false;
        }

        // Check if the token is associated with the user's email
        if (!request.getEmail().equals(tokenEntity.getEmail())) {
            return false;
        }

        // Check if the token has expired
        if (LocalDateTime.now().isAfter(tokenEntity.getExpirationTime())) {
            return false;
        }

        // If we've made it this far, the token is valid
        return true;
    }
}

