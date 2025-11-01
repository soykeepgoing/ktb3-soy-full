package com.example.community.validator;

import com.example.community.comments.CommentsException;
import com.example.community.comments.CommentsRepository;
import com.example.community.posts.PostException;
import com.example.community.posts.PostRepository;
import com.example.community.users.UserException;
import com.example.community.users.UserRepository;
import com.example.community.users.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomainValidator {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentsRepository commentsRepository;

    @Autowired
    public DomainValidator(
            UserRepository userRepository,
            PostRepository postRepository,
            CommentsRepository commentsRepository
    ){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentsRepository = commentsRepository;
    }

    public void validateUserExistById(Long userId) {
        Users users = userRepository.findById(userId).orElseThrow(
                () -> new UserException.UserNotFoundException("존재하지 않는 유저입니다.")
        );
        if (users.getIsDeleted()){
            throw new UserException.UserNotFoundException("탈퇴한 유저입니다.");
        }
    }

    public void validatePostExistById(Long postId) {
        if(!postRepository.existsById(postId)){
            throw new PostException.PostNotFoundException("존재하지 않는 게시글입니다.");
        }
    }

    public void validateCommentExistById(Long commentId) {
        if(!commentsRepository.existsById(commentId)){
            throw new CommentsException.CommentsNotFoundException("댓글 정보를 찾을 수 없습니다.");
        }
    }

}
