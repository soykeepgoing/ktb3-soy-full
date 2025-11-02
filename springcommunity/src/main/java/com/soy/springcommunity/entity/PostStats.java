package com.soy.springcommunity.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post_stats")
public class PostStats {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="post_id", unique = true, nullable = false)
    private Posts post;

    @Column(name = "view_count", nullable = false)
    private Long viewCount;
    @Column(name = "like_count", nullable = false)
    private Long likeCount;
    @Column(name = "comment_count", nullable = false)
    private Long commentCount;

    @Builder
    public PostStats(Long id, Long viewCount, Long likeCount, Long commentCount) {
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }
}
