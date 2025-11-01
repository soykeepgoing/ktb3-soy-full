package com.example.community.likes.entity;

import com.example.community.posts.entity.Posts;
import com.example.community.users.entity.Users;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
public class PostLikes extends BaseLikes{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Posts post;

    private PostLikes(Posts post, Users user) {
        super(user);
        this.post = post;
    }

    public static PostLikes of(Posts post, Users user) {
        return new PostLikes(post, user);
    }

}
