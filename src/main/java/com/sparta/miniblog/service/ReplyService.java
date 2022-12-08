package com.sparta.miniblog.service;


import com.sparta.miniblog.dto.*;
import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.Reply;
import com.sparta.miniblog.entity.User;
import com.sparta.miniblog.entity.UserRoleEnum;
import com.sparta.miniblog.jwt.JwtUtil;
import com.sparta.miniblog.repository.ReplyRepository;
import com.sparta.miniblog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final BlogService blogService;
    private final ReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /*
    * 댓글 달기
    * */
    @Transactional
    public ResponseEntity<ResponseDto> createReply(ReplyRequestDto replyRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 토큰이 있는 경우에만 글쓰기 가능
        ResponseDto<?> responseDto = new ResponseDto<>("인증되지 않은 사용자", 400, null);
        HttpStatus httpStatus = HttpStatus.valueOf(400);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
                // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
                Optional<User> user = userRepository.findByUsername(claims.getSubject());
                if (!user.isEmpty()) {
                    Optional<Blog> blog = blogService.checkBlog(replyRequestDto.getBlogId());//글이 있는지 체크
                    if (!blog.isEmpty()) {
                        Reply reply = new Reply(replyRequestDto, blog.get(), user.get().getUsername());
                        replyRepository.save(reply);
                        responseDto = new ResponseDto<>("댓글 달기 성공", 200, new ReplyResponseDto(reply));
                    }else{
                        responseDto = new ResponseDto<>("해당 글을 찾을 수 없습니다", 400, null);
                        httpStatus = HttpStatus.valueOf(400);
                    }
                }
            }

        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    /*
    * 댓글 수정*/
    @Transactional
    public ResponseEntity<ResponseDto> update(Long id, ReplyRequestDto replyRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        ResponseDto<?> responseDto;
        HttpStatus httpStatus;
        responseDto = new ResponseDto<>("Token Error", 400, "사용자를 인증할 수 없습니다.");
        httpStatus = HttpStatus.valueOf(400);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
                // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
                Optional<User> user = userRepository.findByUsername(claims.getSubject());

                if (!user.isEmpty()) {
                    Optional<Reply> reply = replyRepository.findById(id);
                    if (!reply.isEmpty()) {
                        //블로그의 글쓴이와 현재 로그인한 사람의 값을 비교
                        if (reply.get().getUsername().equals(user.get().getUsername())) {
                            reply.get().update(replyRequestDto); //업데이트 하기
                            responseDto = new ResponseDto<>("댓글 수정 성공", 200, new ReplyResponseDto(reply.get()));
                            httpStatus = HttpStatus.valueOf(200);

                        } else {
                            responseDto = new ResponseDto<>("본인의 댓글만 수정 가능합니다.", 400, null);
                        }
                    }else{
                        responseDto = new ResponseDto<>("해당 댓글을 찾을 수 없습니다", 400, null);
                    }
                }
            }
        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    /*
    * 댓글 삭제
    * */
    @Transactional
    public ResponseEntity<ResponseDto> delete(Long id, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        ResponseDto<?> responseDto;
        HttpStatus httpStatus;
        responseDto = new ResponseDto<>("Token Error", 400, "사용자를 인증할 수 없습니다.");
        httpStatus = HttpStatus.valueOf(400);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
                // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
                Optional<User> user = userRepository.findByUsername(claims.getSubject());

                if (!user.isEmpty()) {
                    Optional<Reply> reply = replyRepository.findById(id);
                    if (!reply.isEmpty()) {
                        //블로그의 글쓴이와 현재 로그인한 사람의 값을 비교
                        if (user.get().getRole().equals(UserRoleEnum.ADMIN/*관리자 체크*/)
                                ||reply.get().getUsername().equals(user.get().getUsername())) {
                            replyRepository.deleteById(id);
                            responseDto = new ResponseDto<>("댓글 삭제 성공", 200, new ReplyResponseDto(reply.get()));
                            httpStatus = HttpStatus.valueOf(200);

                        } else {
                            responseDto = new ResponseDto<>("본인의 댓글만 삭제 가능합니다.", 400, null);
                        }
                    }else{
                        responseDto = new ResponseDto<>("해당 댓글을 찾을 수 없습니다", 400, null);
                    }
                }
            }
        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }
}
