package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class UserNotAllowedException extends RuntimeException{

    public UserNotAllowedException(String message){
        super(message);
    }
}
