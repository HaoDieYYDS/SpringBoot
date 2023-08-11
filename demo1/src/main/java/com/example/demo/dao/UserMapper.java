package com.example.demo.dao;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

//数据库实现接口
@Mapper
public interface UserMapper {
    public List<User> select();

    public  User login(User user);
}
