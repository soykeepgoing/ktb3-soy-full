package com.example.community.likes.service;

import com.example.community.likes.repository.LikeCsvRepository;
import com.example.community.posts.PostCsvRepository;
import com.example.community.users.UserCsvRepository;
import com.example.community.validator.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("postLikeService")
public class PostLikeService extends LikeService {
    public PostCsvRepository postCsvRepository;

    @Autowired
    public PostLikeService(UserCsvRepository userCsvRepository,
                           @Qualifier("postLikeRepository") LikeCsvRepository likeCsvRepository,
                           PostCsvRepository postCsvRepository,
                           DomainValidator domainValidator) {
        this.contentType = "post";
        this.userCsvRepository = userCsvRepository;
        this.likeCsvRepository = likeCsvRepository;
        this.postCsvRepository = postCsvRepository;
        this.domainValidator = domainValidator;
    }

    @Override
    public void validateContent(Long contendId){
        this.domainValidator.validatePostExistById(contendId);
    }

}
