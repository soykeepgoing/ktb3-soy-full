package com.example.community.comments.entity;

import com.example.community.posts.entity.Posts;
import com.example.community.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "body", nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    private Comments parentComment;

    @Column(name = "comment_depth", nullable = true)
    private int commentDepth;

    @OneToMany(mappedBy = "parentComment")
    private List<Comments> childrenCommentList;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateId(Long commentId) {
        this.commentId = commentId;
    }

    public void updateContent(String commentContent) {
        this.commentContent = commentContent;
    }

}
