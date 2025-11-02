package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.LikesSimpleResponse;
import com.soy.springcommunity.entity.PostLikes;
import com.soy.springcommunity.entity.Posts;
import com.soy.springcommunity.entity.Users;
import com.soy.springcommunity.exception.LikesException;
import com.soy.springcommunity.exception.PostsException;
import com.soy.springcommunity.exception.UsersException;
import com.soy.springcommunity.repository.likes.PostLikesRepository;
import com.soy.springcommunity.repository.posts.PostsRepository;
import com.soy.springcommunity.repository.users.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikesService implements LikesService {
    private PostsRepository postsRepository;
    private PostLikesRepository postsLikesRepository;
    private UsersRepository usersRepository;

    @Autowired
    public PostLikesService(
            PostLikesRepository postsLikesRepository,
            PostsRepository postsRepository,
            UsersRepository usersRepository) {
        this.postsLikesRepository = postsLikesRepository;
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    @Override
    public LikesSimpleResponse like(Long contentId, Long userId) {
        PostLikes checkPostLikes = postsLikesRepository.findByPostIdAndUserId(contentId, userId)
                .orElseThrow(()-> new LikesException.AlreadyLikedException("이미 좋아요한 게시글입니다."));

        Posts posts = postsRepository.findById(contentId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));

        Users user = usersRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UsersException.UsersNotFoundException("존재하지 않는 유저입니다."));

        PostLikes postLikes = PostLikes.of(posts, user);
        postsLikesRepository.save(postLikes);
        return LikesSimpleResponse.forLike(
                "post",
                contentId,
                userId
        );
    }

    @Transactional
    @Override
    public LikesSimpleResponse unlike(Long contentId, Long userId) {
        PostLikes checkPostLikes = postsLikesRepository.findByPostIdAndUserId(contentId, userId)
                .orElseThrow(()-> new LikesException.NotFoundException("좋아요 정보를 확인하세요"));
        if (checkPostLikes.getDeletedAt() == null) {
            postsLikesRepository.delete(checkPostLikes);
            checkPostLikes.deleteLikes();
        }
        return LikesSimpleResponse.forUnlike(
                "post",
                contentId,
                userId
        );
    }
}
