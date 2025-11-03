package com.soy.springcommunity.service;

import com.soy.springcommunity.dto.*;
import com.soy.springcommunity.entity.PostStats;
import com.soy.springcommunity.entity.Posts;
import com.soy.springcommunity.entity.Users;
import com.soy.springcommunity.exception.PostsException;
import com.soy.springcommunity.exception.UsersException;
import com.soy.springcommunity.repository.posts.PostsRepository;
import com.soy.springcommunity.repository.posts.PostsStatsReposiotry;
import com.soy.springcommunity.repository.users.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostsService {
    private PostsRepository postsRepository;
    private UsersRepository usersRepository;
    private PostsStatsReposiotry postsStatsReposiotry;
    @Autowired
    public PostsService(PostsRepository postsRepository,
                        UsersRepository usersRepository,
                        PostsStatsReposiotry postsStatsReposiotry) {
        this.postsRepository = postsRepository;
        this.usersRepository = usersRepository;
        this.postsStatsReposiotry = postsStatsReposiotry;
    }

    @Transactional
    public PostsListResponse viewPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Posts> postList = postsRepository.findAll(pageable);
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

    @Transactional
    public PostsDetailResponse viewPostDetail(Long postId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));
        post.getPostStats().increaseViewCount();
        return PostsDetailResponse.of(post);
    }

    @Transactional
    public PostsCreateResponse createPost(Long userId, PostsCreateRequest postsCreateRequest) {
        Users user = usersRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new UsersException.UsersNotFoundException("존재하지않는 유저입니다."));

        Posts post = Posts.builder()
                .title(postsCreateRequest.getPostTitle())
                .body(postsCreateRequest.getPostContent())
                .imgUrl(postsCreateRequest.getPostImageUrl())
                .user(user).build();

        PostStats postStats = PostStats.createStats(post);

        postsRepository.save(post);
        postsStatsReposiotry.save(postStats);

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


    @Transactional
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

    @Transactional
    public SimpleResponse deletePost(Long postId, Long userId) {
        Posts posts = postsRepository.findById(postId)
                .orElseThrow(() -> new PostsException.PostsNotFoundException("존재하지 않는 게시글입니다."));
        ensureUserIsPostWriter(posts.getUser().getId(), userId);
        postsRepository.delete(posts);
        return SimpleResponse.forDeletePost(userId, postId);
    }


}
