package com.example.demo.service;


import com.example.demo.pojo.User;

import java.util.List;

//业务处理接口，承载了Dao层里面的接口
public interface UserService {
    public List<User> select();
    public  User login(User user);
}
