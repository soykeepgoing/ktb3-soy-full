package com.example.community.posts;

import com.example.community.Utility;
import com.example.community.posts.dto.PostCreateRequest;
import com.example.community.posts.dto.PostDetailResponse;
import com.example.community.posts.dto.PostItemResponse;
import com.example.community.posts.entity.PostStats;
import com.example.community.posts.entity.Posts;
import com.example.community.users.entity.Users;
import com.example.community.users.entity.WriterSummary;

public class PostAssembler {
    public static Posts toEntity(PostCreateRequest postCreateRequest, Long userId) {
        return Posts.builder()
                .postTitle(postCreateRequest.getPostTitle())
                .postContent(postCreateRequest.getPostContent())
                .postImgUrl(postCreateRequest.getPostImageUrl())
                .postWriterId(userId)
                .postLikeCounts(0L)
                .postCommentCounts(0L)
                .postViewCounts(0L)
                .postCreatedAt(Utility.getCreatedAt())
                .build();
    }

    public static PostDetailResponse toDetailResponse(Posts posts, Users users) {
        WriterSummary writerSummary = WriterSummary.create(
                users.getId(),
                users.getNickname(),
                users.getProfileImgUrl()
        );

        PostStats postStats = PostStats.of(
                posts.getPostLikeCounts(),
                posts.getPostCommentCounts(),
                posts.getPostViewCounts()
        );

        return PostDetailResponse.builder()
                .postId(posts.getPostId())
                .postTitle(posts.getPostTitle())
                .postContent(posts.getPostContent())
                .postImgUrl(posts.getPostImgUrl())
                .postCreatedAt(posts.getPostCreatedAt())
                .postStats(postStats)
                .writerSummary(writerSummary)
                .build();
    }

    public static PostItemResponse toPostItemResponse(Posts posts, Users users) {
        WriterSummary writerSummary = WriterSummary.create(
                users.getId(),
                users.getNickname(),
                users.getProfileImgUrl()
        );

        PostStats postStats = PostStats.of(
                posts.getPostLikeCounts(),
                posts.getPostCommentCounts(),
                posts.getPostViewCounts()
        );

        PostItemResponse postItemResponse = PostItemResponse.builder()
                .postId(posts.getPostId())
                .writerSummary(writerSummary)
                .postTitle(posts.getPostTitle())
                .postCreatedAt(posts.getPostCreatedAt())
                .postStats(postStats)
                .build();

        return postItemResponse;
    }
}
