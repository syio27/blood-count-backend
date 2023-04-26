package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class IncorrectOldPasswordException extends RuntimeException{
    public IncorrectOldPasswordException(String message) {
        super(message);
    }
}
