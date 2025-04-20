package com.example.exp2.service;

import com.example.exp2.entity.User;
import com.example.exp2.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    // 获取所有用户
    public List<User> findAllUsers() {
        return userMapper.findAll();
    }

    // 根据 ID 获取用户
    public User findUserById(Long id) {
        return userMapper.findById(id);
    }

    // 校验用户名是否已存在
    public boolean isUsernameExist(String username) {
        return userMapper.findByUsername(username) != null;
    }

    // 校验邮箱是否已存在
    public boolean isEmailExist(String email) {
        return userMapper.findByEmail(email) != null;
    }

    // 添加用户
    public void addUser(User user) {
        userMapper.insert(user);
    }

    // 更新用户信息
    public void updateUser(User user) {
        userMapper.update(user);
    }

    // 删除用户
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userMapper.findByUsername(username) != null;
    }

    public boolean existsByEmail(String email) {
        return userMapper.findByEmail(email) != null;
    }

    public void save(User user) {
        userMapper.insert(user);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }
}
