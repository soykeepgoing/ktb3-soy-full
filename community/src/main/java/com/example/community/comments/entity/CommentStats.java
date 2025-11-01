package com.example.community.comments.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentStats {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "comment_id", unique = true, nullable = false)
    private Comments comment;

    @Column(name = "like_count", nullable = false)
    private Long likeCount;

    @Column(name = "reply_count", nullable = false)
    private Long replyCount;

}
