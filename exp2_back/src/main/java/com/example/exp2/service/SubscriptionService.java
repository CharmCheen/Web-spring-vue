package com.example.exp2.service;

import com.example.exp2.entity.Subscription;
import com.example.exp2.entity.User;
import com.example.exp2.mapper.SubscriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    /**
     * 关注用户（新版本）
     * @param subscription 订阅关系
     */
    @Transactional
    public void subscribe(Subscription subscription) {
        // 检查是否已经关注
        if (subscriptionMapper.isSubscribed(subscription.getSubscriberUsername(), subscription.getPublisherUsername()) == 0) {
            // 没有关注则添加关注
            subscriptionMapper.subscribe(subscription.getSubscriberUsername(), subscription.getPublisherUsername());
        }
    }

    /**
     * 取消关注（新版本）
     * @param subscription 订阅关系
     */
    @Transactional
    public void unsubscribe(Subscription subscription) {
        subscriptionMapper.unsubscribe(subscription.getSubscriberUsername(), subscription.getPublisherUsername());
    }

    /**
     * 获取用户的关注列表（新版本）
     * @param username 用户名
     * @return 关注列表
     */
    public List<Subscription> getSubscriptions(String username) {
        return subscriptionMapper.findFollowedUsers(username)
                .stream()
                .map(publisherUsername -> new Subscription(username, publisherUsername))
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的粉丝列表（新功能）
     * @param username 用户名
     * @return 粉丝列表
     */
    public List<User> getFans(String username) {
        // 此功能当前未实现，可以添加到 SubscriptionMapper 中
        // 暂时返回空列表
        return List.of();
    }

    /**
     * 关注用户
     * @param subscriberUsername 关注者用户名
     * @param publisherUsername 被关注者用户名
     * @return 成功返回true，失败返回false
     */
    public boolean subscribeUser(String subscriberUsername, String publisherUsername) {
        // 不能关注自己
        if (subscriberUsername.equals(publisherUsername)) {
            return false;
        }
        
        // 检查是否已经关注
        if (subscriptionMapper.isSubscribed(subscriberUsername, publisherUsername) > 0) {
            return false;
        }
        
        try {
            return subscriptionMapper.subscribe(subscriberUsername, publisherUsername) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 取消关注用户
     * @param subscriberUsername 关注者用户名
     * @param publisherUsername 被关注者用户名
     * @return 成功返回true，失败返回false
     */
    public boolean unsubscribeUser(String subscriberUsername, String publisherUsername) {
        try {
            return subscriptionMapper.unsubscribe(subscriberUsername, publisherUsername) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取关注列表
     * @param username 用户名
     * @return 关注列表
     */
    public List<Subscription> getSubscriptionsByUser(String username) {
        return subscriptionMapper.findFollowedUsers(username)
                .stream()
                .map(publisherUsername -> new Subscription(username, publisherUsername))
                .collect(Collectors.toList());
    }

    /**
     * 获取已关注用户的详细信息
     * @param username 用户名
     * @return 已关注用户列表
     */
    public List<User> getSubscribedUsers(String username) {
        return subscriptionMapper.getSubscribedUsers(username);
    }

    /**
     * 模糊搜索用户
     * @param query 搜索关键词
     * @return 用户列表
     */
    public List<User> searchUsers(String query) {
        return subscriptionMapper.searchUsers(query);
    }

    /**
     * 检查是否已关注
     * @param subscriberUsername 关注者用户名
     * @param publisherUsername 被关注者用户名
     * @return 是否已关注
     */
    public boolean isSubscribed(String subscriberUsername, String publisherUsername) {
        return subscriptionMapper.isSubscribed(subscriberUsername, publisherUsername) > 0;
    }
}