package com.mysql.sbb.answer;


import com.mysql.sbb.DataNotFoundException;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        answer.setVoteCount(answer.getVoter().size());
        this.answerRepository.save(answer);
    }


    public Page<Answer> getList(Question question,int page){
        List<Sort.Order> sorts=new ArrayList<>();
        sorts.add(Sort.Order.desc("voteCount"));
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        return this.answerRepository.findByQuestion(question,pageable);
    }

    public Page<Answer> getList(SiteUser user, int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        return this.answerRepository.findByAuthor(user,pageable);
    }

    public Page<Answer> getList(int page){
        List<Sort.Order>sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        return this.answerRepository.findAll(pageable);
    }
}