package com.yoolee.oauth;

import com.yoolee.oauth.repository.UserJpaRepo;
import com.yoolee.oauth.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootTest
class OauthApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserJpaRepo userJpaRepo;

    @Test
    void contextLoads() {
        System.out.printf("testSecret : %s\n", passwordEncoder.encode("testSecret"));
    }

    @Test
    void 유저생성(){

        User user = new User();
        user.setName("hongyoolee");
        user.setUid("yoolee");
        user.setPassword(passwordEncoder.encode("yoolee"));
        userJpaRepo.save(user);
    }

}
