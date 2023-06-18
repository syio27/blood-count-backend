package com.pja.bloodcount.exceptions;

public class GameCompleteException extends RuntimeException {
    public GameCompleteException(String message) {
        super(message);
    }
}
