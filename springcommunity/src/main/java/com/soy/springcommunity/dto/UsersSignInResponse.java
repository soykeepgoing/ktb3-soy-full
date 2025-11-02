package com.soy.springcommunity.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UsersSignInResponse {
    @Schema(description = "사용자 이메일", example = "test1@gmail.com")
    private String userEmail;
    @Schema(description = "사용자 로그인 일시", example = "202510212147")
    private String userIssuedAt;
    @Schema(description = "사용자 로그인 만료 일시", example = "202510282147")
    private String userExpiresIn;

    public UsersSignInResponse() {}
    public UsersSignInResponse(String email, String issuedAt, String expiresIn) {
        this.userEmail = email;
        this.userIssuedAt = issuedAt;
        this.userExpiresIn = expiresIn;
    }
}

