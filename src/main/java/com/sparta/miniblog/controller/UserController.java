package com.sparta.miniblog.controller;


import com.sparta.miniblog.dto.LoginRequestDto;
import com.sparta.miniblog.dto.ResponseDto;
import com.sparta.miniblog.dto.SignupRequestDto;
import com.sparta.miniblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/blog/user")
public class UserController {

    private final UserService userService;


    @ResponseBody
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto> signup(SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        return userService.login(loginRequestDto, response);
    }

}
