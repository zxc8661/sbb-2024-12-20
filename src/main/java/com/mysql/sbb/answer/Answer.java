package com.mysql.sbb.answer;

import com.mysql.sbb.Comment.Comment;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.patterns.TypePatternQuestions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Entity
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;


    @ManyToMany
    Set<SiteUser> voter;

    private Integer voteCount=0;

    @OneToMany(mappedBy = "answer",cascade = CascadeType.REMOVE)
    private List<Comment> comments;


}