package com.example.community.posts.entity;

import com.example.community.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comments;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
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

    private LocalDateTime createdAt;
    private LocalDateTime editedAt;

    @Override
    public String toString(){
        return "Post %d".formatted(id);
    }

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