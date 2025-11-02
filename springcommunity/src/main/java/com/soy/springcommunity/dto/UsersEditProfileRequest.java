package com.soy.springcommunity.dto;

import com.soy.springcommunity.utils.ConstantUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
public class UsersEditProfileRequest {
    @NotBlank
    @Length(max= ConstantUtil.NICKNAME_MAX_LEN)
    private String nickname;
    @URL(message= ConstantUtil.MSG_URL_NOT_VALID)
    private String profileImgUrl;
    public UsersEditProfileRequest() {}
    public UsersEditProfileRequest(String nickname){
        this.nickname = nickname;
        this.profileImgUrl = "";
    }
    public UsersEditProfileRequest(String nickname, String profileImgUrl) {
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }
}
