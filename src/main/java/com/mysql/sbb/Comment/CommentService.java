package com.mysql.sbb.Comment;


import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comments;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create(String content, SiteUser siteUser, Answer answer, Question question) {
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(siteUser);

        if (answer == null) {
            comment.setQuestion(question);
        } else {
            comment.setAnswer(answer);
        }

        return this.commentRepository.save(comment); // 데이터 저장
    }

    public void update(Comment comment, String content){
        comment.setContent(content);
        this.commentRepository.save(comment);
    }

    public List<Comment> getComments(){
        List<Comment> comments = this.commentRepository.findAll();
        return comments;
    }

    public void deleteComment(Comment comment){
        this.commentRepository.delete(comment);
    }



}
