package com.soy.springcommunity.repository.posts;

import com.soy.springcommunity.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long>, PostsRepositoryCustom {
    Optional<Posts> findById(Long id);
}
