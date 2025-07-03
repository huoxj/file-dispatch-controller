package com.fdc.controller;

import com.fdc.exception.BusinessException;
import com.fdc.vo.ResponseVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseVO<Void> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return new ResponseVO<>("500", "Internal Server Error", null);
    }

    @ExceptionHandler(value = BusinessException.class)
    public ResponseVO<Void> handleBusinessException(BusinessException e) {
        e.printStackTrace();
        return new ResponseVO<>(e.getCode(), e.getMessage(), null);
    }
}
