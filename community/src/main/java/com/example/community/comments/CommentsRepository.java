package com.example.community.comments;

import com.example.community.comments.entity.Comments;
import com.example.community.repository.Repository;

import java.util.ArrayList;

public interface CommentsRepository extends Repository<Comments, Long> {
    ArrayList<Comments> getCommentsByPostId(Long postId);
    void editComment(Long commentId, String content);
}
