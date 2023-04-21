package com.pja.bloodcount.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserWithEmailNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 733414793361476581L;
    private final String email;

    public UserWithEmailNotFoundException(String email) {
        super();
        this.email = email;
    }
}
