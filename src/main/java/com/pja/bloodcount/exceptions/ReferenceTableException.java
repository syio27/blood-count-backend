package com.pja.bloodcount.exceptions;

import lombok.Getter;

@Getter
public class ReferenceTableException extends RuntimeException{

    public ReferenceTableException(String message){
        super(message);
    }
}
