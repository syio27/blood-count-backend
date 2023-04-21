package com.pja.bloodcount.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ResourceConflictException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1509978774214489327L;
    private final String email;
    public ResourceConflictException(String email) {
        super();
        this.email = email;
    }
}