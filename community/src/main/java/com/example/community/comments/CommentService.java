package com.example.community.comments;

import com.example.community.comments.dto.*;
import com.example.community.comments.entity.Comments;
import com.example.community.common.dto.SimpleResponse;
import com.example.community.comments.dto.*;
import com.example.community.posts.PostCsvRepository;
import com.example.community.users.UserCsvRepository;
import com.example.community.validator.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    private PostCsvRepository postCsvRepository;
    private UserCsvRepository userCsvRepository;
    private CommentsCsvRepository commentsCsvRepository;
    private DomainValidator domainValidator;

    @Autowired
    public CommentService(CommentsCsvRepository commentsCsvRepository,
                          UserCsvRepository userCsvRepository,
                          PostCsvRepository postCsvRepository,
                          DomainValidator domainValidator) {
        this.commentsCsvRepository = commentsCsvRepository;
        this.userCsvRepository = userCsvRepository;
        this.postCsvRepository = postCsvRepository;
        this.domainValidator = domainValidator;
    }

    public List<Comments> sortCommentsByCreatedAt(List<Comments> commentsEntities) {
        List<Comments> sortedCommentsEntities = commentsEntities.stream().sorted(Comparator.comparing(Comments::getCommentCreatedAt)).collect(Collectors.toList());
        return sortedCommentsEntities;
    }

    public CommentsViewResponse viewComments(Long postId) {
        domainValidator.validatePostExistById(postId);
        List<Comments> commentsEntities = commentsCsvRepository.getCommentsByPostId(postId);
        commentsEntities = sortCommentsByCreatedAt(commentsEntities);
        return new CommentsViewResponse(commentsEntities);
    }

    public Comments findCommentById(Long commentId) {
        return commentsCsvRepository.findById(commentId)
                .orElseThrow(()-> new CommentsException.CommentsNotFoundException("존재하지 않는 댓글입니다."));
    }

    public void ensureCommentMatchPost(Long parentCommentId, Long postId) {
        Comments comments = findCommentById(parentCommentId);
        if (!comments.getPostId().equals(postId)) {
            throw new CommentsException.CommentsNotMatchPostException("댓글 정보를 확인하세요.");
        }
    }

    public void ensureCommentMatchUser(Long commentId, Long userId) {
        Comments comments = findCommentById(commentId);
        if(!comments.getCommentWriterId().equals(userId)){
            throw new CommentsException.CommentsUnauthorizedException("댓글에 대한 권한이 없습니다.");
        }

    }

    public CreateCommentResponse createComments(CreateCommentRequest createCommentRequest,
                                                Long postId,
                                                Long userId,
                                                Long parentCommentId
    ) {
        domainValidator.validatePostExistById(postId);
        domainValidator.validateUserExistById(userId);
        if(parentCommentId != null){
            domainValidator.validateCommentExistById(parentCommentId);
            ensureCommentMatchPost(parentCommentId, postId);
        }
        Comments comments = CommentAssembler.toEntity(postId, userId, parentCommentId, createCommentRequest.getCommentContent());
        commentsCsvRepository.save(comments);
        return CreateCommentResponse.of(postId);
    }

    public SimpleResponse editComments(EditCommentRequest editCommentRequest,
                                       Long postId,
                                       Long commentId,
                                       Long userId){
        domainValidator.validatePostExistById(postId);
        domainValidator.validateUserExistById(userId);
        domainValidator.validateCommentExistById(commentId);

        ensureCommentMatchUser(commentId, userId);
        ensureCommentMatchPost(postId, commentId);

        commentsCsvRepository.editComment(commentId, editCommentRequest.getNewCommentContent());
        return SimpleResponse.forEditComment(userId, commentId);
    }

    public SimpleResponse deleteComments(Long postId, Long commentId, Long userId){
        domainValidator.validatePostExistById(postId);
        domainValidator.validateUserExistById(userId);
        domainValidator.validateCommentExistById(commentId);

        ensureCommentMatchUser(commentId, userId);
        ensureCommentMatchPost(postId, commentId);

        commentsCsvRepository.delete(commentId);
        return SimpleResponse.forDeleteComment(userId, commentId);
    }


}
