package com.fdc.exception;

public class FileIOException extends BusinessException {
    public FileIOException(String message) {
        super("500", message);
    }
}
