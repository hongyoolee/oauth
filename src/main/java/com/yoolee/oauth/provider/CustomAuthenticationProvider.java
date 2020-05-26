package com.yoolee.oauth.provider;

import com.yoolee.oauth.repository.UserJpaRepo;
import com.yoolee.oauth.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserJpaRepo userJpaRepo;

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        User user = userJpaRepo.findByUid(username);

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BadCredentialsException("password is not vaild");
        }

        return new UsernamePasswordAuthenticationToken(username,password,user.getAuthorities());

    }
}
