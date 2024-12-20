package com.mysql.sbb.answer;


import com.mysql.sbb.user.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {

}