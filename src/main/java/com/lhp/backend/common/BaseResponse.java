package com.lhp.backend.common;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;
    public BaseResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public BaseResponse(String code, String message) {
        new BaseResponse(code, message, null);
    }
    public BaseResponse() {
    }


    public String getTimestamp() {
        return LocalDateTime.now().toString();
    }

}
