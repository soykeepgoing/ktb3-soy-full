package com.example.community.posts.entity;

import com.example.community.comments.entity.Comments;
import com.example.community.likes.entity.Likes;
import com.example.community.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(fetch = FetchType.LAZY)
    @MapsId
    private List<PostStats> postStats;

    private String title;
    private String content;
    private String imgUrl;

    @OneToMany(mappedBy = "post")
    private List<Comments> comments;

    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    public void updatePostId(Long postId) {
        this.id = postId;
    }

    public void updateLikeCounts() {
        this.postLikeCounts += 1;
    }

    public void decrementLikeCount() {
        this.postLikeCounts -= 1;
    }

    public void updatePostTitle(String title) {
        if (title != null & title != "") {
            this.title = title;
        }
    }

    public void updatePostContent(String content) {
        if (content != null & content != "") {
            this.content = content;
        }
    }

    public void updatePostImgUrl(String url) {
        if (url != null & url != ""){
            this.imgUrl = url;
        }
    }


}