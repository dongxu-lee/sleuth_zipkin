package com.ldx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.sql.DataSource;

/**
 * oauth2server配置类，需要继承特定父类
 */
@Configuration
@EnableAuthorizationServer //开启认证服务器
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private LdxAccessTokenConvertor accessTokenConvertor;

    /**
     * 认证服务器最终是以api接口的方式对外提供服务（校验合法性并生成令牌、校验令牌等）
     * 那么，以api接口方式对外的话，就会涉及到接口的访问权限，我们需要在这里进行必要的配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        super.configure(security);
        //相当于打开endpoints访问api接口的开关
        security.allowFormAuthenticationForClients() //允许客户端表单认证
                .tokenKeyAccess("permitAll()")       //开启端口/oauth/token_key的访问权限
                .checkTokenAccess("permitAll()");    //开启端口/oauth/check_token的访问权限
    }

    /**
     * 客户端详情配置
     * 比如client_id,secret
     * 当前服务就如同QQ平台，我的网站需要qq平台进行登陆授权认证，提前需要到qq平台注册，
     * qq平台会给我的网站颁发client_id等必要参数，表明客户端是谁
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        super.configure(clients);

//        //从内存获取
//        clients.inMemory() //客户端信息存储在什么地方:内存/数据库
//                .withClient("client_ldx") //添加一个client配置，指定其client_id
//                .secret("abcxyz")                //指定客户端的密码/安全吗
//                .resourceIds("consumer")         //指定客户端所能访问资源id清单，该id需要在具体的资源服务器上配置一样的
//                .authorizedGrantTypes("password", "refresh_token") //认证类型/令牌颁发模式，可以配置多个在这里，但不一定都有用
//                .scopes("all"); //客户端的权限范围

        //从jdbc获取
        clients.withClientDetails(createJdbcClientDetailsService());

    }

    @Autowired
    private DataSource dataSource;

    public JdbcClientDetailsService createJdbcClientDetailsService() {
        JdbcClientDetailsService jdbcClientDetailsService = new JdbcClientDetailsService(dataSource);
        return jdbcClientDetailsService;
    }

    /**
     * 配置token令牌管理相关
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        super.configure(endpoints);
        endpoints.tokenStore(tokenStore())   //指定token的存储方法
                .tokenServices(authorizationServerTokenServices())         //token服务的一个描述，可以认为是token生成细节的描述，比如有效时间多少等
                .authenticationManager(authenticationManager) //指定认证管理器，随后注入一个到当前类使用即可
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
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

    /**
     * token服务对象（描述了token有效期等信息）
     * @return
     */
    public AuthorizationServerTokenServices authorizationServerTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenStore(tokenStore());

        //针对jwt增加
        defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());

        // 设置令牌有效时间(一般2小时)
        defaultTokenServices.setAccessTokenValiditySeconds(20);
        //设置刷新令牌的有效时间
        defaultTokenServices.setRefreshTokenValiditySeconds(259200); //3天
        return defaultTokenServices;
    }
}
