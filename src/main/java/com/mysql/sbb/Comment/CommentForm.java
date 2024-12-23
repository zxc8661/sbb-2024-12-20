package com.mysql.sbb.Comment;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    @NotEmpty(message = "내용을 입력해주세요.") // 유효성 검사 메시지 수정
    private String content;
}