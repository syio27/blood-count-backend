package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class PatientNotFoundException extends RuntimeException {

    private final Long id;

    public PatientNotFoundException(Long id) {
        super();
        this.id = id;
    }
}
