package com.ldx.provider7001.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/getUser")
    public String getUser(Integer id) {
        Map<String , Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", "admin"+id);
        map.put("age", 10*id);
        map.put("port", 7001);
        return map.toString();
    }

}
