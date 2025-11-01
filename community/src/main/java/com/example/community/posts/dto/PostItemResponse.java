package com.example.community.posts.dto;

import com.example.community.posts.entity.PostStats;
import com.example.community.users.entity.WriterSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "게시글 아이템 DTO")
public class PostItemResponse {

    @Schema(description = "게시글 식별자", example = "101")
    private Long postId;

    @Schema(description = "게시글 작성자 정보", example = "{userID: 1, userNickname: test1, userProfileImgUrl: 'https://example.com/users/1/profile-image.png'}")
    private WriterSummary writerSummary;

    @Schema(description = "게시글 제목", example = "title")
    private String postTitle;

    @Schema(description = "게시글 생성일시", example = "202510101010")
    private String postCreatedAt;

    @Schema(description = "게시글 개수 정보", example = "{likes:10, views:10, comments:10}")
    private PostStats postStats;
}
