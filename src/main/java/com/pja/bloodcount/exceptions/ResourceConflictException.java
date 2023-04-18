package com.pja.bloodcount.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class ResourceConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1509978774214489327L;
    private final String email;
    public ResourceConflictException(String email) {
        super(String.format("CONFLICT! - User with email %s already exists", email));
        this.email = email;
    }
}