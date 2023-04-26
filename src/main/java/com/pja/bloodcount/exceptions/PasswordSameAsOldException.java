package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class PasswordSameAsOldException extends RuntimeException{
    public PasswordSameAsOldException(String message) {
        super(message);
    }
}
