package com.soy.springcommunity.repository.posts;

import com.soy.springcommunity.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostsRepositoryCustom {
    Page<Posts> findPostList(Pageable pageable);
    Page<Posts> findPostListByUserId(Long userId, Pageable pageable);
    Posts findPostDetailById(Long pageId);
    List<Posts> searchPosts(String keyword, int limit);
}
