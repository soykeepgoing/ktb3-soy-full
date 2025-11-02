package com.soy.springcommunity.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
