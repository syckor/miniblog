package com.sparta.miniblog.controller;


import com.sparta.miniblog.dto.*;
import com.sparta.miniblog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @GetMapping("/")
    public ModelAndView home(){
        return new ModelAndView("index");
    }

    //글쓰기
    @PostMapping("/blog")
    public ResponseEntity<BlogResponseContentDto> write(@RequestBody BlogRequestDto blogRequestDto, HttpServletRequest request){
        return blogService.createBlog(blogRequestDto, request);
    }

    //게시글 하나 조회
    @GetMapping("/blog/{id}")
    public ResponseEntity<BlogResponseContentDto> read(@PathVariable Long id){
        return blogService.getBlog(id);
    }

    //게시글 수정하기
    @PutMapping("/blog/{id}")
    public ResponseEntity<BlogResponseContentDto> updateBlog(@PathVariable Long id
                                                            , @RequestBody BlogRequestDto blogRequestDto
                                                            , HttpServletRequest request){
        return blogService.update(id, blogRequestDto, request);

    }

    //게시글 삭제하기
    @DeleteMapping("/blog/{id}")
    public ResponseEntity<BlogResponseContentDto> deleteBlog(@PathVariable Long id
                                                            , HttpServletRequest request){
        return blogService.deleteBlog(id, request);
    }

    //전체 리스트 조회
    @GetMapping("/blog")
    public ResponseEntity<BlogResponseListDto> getTest(){
        return blogService.getBlogs();
    }

}
