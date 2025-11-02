package com.soy.springcommunity.repository.comments;

import com.soy.springcommunity.entity.Comments;
import com.soy.springcommunity.entity.Posts;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CommentsRepositoryCustomImpl implements CommentsRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Comments> findCommentsByPostId(Long postId) {
        EntityGraph entityGraph = entityManager.getEntityGraph("Posts.withUserAndStats");
        TypedQuery<Comments> query = entityManager.createQuery(
                "SELECT c FROM Comments c WHERE c.post.id = :postId ORDER BY c.createdAt DESC",
                Comments.class
        );
        query.setHint("jakarta.persistence.fetchgraph", entityGraph);
        return query.getResultList();
    }
}
