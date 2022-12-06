package com.sparta.miniblog.dto;

import lombok.Getter;

@Getter
public class BlogRequestDto {

    private Long id;
    private String username;
    private String title;
    private String content;

}
