package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.LikesSimpleResponse;
import com.soy.springcommunity.entity.*;
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
    private UsersRepository usersRepository;

    @Autowired
    public CommentLikesService(
            CommentLikesRepository commentLikesRepository,
            CommentsRepository commentsRepository,
            UsersRepository usersRepository) {
        this.commentLikesRepository = commentLikesRepository;
        this.commentsRepository = commentsRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    @Override
    public LikesSimpleResponse like(Long contentId, Long userId) {
        CommentLikes checkCommentLikes = commentLikesRepository.findByCommentIdAndUserId(contentId, userId)
                .orElseThrow(()-> new LikesException.AlreadyLikedException("이미 좋아요한 게시글입니다."));

        Comments comments = commentsRepository.findById(contentId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 댓글입니다."));

        Users user = usersRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UsersException.UsersNotFoundException("존재하지 않는 유저입니다."));

        CommentLikes commentLikes = CommentLikes.of(comments, user);
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
        CommentLikes checkCommentLikes = commentLikesRepository.findByCommentIdAndUserId(contentId, userId)
                .orElseThrow(()-> new LikesException.AlreadyLikedException("이미 좋아요한 게시글입니다."));

        if (checkCommentLikes.getDeletedAt() == null) {
            commentLikesRepository.delete(checkCommentLikes);
            checkCommentLikes.deleteLikes();
        }
        return LikesSimpleResponse.forUnlike(
                "comment",
                contentId,
                userId
        );
    }
}
