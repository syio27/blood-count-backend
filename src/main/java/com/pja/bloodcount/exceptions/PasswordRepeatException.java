package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class PasswordRepeatException extends RuntimeException{
    public PasswordRepeatException(String message) {
        super(message);
    }
}
