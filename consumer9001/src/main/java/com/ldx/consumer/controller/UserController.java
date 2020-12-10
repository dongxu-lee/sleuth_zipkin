package com.ldx.consumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/getUser")
    public String getUser(Integer id) {
        return restTemplate.getForObject("http://provider/getUser?id=" + id, String.class);
    }

    @RequestMapping("/consumer/gateway")
    public String gateway() {
        Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
        // 获取到jwt传来的数据
        System.out.println(details);
        return "consumer-gateway";
    }

    @RequestMapping("/demo/gateway")
    public String demogateway() {
        return "demo-gateway";
    }

    @RequestMapping("/test/gateway")
    public String testgateway() {
        return "test-gateway";
    }


}
