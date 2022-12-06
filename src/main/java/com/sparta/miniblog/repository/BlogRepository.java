package com.sparta.miniblog.repository;

import com.sparta.miniblog.dto.BlogResponseDto;
import com.sparta.miniblog.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByOrderByModifiedAtDesc();
    Optional<Blog> findByIdAndUserUsername(Long id, String username);
}
