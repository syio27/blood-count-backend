package com.pja.bloodcount.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.UUID;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 733414793361476184L;
    private final UUID id;
    private final String email;

    public UserNotFoundException(UUID id) {
        super(String.format("DOEST EXIST! - User with id %s not found", id));
        this.id = id;
        this.email = null;
    }

    public UserNotFoundException(String email) {
        super(String.format("DOEST EXIST! - User with email %s not found", email));
        this.email = email;
        this.id = null;
    }
}

