package com.soy.springcommunity.entity;

import com.soy.springcommunity.utils.PasswordUtil;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 60, nullable = false)
    private String passwordHash;

    @Column(name = "nickname", length = 10, nullable = false, unique = true)
    private String nickname;

    @Column(name = "profile_img_url", length = 2048)
    private String profileImgUrl;

    @Column(name = "is_deleted" )
    private Boolean isDeleted;

    @OneToMany(mappedBy = "user")
    private List<Posts> posts;

    @OneToMany(mappedBy = "user")
    private List<Comments> comments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<PostLikes> postLikes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<CommentLikes> commentLikes;

    @OneToOne(mappedBy = "user")
    private UserDetail userDetail;

    @Builder
    public Users(String email, String password, String nickname, String profileImgUrl){
        this.email = email;
        this.passwordHash = PasswordUtil.getHashedPassword(password);
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.isDeleted = false;
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.userDetail.setUpdatedAt();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.userDetail.setDeletedAt();
    }

    public void updateProfile(String newNickname, String newImgUrl){
        this.nickname = newNickname;
        this.profileImgUrl = newImgUrl;
        this.userDetail.setUpdatedAt();
    }

    public boolean isPasswordMatch(String givenPasswordHash) {
        return BCrypt.checkpw(this.passwordHash, givenPasswordHash);
    }
}
