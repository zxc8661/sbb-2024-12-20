package com.mysql.sbb.question;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionForm {


    @NotEmpty(message = "제목은 필수 입력 사항입니다.")
    @Size(max=200)
    private String subject;

    @NotEmpty(message = "내용은 필수 입력 사항입니다.")
    private String content;
}
