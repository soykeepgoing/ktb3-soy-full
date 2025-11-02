package com.example.community.posts.entity;

import com.example.community.comments.entity.Comments;
import com.example.community.users.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "posts")
@NamedEntityGraph(
        name = "Posts.byUser",
        attributeNodes = @NamedAttributeNode("user")
)
public class Posts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "post")
    private List<Comments> comments;

    @OneToOne(mappedBy = "post")
    private PostStats postStats;

    @Column(name = "title", length = 26, nullable = false)
    private String title;
    @Column(name = "body", nullable = false)
    private String body;
    @Column(name = "img_url", length = 2048)
    private String imgUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    public void updatePostId(Long postId) {
        this.id = postId;
    }

    public void updatePostTitle(String title) {
        if (title != null & title != "") {
            this.title = title;
        }
    }

    public void updatePostContent(String content) {
        if (content != null & content != "") {
            this.body = content;
        }
    }

    public void updatePostImgUrl(String url) {
        if (url != null & url != ""){
            this.imgUrl = url;
        }
    }


}