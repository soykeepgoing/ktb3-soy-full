package com.example.community.likes.entity;

import com.example.community.posts.entity.Posts;
import com.example.community.users.entity.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.hibernate.annotations.Comments;

@Entity
public class CommentLikes extends BaseLikes {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comments comment;

    private CommentLikes(Comments comment, Users user) {
        super(user);
        this.comment = comment;
    }

    public static CommentLikes of(Comments comment, Users user) {
        return new CommentLikes(comment, user);
    }
}
