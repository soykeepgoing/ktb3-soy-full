package com.example.community.users;

import com.example.community.repository.Repository;
import com.example.community.users.entity.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByNickname(String username);
    ArrayList<Users> findAllByIds(List<Long> ids);
    void editPassword(Users users, String newPassword);
    void editProfile(Users users, String newNickname, String newProfileImgUrl);
    void softDelete(Users users);
}
