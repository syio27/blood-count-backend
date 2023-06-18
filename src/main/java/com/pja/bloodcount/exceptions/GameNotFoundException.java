package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class GameNotFoundException extends RuntimeException {

    private final Long id;
    public GameNotFoundException(Long id) {
        super();
        this.id = id;
    }
}
