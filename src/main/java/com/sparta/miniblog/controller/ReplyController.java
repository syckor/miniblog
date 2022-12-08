package com.sparta.miniblog.controller;

import com.sparta.miniblog.dto.*;
import com.sparta.miniblog.service.BlogService;
import com.sparta.miniblog.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class ReplyController {
    private final ReplyService replyService;

    //댓글쓰기
    @PostMapping("/reply")
    public ResponseEntity<ResponseDto> writeReply(@RequestBody ReplyRequestDto replyRequestDto, HttpServletRequest request){
        return replyService.createReply(replyRequestDto, request);
    }
    @PutMapping("/reply/{id}")
    public ResponseEntity<ResponseDto> updateBlog(@PathVariable Long id
            , @RequestBody ReplyRequestDto replyRequestDto
            , HttpServletRequest request){
        return replyService.update(id, replyRequestDto, request);

    }

    @DeleteMapping("/reply/{id}")
    public ResponseEntity<ResponseDto> deleteBlog(@PathVariable Long id
            , HttpServletRequest request){
        return replyService.delete(id, request);
    }
}

