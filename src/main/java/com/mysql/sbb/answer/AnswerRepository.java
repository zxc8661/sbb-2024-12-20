package com.mysql.sbb.answer;


import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);

    Page<Answer> findByAuthor(SiteUser user,Pageable pageable);
    Page<Answer> findAll (Pageable pageable);
}
