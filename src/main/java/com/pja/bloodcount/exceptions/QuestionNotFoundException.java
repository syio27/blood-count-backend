package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class QuestionNotFoundException extends RuntimeException {

    private final Long id;
    public QuestionNotFoundException(Long id) {
        super();
        this.id = id;
    }
}
