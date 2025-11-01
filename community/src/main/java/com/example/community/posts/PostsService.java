package com.example.community.posts;

import com.example.community.contents.posts.dto.*;
import com.example.community.posts.dto.*;
import com.example.community.posts.entity.Posts;
import com.example.community.users.UserException;
import com.example.community.users.entity.Users;
import com.example.community.users.UserCsvRepository;
import com.example.community.common.dto.SimpleResponse;
import com.example.community.validator.DomainValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostsService {
    private PostCsvRepository postCsvRepository;
    private UserCsvRepository userCsvRepository;
    private DomainValidator domainValidator;

    @Autowired
    public PostsService(PostCsvRepository postCsvRepository,
                        UserCsvRepository userCsvRepository,
                        DomainValidator postValidator) {
        this.postCsvRepository = postCsvRepository;
        this.userCsvRepository = userCsvRepository;
        this.domainValidator = postValidator;
    }

    private void verifyUser(Users users) {
        if (users.getIsDeleted()){
            throw new PostException.PostGoneException("게시글을 찾을 수 없습니다.");
        }
    }

    public Users findUserById(Long id){
        return userCsvRepository.findById(id).orElseThrow(()-> new UserException.UserNotFoundException("존재하지 않는 사용자입니다."));
    }

    public Posts findPostById(Long id){
        return postCsvRepository.findById(id)
                .orElseThrow(() -> new PostException.PostNotFoundException("존재하지 않는 게시글입니다."));
    }

    public PostDetailResponse viewPostDetail(Long postId) {
        domainValidator.validatePostExistById(postId);
        Posts posts = findPostById(postId);
        Users writerEntity = findUserById(posts.getPostWriterId());
        verifyUser(writerEntity);
        PostDetailResponse poseDetailResponse = PostAssembler.toDetailResponse(posts, writerEntity);
        return poseDetailResponse;
    }

    private Long[] getTotalPostsAndPages(Long pageSize){
        Long totalPosts = (long) userCsvRepository.userStore.size();
        Long totalPages = (totalPosts + pageSize - 1) / pageSize;
        return new Long[]{totalPosts, totalPages};
    }
    private void verifyPagination(Long totalPosts, Long totalPages, Long pageNumber, Long pageSize) {
        if (pageSize <= 0 || pageNumber <= 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "페이지 정보를 확인하세요.");
        }

        if (totalPages < pageNumber) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "페이지 정보를 확인하세요.");
        }
    }

    private List<PostItemResponse> getPostItemResponseList(List<Posts> postsList){
        List<Long> uniqueWriterIds = postsList.stream().map(Posts::getPostWriterId).collect(Collectors.toList());
        Map<Long, Users> uniqueWriterMap = userCsvRepository.findAllByIds(uniqueWriterIds)
                .stream().collect(Collectors.toMap(Users::getId, Function.identity()));
        return postsList.stream()
                .map(post -> PostAssembler.toPostItemResponse(post, uniqueWriterMap.get(post.getPostWriterId())))
                .toList();
    }

    private List<Long> extractWriterIds(List<Posts> postsList){
        return postsList.stream()
                .map(Posts::getPostWriterId)
                .collect(Collectors.toList());
    }

    private Long[] getPageId(Long pageNumber, Long pageSize, Long totalPosts){
        Long postStartId = (Long) (pageNumber - 1) * pageSize;
        Long postEndId = (Long) Math.min(postStartId + pageSize, totalPosts);
        return new Long[]{postStartId, postEndId};
    }

    public PostListResponse viewPostList(Long pageNumber, Long pageSize) {
        Long[] pageInfo = getTotalPostsAndPages(pageSize); // 서비스 계층으로 바꾸기
        Long totalPosts = pageInfo[0];
        Long totalPages = pageInfo[1];

        Long[] pageIds = getPageId(pageNumber, pageSize, totalPosts);
        Long startPageId = pageIds[0];
        Long endPageId = pageIds[1];

        verifyPagination(totalPages, totalPosts, pageNumber, pageSize);

        List<Posts> paginatedPosts = postCsvRepository.findPagedPosts(startPageId, endPageId);
        List<PostItemResponse> postItemResponseList = getPostItemResponseList(paginatedPosts);

        PagingMetaResponse pagingMetaResponse = PagingMetaResponse.builder()
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalPosts(totalPosts)
                .totalPages(totalPages)
                .sortCondition("createdAt,desc")
                .build();

        return new PostListResponse(
                postItemResponseList,
                pagingMetaResponse
        );
    }

    public PostCreateResponse createPost(Long userId, PostCreateRequest postCreateRequest) {
        postCreateRequest.updatePostImageUrl(postCreateRequest.getPostImageUrl());

        Users writerEntity = userCsvRepository.findNotDeletedById(userId)
                .orElseThrow(() -> new PostException.PostNotAuthorizedException("게시글을 작성할 수 없습니다."));

        Posts posts = PostAssembler.toEntity(postCreateRequest, userId);
        postCsvRepository.save(posts);
        Long postId = posts.getPostId();
        return PostCreateResponse.of(postId);
    }

    public void validatePostEditRequest(PostEditRequest postEditRequest) {
        if((postEditRequest.getPostTitle()==null || postEditRequest.getPostTitle().isEmpty()) &&
                (postEditRequest.getPostContent()==null || postEditRequest.getPostContent().isEmpty()) &&
                (postEditRequest.getPostImageUrl()==null || postEditRequest.getPostImageUrl().isEmpty())){
            throw new PostException.NoEditPostException("수정할 내용이 없습니다.");
        }
    }

    public void editPostTitle(Posts posts, String newTitle) {
        posts.updatePostTitle(newTitle);
    }

    public void editPostContent(Posts posts, String newContent) {
        posts.updatePostContent(newContent);
    }

    public void editPostImgUrl(Posts posts, String newImageUrl) {
        posts.updatePostImgUrl(newImageUrl);
    }

    public void ensureUserIsPostWriter(Long postWriterId, Long userId){
        if (postWriterId != userId){
            throw new PostException.PostNotAuthorizedException("접근할 수 없는 게시글입니다.");
        }
    }

    public SimpleResponse editPost(Long postId, Long userId, PostEditRequest postEditRequest) {
        domainValidator.validatePostExistById(postId);
        Posts posts = findPostById(postId);
        ensureUserIsPostWriter(posts.getPostWriterId(), userId);
        validatePostEditRequest(postEditRequest);
        editPostTitle(posts, postEditRequest.getPostTitle());
        editPostContent(posts, postEditRequest.getPostContent());
        editPostImgUrl(posts, postEditRequest.getPostImageUrl());
        postCsvRepository.edit(posts);
        return SimpleResponse.forEditPost(userId, postId);
    }

    public SimpleResponse deletePost(Long postId, Long userId) {
        domainValidator.validatePostExistById(postId);
        Posts posts = findPostById(postId);
        ensureUserIsPostWriter(posts.getPostWriterId(), userId);
        postCsvRepository.delete(postId);
        return SimpleResponse.forDeletePost(userId, postId);
    }
}
