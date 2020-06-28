package com.yoolee.oauth.provider;

import com.yoolee.oauth.repository.UserJpaRepo;
import com.yoolee.oauth.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserJpaRepo userJpaRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userJpaRepo.findByUid(username);

        if(ObjectUtils.isEmpty(user)){
            throw new UsernameNotFoundException(username);
        }
        return user;
    }
}
