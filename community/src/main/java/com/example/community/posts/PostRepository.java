package com.example.community.posts;

import com.example.community.posts.entity.Posts;
import com.example.community.repository.Repository;

import java.util.List;

public interface PostRepository extends Repository<Posts, Long> {
    void edit(Posts posts);
    void incrementLikeCount(Long postId);
    void decrementLikeCount(Long postId);
    List<Posts> findPagedPosts(Long startPageId, Long endPageId);
}
