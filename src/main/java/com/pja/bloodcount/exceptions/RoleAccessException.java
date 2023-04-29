package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class RoleAccessException extends RuntimeException{
    public RoleAccessException(String message){
        super(message);
    }
}
