package com.pja.bloodcount.repository;

import com.pja.bloodcount.model.Token;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TokenRepository extends CrudRepository<Token, UUID> {
    Token findByToken(String token);
}

