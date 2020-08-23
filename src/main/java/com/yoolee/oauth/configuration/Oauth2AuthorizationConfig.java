package com.yoolee.oauth.configuration;

import com.yoolee.oauth.provider.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;

@EnableAuthorizationServer
@Configuration
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;


/*    @Value("${security.oauth2.jwt.signkey}")
    private String signKey;*/


    // client id 별 redirect url 권한, 시크릿
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }




    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 토큰 처리시 DB 사용.(OAUTH_ACCESS_TOKEN table > clientid/token)
        // 토큰 저장은 DB
        /*endpoints.tokenStore(new JdbcTokenStore(dataSource))
                .userDetailsService(customUserDetailsService);*/

        // token 정보 DB 저장 > jwt 방식으로 변경 (이렇게 하면 토큰을 DB 에 저장할 필요가 없어짐.)
        //jwt 토큰 자체에 인증정보가 있기에 따로 리소스 서버에서 token 유효 체크를 안해도 된다.
        // user detail service 를 넣은 이유는 리플레시 토큰 할시 유저 정보를 검색하기 위해..
        super.configure(endpoints);
        endpoints.accessTokenConverter(jwtAccessTokenConverter()) // 해당 설정시 리소스 서버에서 /oauth/token_key시 암호화 key 정보를 읽어 온다.(리소스 서버에서 최초 한번만 기동시 읽어 온다)
                .userDetailsService(customUserDetailsService);
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        // RSA 암호화
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("oauth2jwt.jks"), "13245941".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth2jwt"));
        // 대칭키 암호화
     /*   JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signKey);*/
        return converter;
    }
    // resource 서버에서 /oauth/check_token 보낼시 받기위해 필요(인증된 토큰인지 확인 필요.) > jwt 토큰 사용시 필요 없음.
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()") //allow check token
                .allowFormAuthenticationForClients();
    }
}
