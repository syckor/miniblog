package com.sparta.miniblog.repository;

import com.sparta.miniblog.entity.Blog;
import com.sparta.miniblog.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository  extends JpaRepository<Reply, Long> {

    List<Reply> findAllByBlogId(Long blogId);

}
