package  com.mysql.sbb.question;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mysql.sbb.Category.Category;
import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.user.SiteUser;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.mysql.sbb.DataNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;


    public Page<Question> getList(int page,String kw,String category) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
        Specification<Question> spec = search(kw,category);
        return this.questionRepository.findAll(spec,pageable);
    }

    public Page<Question> getList(int page, String kw){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page,10,Sort.by(sorts));
        Specification<Question> spec = searchUserQuestion(kw);
        return this.questionRepository.findAll(spec,pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser siteUser,Category category){
        Question question = new Question();
        question.setCreateDate(LocalDateTime.now());
        question.setSubject(subject);
        question.setContent(content);
        question.setAuthor(siteUser);
        question.setCategory(category);
        this.questionRepository.save(question);
    }

   public void modify(Question question,String subject,String content){
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
   }
   public void delete (Question question){
        this.questionRepository.delete(question);
   }

   public void vote(Question question,SiteUser siteUser){
        if(question.voter.contains(siteUser)){
            question.voter.remove(siteUser);
        }else {
            question.voter.add(siteUser);
        }
        this.questionRepository.save(question);
   }

    private Specification<Question> search(String kw, String category) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                Join<Question, Category> c = q.join("category",JoinType.RIGHT);

                Predicate one=cb.or(
                        cb.like(q.get("subject"),"%" + kw + "%"),
                        cb.like(q.get("content"),"%" + kw + "%"),
                        cb.like(u1.get("username"),"%" + kw + "%"),
                        cb.like(u2.get("username"),"%" + kw + "%"),
                        cb.like(a.get("content"), "%" + kw + "%")
                );

                Predicate two=cb.like(c.get("category"),"%" + category+"%");

                return cb.and(one,two);
            }
        };
    }

    private Specification<Question> searchUserQuestion(String kw){
        return new Specification<>(){
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb){
                query.distinct(true);
                Join<Question,SiteUser> u1 = q.join("author",JoinType.LEFT);

                Predicate one=cb.or(
                        cb.like(u1.get("username"),"%"+kw+"%")
                );

                return cb.and(one);
            }
        };
    }

}