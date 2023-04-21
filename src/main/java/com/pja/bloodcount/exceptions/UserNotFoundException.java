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

    public UserNotFoundException(UUID id) {
        super();
        this.id = id;
    }
}

