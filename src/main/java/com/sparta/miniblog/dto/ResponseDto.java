package com.sparta.miniblog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/*
* 응답상태 dto
* */
@NoArgsConstructor
@Getter
public class ResponseDto {
    private String msg;
    private int statusCode;



    public ResponseDto(String msg, int statusCode) {
        this.msg = msg;
        this.statusCode = statusCode;
    }
}
