package com.soy.springcommunity.repository.comments;

import com.soy.springcommunity.entity.Comments;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepositoryCustom {
    List<Comments> findCommentsByPostId(Long postId);
}
