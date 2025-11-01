package com.example.community.likes.service;

import com.example.community.comments.CommentsCsvRepository;
import com.example.community.likes.repository.LikeCsvRepository;
import com.example.community.users.UserCsvRepository;
import com.example.community.validator.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("commentLikeService")
public class CommentLikeService extends LikeService {

    public CommentsCsvRepository commentsCsvRepository;

    @Autowired
    public CommentLikeService(UserCsvRepository userCsvRepository,
                              @Qualifier("commentLikeRepository") LikeCsvRepository likeCsvRepository,
                              CommentsCsvRepository commentsCsvRepository,
                              DomainValidator domainValidator) {
        this.contentType = "comment";
        this.userCsvRepository = userCsvRepository;
        this.likeCsvRepository = likeCsvRepository;
        this.commentsCsvRepository = commentsCsvRepository;
        this.domainValidator = domainValidator;
    }


    @Override
    public void validateContent(Long contentId) {
        domainValidator.validateCommentExistById(contentId);
    }
}
