package com.soy.springcommunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "좋아요 간단 응답")
public class LikesSimpleResponse {

    @Schema(description = "컨텐츠 타입", example = "post")
    private String contentType;

    @Schema(description = "컨텐츠 식별자", example = "1")
    private Long contentId;

    @Schema(description = "컨텐츠 작성자 식별자", example = "1")
    private Long userId;

    public LikesSimpleResponse() {}

    public LikesSimpleResponse(String contentType, Long contentId, Long userId) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.userId = userId;
    }

    public static LikesSimpleResponse forLike(String contentType, Long contentId, Long userId) {
        return new LikesSimpleResponse("Like " + contentType, contentId, userId);
    }

    public static LikesSimpleResponse forUnlike(String contentType, Long contentId, Long userId) {
        return new LikesSimpleResponse("Unlike " + contentType, contentId, userId);
    }
}

