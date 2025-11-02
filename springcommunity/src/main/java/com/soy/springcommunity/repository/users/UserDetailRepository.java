package com.soy.springcommunity.repository.users;

import com.soy.springcommunity.entity.UserDetail;
import com.soy.springcommunity.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, Long> {

}
