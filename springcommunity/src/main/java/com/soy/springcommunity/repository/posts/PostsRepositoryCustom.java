package com.soy.springcommunity.repository.posts;

import com.soy.springcommunity.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostsRepositoryCustom {
    Page<Posts> findPostList(Pageable pageable);
    Page<Posts> findPostListByUserId(Long userId, Pageable pageable);
    Optional<Posts> findPostDetailById(Long pageId);
    List<Posts> searchPosts(String keyword, int limit);
}
