package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.*;
import com.soy.springcommunity.entity.Posts;
import com.soy.springcommunity.entity.Users;
import com.soy.springcommunity.exception.PostsException;
import com.soy.springcommunity.exception.UsersException;
import com.soy.springcommunity.repository.posts.PostsRepository;
import com.soy.springcommunity.repository.users.UserRepository;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PostsService {
    private PostsRepository postsRepository;
    private UserRepository userRepository;
    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UserRepository userRepository) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
    }

    public PostsListResponse viewPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Posts> postList = postsRepository.findPostList(pageable);
        List<PostsItemResponse> postsItemResponseList = postList.stream().map(PostsItemResponse::from).toList();

        PostsPagingMetaResponse pagingMetaResponse = PostsPagingMetaResponse.builder()
                .pageNumber(page)
                .pageSize(size)
                .sortCondition("createdAt,desc")
                .build();

        return new PostsListResponse(
                postsItemResponseList,
                pagingMetaResponse
        );
    }

    public PostsDetailResponse viewPostDetail(Long postId) {
        Posts post = postsRepository.findPostDetailById(postId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));
        return PostsDetailResponse.of(post);
    }

    public PostsCreateResponse createPost(Long userId, PostsCreateRequest postsCreateRequest) {
        Users user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UsersException.UsersNotFoundException("존재하지않는 유저입니다."));

        Posts post = Posts.builder()
                .title(postsCreateRequest.getPostTitle())
                .body(postsCreateRequest.getPostContent())
                .imgUrl(postsCreateRequest.getPostImageUrl())
                .user(user).build();

        postsRepository.save(post);
        return PostsCreateResponse.of(post.getId());

    }

    public void ensureUserIsPostWriter(Long postWriterId, Long userId){
        if (postWriterId != userId){
            throw new PostsException.PostsNotAuthorizedException("접근할 수 없는 게시글입니다.");
        }
    }

    public void validatePostEditRequest(PostsEditRequest postEditRequest) {
        if((postEditRequest.getPostTitle()==null || postEditRequest.getPostTitle().isEmpty()) &&
                (postEditRequest.getPostContent()==null || postEditRequest.getPostContent().isEmpty()) &&
                (postEditRequest.getPostImageUrl()==null || postEditRequest.getPostImageUrl().isEmpty())){
            throw new PostsException.NoEditPostsException("수정할 내용이 없습니다.");
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


    public SimpleResponse editPost(Long postId, Long userId, PostsEditRequest postEditRequest) {
        Posts posts = postsRepository.findById(postId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));
        ensureUserIsPostWriter(posts.getUser().getId(), userId);
        validatePostEditRequest(postEditRequest);
        editPostTitle(posts, postEditRequest.getPostTitle());
        editPostContent(posts, postEditRequest.getPostContent());
        editPostImgUrl(posts, postEditRequest.getPostImageUrl());
        return SimpleResponse.forEditPost(userId, postId);
    }

    public SimpleResponse deletePost(Long postId, Long userId) {
        Posts posts = postsRepository.findById(postId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));
        ensureUserIsPostWriter(posts.getUser().getId(), userId);
        postsRepository.delete(posts);
        return SimpleResponse.forDeletePost(userId, postId);
    }


}
