package com.example.community.comments;

import com.example.community.comments.entity.Comments;

public class CommentAssembler {
    public static Comments toEntity(Long postId,
                                    Long userId,
                                    Long parentId,
                                    String commentContent){
        return Comments.builder()
                .postId(postId)
                .commentWriterId(userId)
                .parentCommentId(parentId)
                .commentContent(commentContent)
                .build();
    }
}
