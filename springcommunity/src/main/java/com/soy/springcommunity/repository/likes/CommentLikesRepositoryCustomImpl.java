package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.CommentLikes;
import com.soy.springcommunity.entity.Comments;
import com.soy.springcommunity.entity.PostLikes;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CommentLikesRepositoryCustomImpl implements CommentLikesRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CommentLikes> findByCommentIdAndUserId(Long commentId, Long userId) {
        EntityGraph entityGraph = entityManager.getEntityGraph("CommentLikes.withCommentAndUser");

        TypedQuery<CommentLikes> query = entityManager.createQuery(
                """
                SELECT cl.post 
                FROM CommentLikes cl
                WHERE cl.user.id = :userId
                ORDER BY cl.comment.createdAt DESC
                """,
                CommentLikes.class
        );

        return Optional.ofNullable(query.getSingleResult());
    }
}
