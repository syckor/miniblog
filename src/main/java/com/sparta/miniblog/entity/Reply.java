package com.sparta.miniblog.entity;


import com.sparta.miniblog.dto.ReplyRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Reply extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "BLOG_ID", nullable = false)
    private Blog blog;

    public Reply(ReplyRequestDto replyRequestDto, Blog blog, String username){
        this.blog = blog;
        content = replyRequestDto.getContent();
        this.username = username;
    }


    public void update(ReplyRequestDto replyRequestDto) {
        this.content = replyRequestDto.getContent();
    }
}
