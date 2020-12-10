package com.ldx.service;

import com.ldx.pojo.Users;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JdbcUserDetailsService implements UserDetailsService {
    /**
     * 根据username查询出该用户的所有信息，封装成userDetails对象，至于密码，框架会自动匹配
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //此处表示根据用户名从数据库去除用户数据，此处省略sql相关复杂操作，直接写死
        //Users users = usersRepository.findByUsername(username);
        Users users = new Users();
        users.setId(1L);
        users.setUsername("admin");
        users.setPassword("123456");
        return new User(username, users.getPassword(), new ArrayList<>());
    }
}
