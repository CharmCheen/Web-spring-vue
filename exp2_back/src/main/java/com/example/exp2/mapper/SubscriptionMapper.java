package com.example.exp2.mapper;

import com.example.exp2.entity.Subscription;
import com.example.exp2.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

@Mapper
public interface SubscriptionMapper {
    // 添加关注关系
    int subscribe(@Param("subscriberUsername") String subscriberUsername, @Param("publisherUsername") String publisherUsername);

    // 取消关注关系
    int unsubscribe(@Param("subscriberUsername") String subscriberUsername, @Param("publisherUsername") String publisherUsername);

    // 检查是否已关注
    int isSubscribed(@Param("subscriberUsername") String subscriberUsername, @Param("publisherUsername") String publisherUsername);

    // 获取用户的关注列表
    Set<String> findFollowedUsers(@Param("username") String username);

    // 获取关注用户的详细信息
    List<User> getSubscribedUsers(String username);

    // 搜索用户（模糊搜索）
    List<User> searchUsers(String query);
}