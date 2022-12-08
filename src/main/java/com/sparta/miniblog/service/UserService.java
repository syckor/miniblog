package com.sparta.miniblog.service;


import com.sparta.miniblog.dto.LoginRequestDto;
import com.sparta.miniblog.dto.ResponseDto;
import com.sparta.miniblog.dto.SignupRequestDto;
import com.sparta.miniblog.entity.User;
import com.sparta.miniblog.entity.UserRoleEnum;
import com.sparta.miniblog.jwt.JwtUtil;
import com.sparta.miniblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    // ADMIN_TOKEN
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ResponseEntity<ResponseDto> signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = signupRequestDto.getPassword();
        HttpStatus httpStatus;
        ResponseDto responseDto;

        Pattern usernamePattern = Pattern.compile("^[a-z0-9]{4,10}$");//username형식
        Pattern passwordPattern = Pattern.compile("^[a-zA-Z0-9]{11,30}$");//password형식

        if(!usernamePattern.matcher(username).matches()){ //username형식이 맞지 않을 때 실행
            httpStatus= HttpStatus.valueOf(400);
            responseDto = new ResponseDto("username은 4~10자릿수 소문자나 숫자입니다.", 400, null);
        }else if(!passwordPattern.matcher(password).matches()){ //password형식이 맞지 않을 때 실행
            httpStatus= HttpStatus.valueOf(400);
            responseDto = new ResponseDto("password는 11~30자릿수 영어나 숫자입니다.", 400, null);
        }else{ //username과 password형식이 모두 만족할 때 실행

            httpStatus= HttpStatus.valueOf(400);
            responseDto = new ResponseDto("중복된 사용자가 존재합니다.", 400, null);

            // 회원 중복 확인
            Optional<User> found = userRepository.findByUsername(username);

            if (!found.isPresent()) { //중복된 사용자가 존재하지 않을 때 실행(모든 조건을 만족할 때)
                UserRoleEnum role = UserRoleEnum.USER;
                if(signupRequestDto.isAdmin()) {
                    if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                        httpStatus = HttpStatus.valueOf(400);
                        responseDto = new ResponseDto("관리자 권한이 없습니다.", 400, null);
                        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
                    }
                    role = UserRoleEnum.ADMIN;
                }
                User user = new User(username, password,role);
                httpStatus = HttpStatus.valueOf(200);
                responseDto = new ResponseDto("회원 가입 완료", 200, null);

                userRepository.save(user);
            }

        }

        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);

    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseDto> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
//        User user = userRepository.findByUsername(username).orElseThrow(
//                () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
//        ); //isPresent??
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()){
            HttpStatus httpStatus = httpStatus = HttpStatus.valueOf(400);
            ResponseDto responseDto = new ResponseDto("존재하는 회원이 아닙니다.", 400, username);
            return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
        }
        // 비밀번호 확인
        if(!user.get().getPassword().equals(password)){
            HttpStatus httpStatus = httpStatus = HttpStatus.valueOf(400);
            ResponseDto responseDto = new ResponseDto("비밀번호 불일치", 400, null);
            return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
        }

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(user.get().getUsername()));
        HttpStatus httpStatus = HttpStatus.valueOf(200);;
        ResponseDto responseDto = new ResponseDto("로그인 성공~", 200, username);
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

}
