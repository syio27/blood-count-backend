package com.pja.bloodcount.exceptions;

public class LanguageNotSupportedException extends RuntimeException {
    public LanguageNotSupportedException(String message) {
        super(message);
    }
}
