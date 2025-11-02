package com.example.community.posts.repository;

import com.example.community.posts.entity.Posts;
import com.example.community.users.entity.Users;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.Page;
import java.util.ArrayList;
import java.util.List;

public class PostRepositoryCustmImpl implements PostRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Posts> findPostList(Pageable pageable) {

    }

    @Override
    public Page<Posts> findPageByUserId(Long userId, Pageable pageable){
        EntityGraph entityGraph = entityManager.getEntityGraph("Posts.byUser");

        TypedQuery<Posts> query = entityManager.createQuery(
                "SELECT p FROM Posts p WHERE p.user.id = :userId ORDER BY p.createdAt DESC",
                Posts.class
        );
        query.setParameter("userId", userId);
        query.setHint("jakarta.persistence.fetchgraph", entityGraph);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Posts> posts = query.getResultList();
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public List<Posts> searchPosts(String keyword, int limit) {
        return List.of();
    }
}
