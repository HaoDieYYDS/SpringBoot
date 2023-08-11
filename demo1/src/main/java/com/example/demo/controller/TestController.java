package com.example.demo.controller;

import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RequestMapping("/user")
@RestController
public class TestController {

    @Autowired
    UserService userService;
    @RequestMapping("/test")
    public List<User> test()
    {
        List<User> list = userService.select();
        return list;
    }

    @RequestMapping("/login")
    public User login(@RequestBody User user)
    {
        User user1 =   userService.login(user);
        if(user1!=null)
        return userService.login(user);
        else {
            System.out.println("账号密码有误，请重新输入！");
            return null;
        }
    }
}
