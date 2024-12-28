package com.mysql.sbb.answer;


import com.mysql.sbb.Comment.CommentForm;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.question.QuestionService;
import com.mysql.sbb.user.SiteUser;
import com.mysql.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final QuestionService questionService;
    private  final AnswerService answerService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createAnswer(Model model,
                               @PathVariable("id") Integer id,
//                               @RequestParam(value = "content") String content,
                               @Valid AnswerForm answerForm,
                               BindingResult bindingResult,
                               Principal principal){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if(bindingResult.hasErrors()){
            Page<Answer> paging = this.answerService.getList(question, 0); // 첫 번째 페이지
            model.addAttribute("question", question);
            model.addAttribute("paging", paging);
            model.addAttribute("commentForm", new CommentForm());
            return String.format("redirect:/question/detail/%s",question.getId());
        }
        Answer answer = this.answerService.create(question,answerForm.getContent(),siteUser);
        return String.format("redirect:/question/detail/%s#answer_%s",id,answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswer(@PathVariable("id") Integer id,Principal principal,
                               AnswerForm answerForm){
        Answer answer =this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정 권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PostMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    public String modifyAnswer(@Valid AnswerForm answerForm,
                               BindingResult bindingResult,
                               @PathVariable("id") Integer id,
                               Principal principal){
        if(bindingResult.hasErrors()){
            return String.format("/modify/{%d}",id);
        }
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"변경 권한이 없습니다.");
        }
        this.answerService.modify(answer,answerForm.getContent());
        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(),answer.getId());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(@PathVariable("id") Integer id,
                               Principal principal){
        Answer answer = this.answerService.getAnswer(id);
        if(!answer.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한 없음");
        }
        this.answerService.delete(answer);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String answerVote(Principal principal,@PathVariable("id") Integer id)
    {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser user = this.userService.getUser(principal.getName());

        this.answerService.vote(answer,user);
        return String.format("redirect:/question/detail/%s#answer_%s",answer.getQuestion().getId(),answer.getId());
    }


    @GetMapping("/list")
    public String answerList(Model model, @RequestParam(value="page",defaultValue = "0") int page){
        Page<Answer> paging = this.answerService.getList(page);
        model.addAttribute("paging",paging);
        return "recent_commentOrAnswer";
    }


}