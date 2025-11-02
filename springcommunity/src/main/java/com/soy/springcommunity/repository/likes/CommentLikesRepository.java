package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.CommentLikes;
import com.soy.springcommunity.repository.comments.CommentsRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> , CommentLikesRepositoryCustom{
}
