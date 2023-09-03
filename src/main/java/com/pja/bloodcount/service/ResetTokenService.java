package com.pja.bloodcount.service;

import com.pja.bloodcount.dto.request.TokenValidationRequest;
import com.pja.bloodcount.exceptions.ResetTokenInvalidException;
import com.pja.bloodcount.model.Token;
import com.pja.bloodcount.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(2);
        Token tokenEntity = Token
                .builder()
                .token(token.toString())
                .email(email)
                .expirationTime(expirationTime)
                .build();

        repository.save(tokenEntity);

        return tokenEntity;
    }

    public void validateTokenAndThrowErrors(TokenValidationRequest request) {
        if(!this.validateToken(request)){
            throw new ResetTokenInvalidException("token invalid");
        }
    }

    public boolean validateToken(TokenValidationRequest request) {
        Token tokenEntity = repository.findByToken(request.getToken());

        if (tokenEntity == null) {
            return false;
        }

        if (!request.getEmail().equals(tokenEntity.getEmail())) {
            return false;
        }

        if (LocalDateTime.now().isAfter(tokenEntity.getExpirationTime())) {
            return false;
        }

        return true;
    }
}

