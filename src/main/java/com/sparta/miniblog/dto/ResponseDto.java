package com.sparta.miniblog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/*
* 응답상태 dto
* */
@NoArgsConstructor
@Getter
public class ResponseDto<T> {
    private String msg;
    private int statusCode;
    private T data;

    public ResponseDto(String msg, int statusCode, T data) {
        this.msg = msg;
        this.statusCode = statusCode;
        this.data = data;
    }
}
