package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class GroupConflictException extends RuntimeException{
    private final String groupNumber;
    public GroupConflictException(String groupNumber) {
        super();
        this.groupNumber = groupNumber;
    }
}
