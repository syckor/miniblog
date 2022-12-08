package com.sparta.miniblog.dto;

import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.Reply;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
public class ReplyResponseDto {
    private Long id;
    private Long blogId;
    private String content;
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ReplyResponseDto(Reply reply){
        this.id = reply.getId();
        this.blogId = reply.getBlog().getId();
        this.username = reply.getUsername();
        this.content = reply.getContent();
        this.createdAt = reply.getCreatedAt();
        this.modifiedAt = reply.getModifiedAt();

    }

}
