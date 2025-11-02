package com.soy.springcommunity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "댓글 생성 응답 DTO")
public class CommentsCreateResponse {

    @Schema(description = "댓글 내용", example = "comment1")
    private String message;

    @Schema(description = "댓글을 단 게시글 식별자", example = "1")
    private Long postId;

    @Schema(description = "생성 후 리다이렉트할 리소스 위치", example = "/api/posts/123")
    private String redirectUri;

    private CommentsCreateResponse(String message, Long postId, String redirectUri) {
        this.message = message;
        this.postId = postId;
        this.redirectUri = redirectUri;
    }

    public static CommentsCreateResponse of(Long postId) {
        return new CommentsCreateResponse(
                "댓글이 성공적으로 생성되었습니다.", // 반환 메시지
                postId,
                "/posts/%d/comments".formatted(postId)
        );
    }
}
