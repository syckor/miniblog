package com.sparta.miniblog.service;

import com.sparta.miniblog.dto.*;
import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.User;
import com.sparta.miniblog.jwt.JwtUtil;
import com.sparta.miniblog.repository.BlogRepository;
import com.sparta.miniblog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /*
    * 블로그 쓰기
    * */
    @Transactional
    public ResponseEntity<BlogResponseContentDto> createBlog(BlogRequestDto blogRequestDto, HttpServletRequest request) {
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 토큰이 있는 경우에만 글쓰기 가능
        BlogResponseContentDto res = new BlogResponseContentDto("인증되지 않은 사용자", 400);
        HttpStatus httpStatus = HttpStatus.valueOf(201);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );

            Blog blog = new Blog(blogRequestDto, user);

            // 요청받은 DTO 로 DB에 저장할 객체 만들기
            blogRepository.save(blog);
            res = new BlogResponseContentDto("글쓰기 성공", 201);
            res.resultBlog(blog);
            return new ResponseEntity<BlogResponseContentDto>(res,httpStatus);
        }

        return new ResponseEntity<BlogResponseContentDto>(res,httpStatus);
    }

    /*
     * 블로그 전체 보기
     * */
    @Transactional
    public ResponseEntity<BlogResponseListDto> getBlogs() {

        List<Blog> list = blogRepository.findAllByOrderByModifiedAtDesc();
        BlogResponseListDto blogResponseList = new BlogResponseListDto("게시글 목록 조회 성공", 200);
        for (Blog blog : list) {
            blogResponseList.addBlogList(new BlogResponseDto(blog));
        }
        HttpStatus httpStatus = HttpStatus.valueOf(200);
        return new ResponseEntity<BlogResponseListDto>(blogResponseList,httpStatus);
    }

    /*
     * 블로그 수정하기
     * */
    @Transactional
    public ResponseEntity<BlogResponseContentDto> update(Long id, BlogRequestDto blogRequestDto, HttpServletRequest request) {

        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 토큰이 있는 경우에만 글쓰기 가능
        BlogResponseContentDto res = new BlogResponseContentDto("인증되지 않은 사용자", 400);
        HttpStatus httpStatus = HttpStatus.valueOf(201);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Blog blog = blogRepository.findByIdAndUserUsername(id, user.getUsername()).orElseThrow(
                    () -> new NullPointerException("해당 사용자가 쓴 글이 아니거나 존재하지 않습니다.")
            );

            blog.update(blogRequestDto);
            res = new BlogResponseContentDto("글 수정 완료", 201);
            res.resultBlog(blog);
            httpStatus = HttpStatus.valueOf(201);
        }

        return new ResponseEntity<BlogResponseContentDto>(res,httpStatus);
    }

    //게시글 삭제
    @Transactional
    public ResponseEntity<BlogResponseContentDto> deleteBlog(Long id, HttpServletRequest request){
        // Request에서 Token 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        // 토큰이 있는 경우에만 글쓰기 가능
        BlogResponseContentDto res = new BlogResponseContentDto("인증되지 않은 사용자", 400);
        HttpStatus httpStatus = HttpStatus.valueOf(201);
        if (token != null) {
            if (jwtUtil.validateToken(token)) {
                // 토큰에서 사용자 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            } else {
                throw new IllegalArgumentException("Token Error");
            }

            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
            User user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
            );
            Blog blog = blogRepository.findByIdAndUserUsername(id, user.getUsername()).orElseThrow(
                    () -> new NullPointerException("해당 사용자가 쓴 글이 아니거나 존재하지 않습니다.")
            );

            blogRepository.deleteById(id);
            res = new BlogResponseContentDto("글 삭제 완료", 201);
            res.resultBlog(blog);
            httpStatus = HttpStatus.valueOf(201);
        }
        return new ResponseEntity<BlogResponseContentDto>(res, httpStatus);
    }

    //게시글 하나 조회
    @Transactional
    public ResponseEntity<BlogResponseContentDto> getBlog(Long id) {


        Blog blog = checkBlog(id);

        System.out.println("blog에서 가져온 유저 네임~~~~ :::::::::::: " + blog.getUser().getUsername());
        BlogResponseContentDto res = new BlogResponseContentDto("블로그 조회 완료", 200);
        res.resultBlog(blog);
        HttpStatus httpStatus = HttpStatus.valueOf(200);
        return new ResponseEntity<BlogResponseContentDto>(res,httpStatus);
    }

    public Blog checkBlog(Long id){
        return blogRepository.findById(id).orElseThrow(
                ()->new IllegalArgumentException("해당 글이 존재하지 않아요")
                //() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없음", new IllegalArgumentException())
        );
    }

//    public User verifyToken(HttpServletRequest request){
//        // Request에서 Token 가져오기
//        String token = jwtUtil.resolveToken(request);
//        Claims claims;
//        User user;
//        //Optional<User> op = Optional.ofNullable(user);
//        if (token != null) { //토큰이 존재할 때
//            if (jwtUtil.validateToken(token)) {
//                // 토큰에서 사용자 정보 가져오기
//                claims = jwtUtil.getUserInfoFromToken(token);
//            } else {
//                throw new IllegalArgumentException("Token Error");
//            }
//
//            // 토큰에서 가져온 사용자 정보를 사용하여 DB 조회
//            user = userRepository.findByUsername(claims.getSubject()).orElseThrow(
//                    () -> new IllegalArgumentException("사용자가 존재하지 않습니다.")
//            );
//            //op = Optional.ofNullable(user);
//        }
//        return user;
//    }
}
