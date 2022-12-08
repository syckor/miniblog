package com.sparta.miniblog.service;

import com.sparta.miniblog.dto.*;
import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.Reply;
import com.sparta.miniblog.entity.User;
import com.sparta.miniblog.entity.UserRoleEnum;
import com.sparta.miniblog.jwt.JwtUtil;
import com.sparta.miniblog.repository.BlogRepository;
import com.sparta.miniblog.repository.ReplyRepository;
import com.sparta.miniblog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    private final ReplyRepository replyRepository;
    private final JwtUtil jwtUtil;

    /*
     * 블로그 쓰기
     * */
    @Transactional
    public ResponseEntity<ResponseDto> createBlog(BlogRequestDto blogRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        ResponseDto<?> responseDto;
        HttpStatus httpStatus;
        responseDto = new ResponseDto<>("Token Error", 400, "사용자를 인증할 수 없습니다.");
        // 토큰이 있는 경우에만 글쓰기 가능
        //BlogResponseContentDto res = new BlogResponseContentDto("인증되지 않은 사용자", 400);
        httpStatus = HttpStatus.valueOf(400);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
                // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
                Optional<User> user = userRepository.findByUsername(claims.getSubject());
                if (!user.isEmpty()) {
                    Blog blog = new Blog(blogRequestDto, user.get());
                    // 요청받은 DTO 로 DB에 저장할 객체 만들기
                    blogRepository.save(blog);
                    responseDto = new ResponseDto<>("글쓰기 성공", 200, new BlogResponseDto(blog));
                    httpStatus = HttpStatus.valueOf(200);
                }
            }
        }

        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    /*
     * 블로그 전체 보기
     * */
    @Transactional
    public ResponseEntity<ResponseDto> getBlogs() {

        List<Blog> list = blogRepository.findAllByOrderByModifiedAtDesc();

        List<BlogResponseDto> blogList = new ArrayList<>();
        for (Blog blog : list) {
            //blogResponseList.addBlogList(new BlogResponseDto(blog));
            //List<Reply> replies = replyRepository.findAllByBlogId(blog.getId());
            blogList.add(new BlogResponseDto(blog));
        }
        ResponseDto<List<BlogResponseDto>> responseDto = new ResponseDto<>("전체조회", 200, blogList);
        HttpStatus httpStatus = HttpStatus.valueOf(200);
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    /*
     * 블로그 수정하기
     * */
    @Transactional
    public ResponseEntity<ResponseDto> update(Long id, BlogRequestDto blogRequestDto, HttpServletRequest request) {

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

                    //Optional<Blog> blog = blogRepository.findByIdAndUserUsername(id, user.get().getUsername());
                    /*findByIdAndUserUsername으로 조회했을 때 Id가 문제인지 Username(현재 로그인한 사람)이 문제인지 모름
                      그래서 일단 Id로 검색해 와서 해당 글쓴이와 현재 로그인한 유저의 값을 비교함
                      하지만 이렇게 하니까 코드가 너무 길어짐 ㅜ
                    * */

                    Optional<Blog> blog = blogRepository.findById(id);
                    if (!blog.isEmpty()) {


                        //관리자이거나 블로그의 글쓴이와 현재 로그인한 사람의 값을 비교
                        if (user.get().getRole().equals(UserRoleEnum.ADMIN/*관리자 체크*/)
                                || blog.get().getUser().getUsername().equals(user.get().getUsername())/*글쓴이와 로그인 유저 비교*/ ) {
                            blog.get().update(blogRequestDto); //업데이트 하기
                            responseDto = new ResponseDto<>("글 수정 성공", 200, new BlogResponseDto(blog.get()));
                            httpStatus = HttpStatus.valueOf(200);

                        } else {
                            responseDto = new ResponseDto<>("본인의 글만 수정 가능합니다.", 400, null);
                        }

                    }else{
                        responseDto = new ResponseDto<>("해당 글을 찾을 수 없습니다", 400, null);
                    }
                }
            }
        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }
    //게시글 삭제
    @Transactional
    public ResponseEntity<ResponseDto> deleteBlog(Long id, HttpServletRequest request) {

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

                    Optional<Blog> blog = blogRepository.findById(id);
                    if (!blog.isEmpty()) {


                        //블로그의 글쓴이와 현재 로그인한 사람의 값을 비교
                        if (user.get().getRole().equals(UserRoleEnum.ADMIN/*관리자 체크*/)
                                ||blog.get().getUser().getUsername().equals(user.get().getUsername())) {
                            blogRepository.deleteById(id);
                            responseDto = new ResponseDto<>("글 삭제 성공", 200, "글 삭제 성공");
                            httpStatus = HttpStatus.valueOf(200);

                        } else {
                            responseDto = new ResponseDto<>("본인의 글만 삭제 가능합니다", 400, null);
                        }
                    }else{
                        responseDto = new ResponseDto<>("해당 게시글을 찾을 수 없습니다.", 400, null);
                    }
                }
            }
        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    //게시글 하나 조회
    @Transactional
    public ResponseEntity<ResponseDto> getBlog(Long id) {
        HttpStatus httpStatus;
        ResponseDto<Blog> responseDto;
        Optional<Blog> blog = checkBlog(id);

        if(!blog.isEmpty()) {
            responseDto = new ResponseDto("조회 성공", 200, new BlogResponseDto(blog.get()));
            httpStatus = HttpStatus.valueOf(200);
        }else{
            responseDto = new ResponseDto("해당 글을 찾을 수 없습니다.", 200, null);
            httpStatus = HttpStatus.valueOf(400);
        }
        return new ResponseEntity<ResponseDto>(responseDto, httpStatus);
    }

    public Optional<Blog> checkBlog(Long id) {
        return blogRepository.findById(id);
    }
}
