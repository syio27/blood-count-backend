package com.pja.bloodcount.exceptions;

import lombok.Getter;

import java.io.Serial;

@Getter
public class UserWithEmailNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 733414793361476581L;
    private final String email;

    public UserWithEmailNotFoundException(String email) {
        super();
        this.email = email;
    }
}
