package com.soy.springcommunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UsersSignUpResponse {
    @Schema(description = "사용자 이메일", example = "test1@gmail.com")
    private String userEmail;
    @Schema(description = "사용자 닉네임", example = "test1")
    private String userNickname;
    @Schema(description = "사용자 생성일시", example = "202510212147")
    private LocalDateTime userCreatedAt;

    private UsersSignUpResponse(String userEmail, String userNickname, LocalDateTime userCreatedAt) {
        this.userEmail = userEmail;
        this.userNickname = userNickname;
        this.userCreatedAt = userCreatedAt;
    }

    public static UsersSignUpResponse create(String email, String nickname, LocalDateTime createdAt) {
        return new UsersSignUpResponse(email, nickname, createdAt);
    }
}

