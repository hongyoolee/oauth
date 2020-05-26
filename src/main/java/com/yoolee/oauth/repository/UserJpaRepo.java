package com.yoolee.oauth.repository;


import com.yoolee.oauth.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepo extends JpaRepository<User, Long> {

    User findByUid (String username);

}
