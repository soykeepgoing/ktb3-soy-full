package com.soy.springcommunity.dto;

import com.soy.springcommunity.entity.PostStats;
import com.soy.springcommunity.entity.Posts;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "게시글 상세 조회 응답 DTO")
public class PostsDetailResponse {

    @Schema(description = "게시글 식별자", example = "101")
    private Long id;
    @Schema(description = "게시글 제목", example = "title")
    private String title;
    @Schema(description = "게시글 내용", example = "content")
    private String body;
    @Schema(description = "게시글 사진", example = "https://example.com/images/post-101.png")
    private String imgUrl;
    @Schema(description = "게시글 생성일시", example = "202510101010")
    private LocalDateTime createdAt;

    private String userNickname;
    private String userProfileImgUrl;

    private Long statsViewCounts;
    private Long statsLikeCounts;
    private Long statsCommentCounts;

    public static PostsDetailResponse of(Posts post) {
        return new PostsDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getBody(),
                post.getImgUrl(),
                post.getCreatedAt(),
                post.getUser().getNickname(),
                post.getUser().getProfileImgUrl(),
                post.getPostStats().getViewCount(),
                post.getPostStats().getLikeCount(),
                post.getPostStats().getCommentCount()
        );
    }
}

