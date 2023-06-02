package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class GroupNotFoundException extends RuntimeException {

    private final String groupNumber;
    public GroupNotFoundException(String groupNumber) {
        super();
        this.groupNumber = groupNumber;
    }
}
