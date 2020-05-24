package com.yoolee.oauth.controller;

import com.yoolee.oauth.vo.OauthToken;
import kong.unirest.Unirest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/oauth2")
@RestController
public class Oauth2Controller {

    @GetMapping("/callback")
    public OauthToken.response callback(@RequestParam String code){

        String cridentials = "testClientId:testSecret";
        // base 64로 암호화
        String encodingCredentials = new String(
                Base64.encodeBase64(cridentials.getBytes())
        );
        String requestCode = code;
        OauthToken.request request = new OauthToken.request(){{
            setCode(requestCode);
            setGrant_type("authorization_code");
            setRedirect_uri("http://localhost:8081/oauth2/callback");
        }};
        OauthToken.response oauthToken = Unirest.post("http://localhost:8081/oauth/token")
                .header("Authorization","Basic "+encodingCredentials)
                .fields(request.getMapData())
                .asObject(OauthToken.response.class).getBody();

        return oauthToken;
    }

}