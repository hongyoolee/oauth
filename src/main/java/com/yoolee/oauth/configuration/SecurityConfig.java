package com.yoolee.oauth.configuration;

import com.yoolee.oauth.provider.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    // 인증 : 어떤 사용자인지 확인하는 메소드 커스터마이징
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable() // state 값을 전달 받아서 csrf 공격을 막을 것인가? no
                .authorizeRequests()
                .antMatchers("/oauth/**","/oauth2/callback").permitAll() // 해당 url은 권한 체크를 하지 않겠다. (permitAll)
                .and()
                .formLogin()
                //.loginPage("/login") //로그인 페이지 지정 (지정 안하면 기본 제공 로그인 페이지 제공.)
                .and()
                .httpBasic();
    }

}
