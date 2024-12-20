package com.mysql.sbb;


import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.answer.AnswerRepository;
import com.mysql.sbb.answer.AnswerService;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.question.QuestionRepository;
import com.mysql.sbb.question.QuestionService;
import com.mysql.sbb.user.SiteUser;
import com.mysql.sbb.user.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class SbbApplicationTests{
	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;
	@Autowired
	private AnswerService answerService;

	@Autowired
	private QuestionService questionService;
	@Autowired
	private UserService userService;

	@Test
	public void creteTestData(){
		Question question = this.questionService.getQuestion(312);
		System.out.println(question.getSubject());
				SiteUser user= this.userService.getUser("테스트2");
		for(int i=1;i<301;i++){
		 	this.answerService.create(question,"테스트"+i,user);

		}
	}

	@Test
	public void create(){
		Question q1 = new Question();
		q1.setCreateDate(LocalDateTime.now());
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		this.questionRepository.save(q1);


		Question q2 = new Question();
		q2.setContent("id는 자동으로 생성되나요?");
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);
	}

	@Test
	@DisplayName("findAll")
	public void findAll(){
		List<Question> q= this.questionRepository.findAll();
		assertEquals(2,q.size());

		Question question = q.get(0);
		assertEquals("sbb가 무엇인가요?",question.getSubject());
	}

	@Test
	public void findById(){
		Optional<Question> o = this.questionRepository.findById(1);
		if(o.isPresent()){
			Question question = o.get();
			assertEquals("sbb가 무엇인가요?", question.getSubject());
		}
	}

	@Test
	public void findBySubject(){
		Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
		assertEquals(1,q.getId());
	}

	@Test
	public void findBySubjectAndContent(){
		Question q = this.questionRepository.findBySubjectAndContent(
				"sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."
		);
		assertEquals(1,q.getId());
	}

	@Test
	public void findBySubjectLike(){
		List<Question> q = this.questionRepository.findBySubjectLike("%sbb%");
		assertEquals(1,q.get(0).getId());
	}


	@Test
	public void modify1(){
		Optional<Question> o= this.questionRepository.findById(1);
		Question q = o.get();
		q.setSubject("수정해 봤음");
		this.questionRepository.save(q);
	}

	@Test
	public void delete(){
		Optional<Question> o = this.questionRepository.findById(1);
		Question q = o.get();
		this.questionRepository.delete(q);
		assertEquals(1,this.questionRepository.count());
	}

	@Test
	public void answerCreate(){
		Optional<Question> o = this.questionRepository.findById(2);
		Question q = o.get();
		Answer a= new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setCreateDate(LocalDateTime.now());
		a.setQuestion(q);
		this.answerRepository.save(a);
	}

	@Test
	public void findAnwer(){
		Optional<Answer> oa = this.answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer a = oa.get();
		System.out.println(a.getQuestion().getId());
	}
	@Transactional

	@Test
	public void find(){
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		List<Answer> answerList = q.getAnswerList();

		assertEquals(1, answerList.size());
		assertEquals("네 자동으로 생성됩니다.", answerList.get(0).getContent());
	}
}
