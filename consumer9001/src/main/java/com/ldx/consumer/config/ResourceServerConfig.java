package com.ldx.consumer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@EnableWebSecurity
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private LdxAccessTokenConvertor accessTokenConvertor;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources.resourceId("consumer");
//        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
//        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:9999/oauth/check_token");
//        remoteTokenServices.setClientId("client_ldx");
//        remoteTokenServices.setClientSecret("abcxyz");
//
//        resources.tokenServices(remoteTokenServices);

        //jwt令牌改造
        resources.resourceId("consumer").tokenStore(tokenStore()).stateless(true); //无状态设置
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .authorizeRequests()
                .antMatchers("/consumer/**").authenticated()
                .antMatchers("/demo/**").authenticated()
                .anyRequest().permitAll();
    }

    /**
     * 创建tokenStore对象（令牌存储对象）
     * @return
     */
    public TokenStore tokenStore() {
        //return new InMemoryTokenStore();
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    private String sign_key = "ldx123";

    /**
     * 返回jwt令牌转换器
     * 在这里可以把签名秘钥传递进去给转换器对象
     * @return
     */
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(sign_key); //签名秘钥
        jwtAccessTokenConverter.setVerifier(new MacSigner(sign_key)); //验证时使用的秘钥，和签名秘钥保持一致
        jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConvertor);
        return jwtAccessTokenConverter;
    }
}
