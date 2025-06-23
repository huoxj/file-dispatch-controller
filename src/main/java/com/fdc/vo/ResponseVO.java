package com.fdc.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseVO<T> implements Serializable {

    private String code;

    private String msg;

    private T result;

    public static <T> ResponseVO<T> buildSuccess(T result) {
        return new ResponseVO<T>("200", null, result);
    }

    public static <T> ResponseVO<T> buildFailure(String msg) {
        return new ResponseVO<T>("400", msg, null);
    }

}
