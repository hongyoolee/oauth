package com.yoolee.oauth.configuration;

import com.yoolee.oauth.provider.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableAuthorizationServer
@Configuration
public class Oauth2AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    /***
     * oauth 관련 클라이언트 정보 가져올때 설정
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
        //.passwordEncoder(passwordEncoder); // 패스워드 암호화 해서 넣지 않으면 인증 후 클라이언트 정보 읽어올때 오류나게 함.
    }

    /***
     * 권한 동의 저장소 선언
     * @return
     */
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /***
         * 리소스 서버에서 발행 토큰인지 체크 하기위해 clientid 별 발행 토큰 정보를 저장
         * 저장소는 DB 사용. (OAUTH_ACCESS_TOKEN table)
         */
        /*endpoints.tokenStore(new JdbcTokenStore(dataSource))
                .userDetailsService(customUserDetailsService);*/

        /***
         * token 정보 DB 저장 > jwt 방식으로 변경 (이렇게 하면 토큰을 DB 에 저장할 필요가 없어짐.)
         * jwt 토큰 자체에 인증 정보가 있기에 따로 리소스 서버에서 token 유효 체크를 안해도 된다.
         * user detail service 를 넣은 이유는 refrash 토큰으로 token 발행시 유저 정보를 검색하기 위해.
         */
        super.configure(endpoints);
        endpoints.accessTokenConverter(jwtAccessTokenConverter()) // 해당 설정시 리소스 서버에서 /oauth/token_key시 암호화 key 정보를 읽어 온다.(리소스 서버에서 최초 한번만 기동시 읽어 온다)
                .userDetailsService(customUserDetailsService) // 유저정보 조회는 커스터마이징한 서비스로 지정
                .approvalStore(approvalStore()); // 권한 동의 저장소 설정
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        // RSA 암호화 : 비 대칭키 암호화 : 공개키로 암호화 하면 개인키로 복호화
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("oauth2jwt.jks"), "13245941".toCharArray());
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("oauth2jwt"));

        // 대칭키 암호화 : key 값은 리소스 서버에도 넣고 하면 됨.
        /* JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("key");*/

        return converter;
    }

    /***
     * checkTokenAccess : resource 서버에서 /oauth/check_token(저장된 토큰을 DB에서 조회) 보낼시 받기 위해 필요(인증된 토큰인지 확인 필요.) > jwt 토큰 사용시 필요 없음.
     * tokenKeyAccess : jwt 토큰 사용시 필요 > 암호화한 jwt 토큰을 보내기에 공개키 정보 공유시 사용
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                //.checkTokenAccess("isAuthenticated()") //allow check token
                .allowFormAuthenticationForClients();
    }
}
