package com.mysql.sbb.question;


import com.mysql.sbb.Category.Category;
import com.mysql.sbb.Category.CategoryService;
import com.mysql.sbb.Comment.Comment;
import com.mysql.sbb.Comment.CommentForm;
import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.answer.AnswerForm;
import com.mysql.sbb.answer.AnswerService;
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
import com.mysql.sbb.question.QuestionForm;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(Model model,@RequestParam(value="page",defaultValue = "0") int page,
                       @RequestParam(value = "kw",defaultValue = "") String kw) {
        Page<Question> paging= this.questionService.getList(page,kw,"");
        model.addAttribute("paging", paging);
        model.addAttribute("kw",kw);
        return "question_list";
    }

    @GetMapping("/category/{id}")
    public String list(Model model,
                       @RequestParam(value="page", defaultValue="0") int page,
                       @RequestParam(value="kw",defaultValue = "") String kw,
                       @PathVariable("id") Integer id){
        Category category = this.categoryService.getCategory(id);
        Page<Question> paging = this.questionService.getList(page,kw,category.getCategory());
        model.addAttribute("paging",paging);
        model.addAttribute("kw",kw);
        return "question_category";

    }


    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id,
                         AnswerForm answerForm, CommentForm commentForm,
                         @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Question question = this.questionService.getQuestion(id);
        question.setViewCount(question.getViewCount()+1);
        Page<Answer> paging = this.answerService.getList(question, page);
        model.addAttribute("question", question);
        model.addAttribute("paging", paging);
        model.addAttribute("commentForm", commentForm); // 폼 객체 추가
        this.questionService.modify(question,question.getSubject(),question.getContent());
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String QuestionCreate(@Valid QuestionForm questionForm,
                                 BindingResult bindingResult,
                                 Principal principal)
    {

        if(bindingResult.hasErrors()){
            return "question_form";
        }
        Category category  = this.categoryService.getCategory(questionForm.getCategoryId());
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(),questionForm.getContent(),siteUser,category);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal,@PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(@PathVariable("id") Integer id,
                               Principal principal){
        Question question = this.questionService.getQuestion(id);
        SiteUser user = this.userService.getUser(principal.getName());
        this.questionService.vote(question,user);
        return String.format("redirect:/question/detail/%s",id);
    }




}