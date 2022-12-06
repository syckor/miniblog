package com.sparta.miniblog.entity;


import com.sparta.miniblog.dto.BlogRequestDto;
import com.sparta.miniblog.repository.BlogRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Blog extends TimeStamped{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
/*
    @Column(nullable = false)
    private String username;
*/
    /*
        연관관계의 주인은 mappedby 를 사용하지 않는다.
        연관관계의 주인은 외래키가 있는 쪽이 주인이 된다.
    */
    @ManyToOne
    @JoinColumn(name = "USER_USERNAME", nullable = false)
    private User user;

    public Blog(BlogRequestDto blogRequestDto, User user){

        this.title = blogRequestDto.getTitle();
        this.content = blogRequestDto.getContent();
        this.user = user;
    }

    public void update(BlogRequestDto blogRequestDto) {
        this.title = blogRequestDto.getTitle();
        this.content = blogRequestDto.getContent();
    }
}
