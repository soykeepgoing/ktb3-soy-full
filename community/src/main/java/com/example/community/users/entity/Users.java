package com.example.community.users.entity;

import com.example.community.Utility;
import com.example.community.comments.entity.Comments;
import com.example.community.likes.entity.CommentLikes;
import com.example.community.likes.entity.PostLikes;
import com.example.community.posts.entity.Posts;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
