package com.yoolee.oauth.repository;


import com.yoolee.oauth.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepo extends JpaRepository<User, Long> {
    User findByUid (String username);
}
