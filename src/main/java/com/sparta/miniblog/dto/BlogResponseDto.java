package com.sparta.miniblog.dto;

import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.Reply;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
* 블로그의 내용을 담은 dto
* */
@Getter
public class BlogResponseDto{
    private Long id;
    //private String username;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private List<ReplyResponseDto> replies;


    public BlogResponseDto(Blog blog){
        this.id = blog.getId();
        this.username = blog.getUser().getUsername();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.createdAt = blog.getCreatedAt();
        this.modifiedAt = blog.getModifiedAt();
        this.replies = blog.getReplies().stream().map(x -> new ReplyResponseDto(x)).collect(Collectors.toList());
    }
}
