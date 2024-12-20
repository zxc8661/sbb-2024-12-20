package com.mysql.sbb.answer;


import com.mysql.sbb.DataNotFoundException;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer  create(Question question,String content,SiteUser  author){
        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setContent(content);
        answer.setAuthor(author);
        answer.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(answer);
        return answer;

    }



    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer){
        this.answerRepository.delete(answer);
    }


    public void vote(Answer answer,SiteUser siteUser) {
        if(answer.voter.contains(siteUser)){
            answer.voter.remove(siteUser);
        }else{
            answer.voter.add(siteUser);
        }
        this.answerRepository.save(answer);
    }
}