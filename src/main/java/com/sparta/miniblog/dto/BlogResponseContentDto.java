package com.sparta.miniblog.dto;

import com.sparta.miniblog.entity.Blog;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/*
* 블로그 컨텐츠 하나의 내용을 반환하는 dto
* */
@Getter
public class BlogResponseContentDto extends ResponseDto{
    private BlogResponseDto blog;
    public BlogResponseContentDto(String msg, int statusCode){
        super(msg, statusCode);
    }

    public void resultBlog(Blog blog){
        this.blog = new BlogResponseDto(blog);

    }
}
