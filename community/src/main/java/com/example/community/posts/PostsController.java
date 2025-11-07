package com.example.community.posts;

import com.example.community.common.dto.SimpleResponse;
import com.example.community.posts.dto.*;
import com.example.community.posts.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class PostsController {
    private PostsService postsService;
    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @GetMapping("/api/posts")
    @Operation(summary = "게시글 목록 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 목록 보기 성공")
    })
    public ResponseEntity<PostListResponse> getPostList(@RequestParam Long page, @RequestParam Long size) {
        PostListResponse postListResponse = postsService.viewPostList(page, size);
        return ResponseEntity.ok(postListResponse);
    }

    @GetMapping("/api/posts/detail/{postId}")
    @Operation(summary = "게시글 상세 보기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 상세 보기 성공")
    })
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable("postId") Long postId) {
        PostDetailResponse postDetailResponse = postsService.viewPostDetail(postId);
        return ResponseEntity.ok(postDetailResponse);
    }

    @PostMapping("/api/posts")
    @Operation(summary = "게시글 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공")
    })
    public ResponseEntity<PostCreateResponse> createPost(@RequestParam Long userId, @RequestBody PostCreateRequest postCreateRequest) {
        PostCreateResponse postCreateResponse = postsService.createPost(userId, postCreateRequest);
        return ResponseEntity
                .created(URI.create(postCreateResponse.getRedirectUri()))
                .body(postCreateResponse);
    }

    @PatchMapping("/api/posts/{postId}")
    @Operation(summary = "게시글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 수정 성공")
    })
    public ResponseEntity<SimpleResponse> editPost(@PathVariable("postId") Long postId, @RequestParam Long userId, @RequestBody PostEditRequest postEditRequest) {
        SimpleResponse simpleResponse = postsService.editPost(postId, userId, postEditRequest);
        return ResponseEntity.ok(simpleResponse);
    }

    @DeleteMapping("/api/posts/{postId}")
    @Operation(summary = "게시글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공")
    })
    public ResponseEntity<SimpleResponse> deletePost(@PathVariable("postId") Long postId, @RequestParam Long userId) {
        SimpleResponse simpleResponse = postsService.deletePost(postId, userId);
        return ResponseEntity.ok(simpleResponse);
    }
}
