package com.pja.bloodcount.exceptions;

import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

@Getter
public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 733414793361476184L;
    private final UUID id;

    public UserNotFoundException(UUID id) {
        super();
        this.id = id;
    }
}

