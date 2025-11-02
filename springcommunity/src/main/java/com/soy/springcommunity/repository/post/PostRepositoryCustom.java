package com.soy.springcommunity.repository.post;

import com.soy.springcommunity.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Posts> findPostList(Pageable pageable);
    Page<Posts> findPageByUserId(Long userId, Pageable pageable);
    List<Posts> searchPosts(String keyword, int limit);
}
