package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.LikesSimpleResponse;
import com.soy.springcommunity.entity.*;
import com.soy.springcommunity.exception.CommentsException;
import com.soy.springcommunity.exception.LikesException;
import com.soy.springcommunity.exception.PostsException;
import com.soy.springcommunity.exception.UsersException;
import com.soy.springcommunity.repository.comments.CommentsRepository;
import com.soy.springcommunity.repository.likes.CommentLikesRepository;
import com.soy.springcommunity.repository.likes.PostLikesRepository;
import com.soy.springcommunity.repository.posts.PostsRepository;
import com.soy.springcommunity.repository.users.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentLikesService implements LikesService {
    private CommentsRepository commentsRepository;
    private CommentLikesRepository commentLikesRepository;

    @Autowired
    public CommentLikesService(
            CommentLikesRepository commentLikesRepository,
            CommentsRepository commentsRepository) {
        this.commentLikesRepository = commentLikesRepository;
        this.commentsRepository = commentsRepository;
    }

    @Transactional
    @Override
    public LikesSimpleResponse like(Long contentId, Long userId) {
        if (commentLikesRepository.existsByCommentIdAndUserId(contentId, userId)) {
            throw new LikesException.AlreadyLikedException("이미 좋아요한 댓글입니다.");
        }

        Comments comments = commentsRepository.findById(contentId)
                .orElseThrow(() -> new CommentsException.CommentsNotFoundException("존재하지 않는 댓글입니다."));

        CommentLikes commentLikes = CommentLikes.of(comments, comments.getUser());
        comments.getCommentStats().increaseLikeCount();
        commentLikesRepository.save(commentLikes);
        return LikesSimpleResponse.forLike(
                "comment",
                contentId,
                userId
        );
    }

    @Transactional
    @Override
    public LikesSimpleResponse unlike(Long contentId, Long userId) {
        CommentLikes checkCommentLikes = commentLikesRepository.findByCommentIdAndUserIdAndDeletedAtIsNull(contentId, userId)
                .orElseThrow(()-> new LikesException.AlreadyLikedException("이미 좋아요한 게시글입니다."));

        commentLikesRepository.delete(checkCommentLikes);
        checkCommentLikes.deleteLikes();
        checkCommentLikes.getComment().getCommentStats().decreaseLikeCount();
        return LikesSimpleResponse.forUnlike(
                "comment",
                contentId,
                userId
        );
    }
}
