package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.CommentLikes;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentLikesRepositoryCustom {
    Optional<CommentLikes> findByCommentIdAndUserId(Long commentId, Long userId);
}
