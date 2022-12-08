package com.sparta.miniblog.entity;


import com.sparta.miniblog.dto.BlogRequestDto;
import com.sparta.miniblog.repository.BlogRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "USER_USERNAME", nullable = false)
    private User user;


    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE)
    @OrderBy("modifiedAt desc")
    private List<Reply> replies;


    public Blog(BlogRequestDto blogRequestDto, User user){

        this.title = blogRequestDto.getTitle();
        this.content = blogRequestDto.getContent();
        this.user = user;
        this.replies = new ArrayList<>();
    }

    public void update(BlogRequestDto blogRequestDto) {
        this.title = blogRequestDto.getTitle();
        this.content = blogRequestDto.getContent();
    }
}
