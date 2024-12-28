package com.mysql.sbb.Comment;


import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
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


    public Page<Comment> getComments(SiteUser user, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        return this.commentRepository.findByAuthor(user,pageable);
    }

    public Page<Comment> getComments(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable=PageRequest.of(page,10,Sort.by(sorts));
        return this.commentRepository.findAll(pageable);
    }
}
