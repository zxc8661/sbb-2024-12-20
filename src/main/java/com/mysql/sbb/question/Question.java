package com.mysql.sbb.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysql.sbb.Category.Category;
import com.mysql.sbb.Comment.Comment;
import com.mysql.sbb.answer.Answer;

import com.mysql.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Comment> comments;


    @ManyToOne
    private Category category;

    private Integer viewCount =0;
}