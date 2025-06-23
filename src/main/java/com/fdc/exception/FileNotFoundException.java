package com.fdc.exception;

public class FileNotFoundException extends BusinessException{
    public FileNotFoundException(String msg) {
        super("404", msg);
    }
}
