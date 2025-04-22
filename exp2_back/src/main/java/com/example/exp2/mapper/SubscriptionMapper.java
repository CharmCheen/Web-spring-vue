package com.example.exp2.mapper;

import com.example.exp2.entity.Subscription;
import com.example.exp2.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubscriptionMapper {

    // 添加关注关系
    @Insert("INSERT INTO subscription (subscriber_username, publisher_username, created_at) VALUES (#{subscriberUsername}, #{publisherUsername}, #{createdAt})")
    void addSubscription(Subscription subscription);

    // 取消关注关系
    @Delete("DELETE FROM subscription WHERE subscriber_username = #{subscriberUsername} AND publisher_username = #{publisherUsername}")
    void removeSubscription(String subscriberUsername, String publisherUsername);

    // 检查是否已关注
    @Select("SELECT COUNT(*) FROM subscription WHERE subscriber_username = #{subscriberUsername} AND publisher_username = #{publisherUsername}")
    boolean isSubscribed(String subscriberUsername, String publisherUsername);

    // 获取用户的关注列表
    @Select("SELECT * FROM subscription WHERE subscriber_username = #{username}")
    List<Subscription> getSubscriptionsByUser(String username);

    // 获取关注用户的详细信息
    @Select("SELECT u.* FROM user u JOIN subscription s ON u.username = s.publisher_username WHERE s.subscriber_username = #{username}")
    List<User> getSubscribedUsers(String username);

    // 搜索用户（模糊搜索）
    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{query}, '%') LIMIT 10")
    List<User> searchUsers(String query);
}