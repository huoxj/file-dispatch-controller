package com.fdc.exception;

public class FSException extends BusinessException{
    public FSException(String msg) {
        super("500", msg);
    }
}
