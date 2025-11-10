package com.soy.springcommunity.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "files_user_profile_img_url")
public class FilesUserProfileImgUrl {
    @Column(name = "profile_img_url", nullable = false)
    private String profile_img_url;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="user_id", unique = true, nullable = false)
    private Users user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    private FilesUserProfileImgUrl(Users user){
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = null;
        this.deletedAt = null;
    }

    public static FilesUserProfileImgUrl of(Users user) {
        return new FilesUserProfileImgUrl(user);
    }
}
