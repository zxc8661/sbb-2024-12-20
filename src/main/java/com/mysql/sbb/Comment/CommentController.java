package com.mysql.sbb.Comment;


import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.answer.AnswerService;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.question.QuestionService;
import com.mysql.sbb.user.SiteUser;
import com.mysql.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/question/{id}")
    public String createComment(@PathVariable("id") Integer id,
                                Principal principal,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Model model){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteuser = this.userService.getUser(principal.getName());
        Answer answer = this.answerService.getAnswer(id);

        if (bindingResult.hasErrors()) {
            Page<Answer> paging = this.answerService.getList(question, 0); // 첫 번째 페이지
            model.addAttribute("question", question);
            model.addAttribute("paging", paging);
            model.addAttribute("commentForm", new CommentForm());
            return String.format("redirect:/question/detail/%s",id);

        }
        Comment comment = this.commentService.create(commentForm.getContent(),siteuser,null,question);

        return String.format("redirect:/question/detail/%s",id);


    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/answer/{id}")
    public String createAnswerComment(@PathVariable("id") Integer id,
                                Principal principal,
                                @Valid CommentForm commentForm,
                                BindingResult bindingResult,
                                Model model){
        Answer answer = this.answerService.getAnswer(id);
        SiteUser user = this.userService.getUser(principal.getName());

        if(bindingResult.hasErrors()){
            model.addAttribute("question",answer.getQuestion());
            model.addAttribute("commentForm",new CommentForm());
            Page<Answer> paging = this.answerService.getList(answer.getQuestion(), 0); // 첫 번째 페이지



            return String.format("redirect:/question/detail/%s",answer.getQuestion().getId());
        }
        this.commentService.create(commentForm.getContent(),user,answer,null);
        return String.format("redirect:/question/detail/%s",answer.getQuestion().getId());
    }

    @GetMapping("/list")
    public String getComments(Model model,@RequestParam(value="page",defaultValue = "0") int page){
        Page<Comment> Paging = this.commentService.getComments(page);
        model.addAttribute("paging",Paging);
        return "recent_commentOrAnswer";
    }
}
