package com.fdc.exception;

public class AuthFailedException extends BusinessException{
    public AuthFailedException(String msg) {
        super("401", msg);
    }
}
