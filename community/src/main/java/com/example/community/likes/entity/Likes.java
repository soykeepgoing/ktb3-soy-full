package com.example.community.likes.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId; // 이게 post 또는 comment 아이디가 될 예정이다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Long userId;

    private LocalDateTime createdAt;

    private Likes(Long contentId, Long userId, String createdAt) {
        this.contentId = contentId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public static Likes of(Long contentId, Long userId, String createdAt) {
        return new Likes(contentId, userId, createdAt);
    }

}
