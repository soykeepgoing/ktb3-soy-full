package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.PostLikes;
import com.soy.springcommunity.entity.Posts;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

public class PostLikesRepositoryCustomImpl implements PostLikesRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PostLikes> findByPostIdAndUserId(Long postId, Long userId) {
        EntityGraph entityGraph = entityManager.getEntityGraph("PostLikes.withPostAndUser");

        TypedQuery<PostLikes> query = entityManager.createQuery(
                """
                SELECT pl.post 
                FROM PostLikes pl
                WHERE pl.user.id = :userId
                ORDER BY pl.post.createdAt DESC
                """,
                PostLikes.class
        );

        return Optional.ofNullable(query.getSingleResult());
    }
}
