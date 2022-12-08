package com.sparta.miniblog.dto;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
public class ReplyRequestDto {
    private long blogId;
    private String content;

}
