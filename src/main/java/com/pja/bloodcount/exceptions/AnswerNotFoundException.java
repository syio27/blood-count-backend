package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class AnswerNotFoundException extends RuntimeException {

    private final Long id;
    public AnswerNotFoundException(Long id) {
        super();
        this.id = id;
    }
}
