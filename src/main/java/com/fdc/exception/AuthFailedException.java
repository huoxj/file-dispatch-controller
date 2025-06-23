package com.fdc.exception;

import lombok.AllArgsConstructor;

public class AuthFailedException extends BusinessException{
    public AuthFailedException(String msg) {
        super("401", msg);
    }
}
