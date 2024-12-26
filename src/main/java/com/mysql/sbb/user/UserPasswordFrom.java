package com.mysql.sbb.user;


import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserPasswordFrom {

    @NotEmpty(message = "아이디는 필수 입력 사항입니다")
    private String username;
}
