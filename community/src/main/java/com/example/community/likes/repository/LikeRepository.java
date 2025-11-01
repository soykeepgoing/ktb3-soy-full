package com.example.community.likes.repository;

import com.example.community.likes.entity.Likes;

public interface LikeRepository {
    Likes save(Likes entity);
    boolean existsByContentAndUserId(Long contentId, Long userId);
    void deleteByContentAndUserId(Long contentId, Long userId);
}
