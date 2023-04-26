package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class EmailValidationException extends RuntimeException{
    public EmailValidationException(String message) {
        super(message);
    }
}
