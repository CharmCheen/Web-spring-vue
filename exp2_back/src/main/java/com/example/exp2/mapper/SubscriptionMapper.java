package com.example.exp2.mapper;

import com.example.exp2.entity.Subscription;
import com.example.exp2.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SubscriptionMapper {

    // 添加关注关系
    void addSubscription(Subscription subscription);

    // 取消关注关系
    void removeSubscription(String subscriberUsername, String publisherUsername);

    // 检查是否已关注
    boolean isSubscribed(String subscriberUsername, String publisherUsername);

    // 获取用户的关注列表
    List<Subscription> getSubscriptionsByUser(String username);

    // 获取关注用户的详细信息
    List<User> getSubscribedUsers(String username);

    // 搜索用户（模糊搜索）
    List<User> searchUsers(String query);
}