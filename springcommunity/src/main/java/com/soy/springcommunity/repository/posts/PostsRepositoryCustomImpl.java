package com.soy.springcommunity.repository.posts;

import com.soy.springcommunity.entity.Posts;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public class PostsRepositoryCustomImpl implements PostsRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Posts> findPostDetailById(Long postId){
        EntityGraph entityGraph = entityManager.getEntityGraph("Posts.withUserAndStatsAndComments");
        TypedQuery<Posts> query = entityManager.createQuery(
                "SELECT p FROM Posts p WHERE p.id = :postId",
                Posts.class
        );
        query.setParameter("postId", postId);
        query.setHint("jakarta.persistence.fetchgraph", entityGraph);

        return Optional.ofNullable(query.getSingleResult());
    }

    @Override
    public Page<Posts> findPostList(Pageable pageable) {
        EntityGraph entityGraph = entityManager.getEntityGraph("Posts.withUserAndStats");
        TypedQuery<Posts> query = entityManager.createQuery(
                "SELECT p FROM Posts p ORDER BY p.createdAt DESC",
                Posts.class
        );
        query.setHint("jakarta.persistence.fetchgraph", entityGraph);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Posts> posts = query.getResultList();
        return new PageImpl<>(posts, pageable, posts.size());
    }

    @Override
    public Page<Posts> findPostListByUserId(Long userId, Pageable pageable){
        EntityGraph entityGraph = entityManager.getEntityGraph("Posts.withUserAndStats");

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
