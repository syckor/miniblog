package com.sparta.miniblog.dto;

import com.sparta.miniblog.entity.Blog;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/*
* 블로그 전체의 내용을 담은 dto
* */
@Getter
public class BlogResponseListDto extends ResponseDto{

    List<BlogResponseDto> blogList = new ArrayList<BlogResponseDto>();

    public BlogResponseListDto(String msg, int statusCode){
        super(msg, statusCode);
    }

    public void addBlogList(BlogResponseDto blogResponseDto) {
        blogList.add(blogResponseDto);
    }
}
