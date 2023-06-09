package com.pja.bloodcount.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class InvalidCredentialsException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = -1509978774214489227L;
    private final String message;

    public InvalidCredentialsException(String message) {
        super();
        this.message = message;
    }
}
