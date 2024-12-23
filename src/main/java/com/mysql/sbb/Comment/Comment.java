package com.mysql.sbb.Comment;


import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private SiteUser author;

    @ManyToOne
    private Answer answer;

    @ManyToOne
    private Question question;
}
