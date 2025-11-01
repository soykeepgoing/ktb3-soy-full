package com.example.community.likes.service;

import com.example.community.Utility;
import com.example.community.likes.LikeException;
import com.example.community.likes.dto.SimpleResponse;
import com.example.community.likes.entity.Likes;
import com.example.community.likes.repository.LikeCsvRepository;
import com.example.community.users.UserCsvRepository;
import com.example.community.validator.DomainValidator;

public abstract class LikeService {
    public UserCsvRepository userCsvRepository;
    public LikeCsvRepository likeCsvRepository;
    public DomainValidator domainValidator;

    protected String contentType;
    public LikeService(){}

    public abstract void validateContent(Long contentId);

    public final void validateLikes(Long contentId, Long userId){
        if (likeCsvRepository.existsByContentAndUserId(contentId, userId)) {
            throw new LikeException.AlreadyLikedException("이미 좋아요한 게시글입니다.");
        }
    }

    public final SimpleResponse like(Long contentId, Long userId) {
        domainValidator.validateUserExistById(userId);
        validateContent(contentId);
        validateLikes(contentId, userId);

        String createdAt = Utility.getCreatedAt();
        Likes likes = Likes.of(
                contentId,
                userId,
                createdAt
        );
        likeCsvRepository.save(likes);

        return SimpleResponse.forLike(
                this.contentType,
                contentId,
                userId
        );
    }

    public final SimpleResponse unlike(Long contentId, Long userId) {
        domainValidator.validateUserExistById(userId);
        validateContent(contentId);
        likeCsvRepository.deleteByContentAndUserId(contentId, userId);
        return SimpleResponse.forUnlike(
                this.contentType,
                contentId,
                userId
        );
    }

}
