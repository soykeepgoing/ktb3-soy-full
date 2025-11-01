package com.example.community.users.entity;

import com.example.community.Utility;
import com.example.community.posts.entity.Posts;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String passwordHash;
    private String nickname;
    private String profileImgUrl;
    private Boolean isDeleted;

    @OneToMany (mappedBy = "user")
    private List<Posts> posts;

    @OneToMany(fetch = FetchType.LAZY)
    private UserDetails userDetail;



    @Builder
    public Users(String email, String password, String nickname, String profileImgUrl){
        this.email = email;
        this.passwordHash = Utility.getHashedPassword(password);
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.isDeleted = false;
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public void updateProfileImgUrl(String newProfileImgUrl) {
        this.profileImgUrl = newProfileImgUrl;
    }

    public void updateUserNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updateUserId(Long newUserId) {
        this.id = newUserId;
    }

    public void updateUserIsDeleted(Boolean newUserIsDeleted) {
        this.isDeleted = newUserIsDeleted;
    }

    public boolean isPasswordMatch(String givenPasswordHash) {
        return BCrypt.checkpw(this.passwordHash, givenPasswordHash);
    }

    @Override
    public String toString() {
        return "%d %s %s %s %s %s %s".formatted(id, email, passwordHash, nickname, profileImgUrl, createdAt, deletedAt);
    }
}
