package com.ldx.config;

import com.ldx.service.JdbcUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

/**
 * 该配置类主要处理用户名和密码的校验等事宜
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 注册一个认证管理器对象到容器
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 加密策略是：不加密
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcUserDetailsService jdbcUserDetailsService;

    /**
     * 处理用户名和密码
     * 1）客户端传递username和password参数到认证服务器
     * 2）一般来说，username和password会存储在数据库的用户表里
     * 3）根据用户表里的数据，验证当前传递过来的用户信息的合法性
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //在这个方法就可以关联数据库了，这里先把用户信息保存在内存
        //实例化一个用户对象（相当于数据库中的一条用户记录）
//        UserDetails user = new User("admin", "123456", new ArrayList<>());
//        auth.inMemoryAuthentication()
//                .withUser(user).passwordEncoder(passwordEncoder);
        auth.userDetailsService(jdbcUserDetailsService).passwordEncoder(passwordEncoder);
    }
}
