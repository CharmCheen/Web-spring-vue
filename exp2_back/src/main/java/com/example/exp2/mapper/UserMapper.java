package com.example.exp2.mapper;

import com.example.exp2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll();  // 获取所有用户

    User findById(Long id);  // 根据 ID 查询用户

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);  // 根据用户名查询用户

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);  // 根据邮箱查询用户

    void insert(User user);  // 插入用户

    void update(User user);  // 更新用户信息

    void deleteById(Long id);  // 删除用户

    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{query}, '%') OR email LIKE CONCAT('%', #{query}, '%') LIMIT 10")
    List<User> searchUsers(String query);  // 模糊搜索用户

    /**
     * 批量查询用户
     * 注：这里需要使用MyBatis的动态SQL功能，通过XML配置实现
     */
    List<User> findUsersByUsernames(List<String> usernames);  // 根据多个用户名查询用户

    /**
     * 仅更新用户密码
     * @param username 用户名
     * @param newPassword 新密码
     * @return 影响的行数
     */
    @org.apache.ibatis.annotations.Update("UPDATE users SET password = #{newPassword} WHERE username = #{username}")
    int updatePassword(String username, String newPassword);

    /**
     * 通过用户名删除用户
     * @param username 用户名
     * @return 影响的行数
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM users WHERE username = #{username}")
    int deleteByUsername(String username);
}
