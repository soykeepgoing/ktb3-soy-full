package com.soy.springcommunity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class PostsCreateRequest {
    @NotBlank
    private String postTitle;
    @NotBlank
    private String postContent;

    private String postImageUrl;

    public void updatePostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }
}
