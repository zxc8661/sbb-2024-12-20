package com.mysql.sbb.user;


import com.mysql.sbb.Comment.Comment;
import com.mysql.sbb.Comment.CommentService;
import com.mysql.sbb.EmailService;
import com.mysql.sbb.answer.Answer;
import com.mysql.sbb.answer.AnswerService;
import com.mysql.sbb.question.Question;
import com.mysql.sbb.question.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final CommentService commentService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e){
            e.printStackTrace();
            bindingResult.reject("signupFailed","이미 등록된 사용자입니다");
            return "signup_form";
        }catch(Exception e){
            e.printStackTrace();
            bindingResult.reject("signupFailed",e.getMessage());
            return "signup)form";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login_form";
    }

    @GetMapping("/password")
    public String password(){return "password_form";}

    @PostMapping("/password")
    public String password(@Valid UserPasswordFrom userPasswordFrom,
                           BindingResult bindingResult,
                           Model model){
        if(bindingResult.hasErrors()){
            return "password_form";
        }
        String username = userPasswordFrom.getUsername();


        SiteUser user= this.userService.getUser(username);
        String email = user.getEmail();

        String tempPassword = UUID.randomUUID().toString().substring(0,8);

        this.emailService.sendMail(email,"이메일 변경",tempPassword);
        this.userService.modifyPassword(user,tempPassword);
        return "redirect:/question/list";


    }

    @GetMapping("/modify/password")
    public String changePassword(){
        return "modify_password_form";
    }

    @PostMapping("/modify/password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("nextPassword") String nextPassword,
                                 Principal principal){
        SiteUser user = this.userService.getUser(principal.getName());

        boolean match = this.userService.matches(currentPassword,user.getPassword());
        if(match){
            userService.modifyPassword(user,nextPassword);
        }else{
            return "redirect:/question/list";
        }
        return "redirect:/question/list";
    }


    @GetMapping("/detail")
    public String userDetail(Model model, Principal principal){
        SiteUser user = this.userService.getUser(principal.getName());
        model.addAttribute("user",user);
        return "user_detail";
    }


    @GetMapping("/detail/{type}")
    public String userDetailContent(Model model,
                                    Principal principal,
                                    @PathVariable("type") String type){
        SiteUser user = this.userService.getUser(principal.getName());

        if(type.equals("question")){
            Page<Question> page =  this.questionService.getList(0,user.getUsername());
            model.addAttribute("page",page);

        }else if(type.equals("comment")){
            Page<Comment> page = this.commentService.getComments(user,0);
            model.addAttribute("page",page);

        }else if(type.equals("answer")){
            Page<Answer> page = this.answerService.getList(user,0);
            model.addAttribute("page",page);
        }
        model.addAttribute("type",type);
        return "user_detail_content";
    }
    @GetMapping("/loginInfo")
    public String getJson(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();

        return attributes.toString();
    }
}
