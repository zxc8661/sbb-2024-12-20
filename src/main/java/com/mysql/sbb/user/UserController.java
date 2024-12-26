package com.mysql.sbb.user;


import ch.qos.logback.core.model.Model;
import com.mysql.sbb.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

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

}