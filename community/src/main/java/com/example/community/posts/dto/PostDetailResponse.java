package com.example.community.posts.dto;

import com.example.community.posts.entity.PostStats;
import com.example.community.users.entity.WriterSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(description = "게시글 상세 조회 응답 DTO")
public class PostDetailResponse {

    @Schema(description = "게시글 식별자", example = "101")
    private Long postId;

    @Schema(description = "게시글 제목", example = "title")
    private String postTitle;

    @Schema(description = "게시글 내용", example = "content")
    private String postContent;

    @Schema(description = "게시글 사진", example = "https://example.com/images/post-101.png")
    private String postImgUrl;

    @Schema(description = "게시글 생성일시", example = "202510101010")
    private String postCreatedAt;

    @Schema(description = "게시글 개수 정보", example = "{likes:10, views:10, comments:10}")
    private PostStats postStats;

    @Schema(description = "게시글 작성자 정보", example = "{userID: 1, userNickname: test1, userProfileImgUrl: 'https://example.com/users/1/profile-image.png'}")
    private WriterSummary writerSummary;
}
