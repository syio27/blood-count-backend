package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class PasswordValidationException extends RuntimeException{
    public PasswordValidationException(String message) {
        super(message);
    }
}
