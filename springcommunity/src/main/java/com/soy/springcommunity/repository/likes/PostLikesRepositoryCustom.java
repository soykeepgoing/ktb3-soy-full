package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.PostLikes;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikesRepositoryCustom {
    Optional<PostLikes> findByPostIdAndUserId(Long postId, Long userId);
}
