package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class ResetTokenInvalidException extends RuntimeException {
    public ResetTokenInvalidException(String message){
        super(message);
    }
}
