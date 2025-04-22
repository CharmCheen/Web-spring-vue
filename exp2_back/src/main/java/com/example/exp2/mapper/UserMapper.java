package com.example.exp2.mapper;

import com.example.exp2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users")
    List<User> findAll();  // 获取所有用户

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);  // 根据 ID 查询用户

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);  // 根据用户名查询用户

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);  // 根据邮箱查询用户

    @Insert("INSERT INTO users (username, email, birthdate, address, postcode, password, avatar, province, city, district) VALUES (#{username}, #{email}, #{birthdate}, #{address}, #{postcode}, #{password}, #{avatar}, #{province}, #{city}, #{district})")
    void insert(User user);  // 插入用户

    @Update("UPDATE users SET username = #{username}, email = #{email}, birthdate = #{birthdate}, address = #{address}, postcode = #{postcode}, password = #{password}, avatar = #{avatar}, province = #{province}, city = #{city}, district = #{district} WHERE id = #{id}")
    void update(User user);  // 更新用户信息

    @Delete("DELETE FROM users WHERE id = #{id}")
    void deleteById(Long id);  // 删除用户

    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{query}, '%') OR email LIKE CONCAT('%', #{query}, '%') LIMIT 10")
    List<User> searchUsers(String query);  // 模糊搜索用户
}
