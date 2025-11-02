package com.soy.springcommunity.repository.likes;

import com.soy.springcommunity.entity.PostLikes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikesRepository extends JpaRepository<PostLikes, Long>, PostLikesRepositoryCustom {

}
