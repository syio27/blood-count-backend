package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class PatientBloodCountConflictException extends RuntimeException {
    private final Long id;

    public PatientBloodCountConflictException(Long id) {
        super();
        this.id = id;
    }
}
