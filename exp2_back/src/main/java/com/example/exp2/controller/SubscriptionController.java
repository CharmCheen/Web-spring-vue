package com.example.exp2.controller;

import com.example.exp2.entity.Subscription;
import com.example.exp2.entity.User;
import com.example.exp2.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * 关注用户
     */
    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody Map<String, String> request) {
        String subscriberUsername = request.get("subscriberUsername");
        String publisherUsername = request.get("publisherUsername");

        if (subscriberUsername == null || publisherUsername == null) {
            return ResponseEntity.badRequest().body("关注者和被关注者用户名不能为空");
        }

        boolean success = subscriptionService.subscribeUser(subscriberUsername, publisherUsername);
        
        if (success) {
            return ResponseEntity.ok("关注成功");
        } else {
            return ResponseEntity.badRequest().body("关注失败，可能已经关注过该用户或用户不存在");
        }
    }

    /**
     * 取消关注用户
     */
    @DeleteMapping("/unfollow")
    public ResponseEntity<?> unfollowUser(@RequestParam String subscriberUsername, @RequestParam String publisherUsername) {
        if (subscriberUsername == null || publisherUsername == null) {
            return ResponseEntity.badRequest().body("关注者和被关注者用户名不能为空");
        }

        boolean success = subscriptionService.unsubscribeUser(subscriberUsername, publisherUsername);
        
        if (success) {
            return ResponseEntity.ok("取消关注成功");
        } else {
            return ResponseEntity.badRequest().body("取消关注失败");
        }
    }

    /**
     * 获取用户的关注列表
     */
    @GetMapping("/my/{username}")
    public ResponseEntity<?> getMySubscriptions(@PathVariable String username) {
        try {
            System.out.println("获取用户关注列表: " + username); // 调试日志
            if (username == null || username.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("用户名不能为空");
            }
            
            List<Subscription> subscriptions = subscriptionService.getSubscriptionsByUser(username);
            
            // 保证返回的数据不为null
            if (subscriptions == null) {
                subscriptions = new ArrayList<>();
            }
            
            System.out.println("找到关注数量: " + subscriptions.size()); // 调试日志
            // 安全检查每个对象
            for (Subscription sub : subscriptions) {
                if (sub.getPublisherUsername() == null) {
                    System.out.println("警告: 找到null发布者的订阅"); // 调试日志
                }
            }
            
            return ResponseEntity.ok(subscriptions);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("获取关注列表异常: " + e.getMessage()); // 调试日志
            return ResponseEntity.status(500).body("获取关注列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户关注的用户详情
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getSubscribedUsers(@PathVariable String username) {
        List<User> users = subscriptionService.getSubscribedUsers(username);
        return ResponseEntity.ok(users);
    }

    /**
     * 搜索用户（模糊搜索）
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("搜索关键词不能为空");
        }
        
        List<User> users = subscriptionService.searchUsers(query);
        return ResponseEntity.ok(users);
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkSubscription(
            @RequestParam String subscriberUsername, 
            @RequestParam String publisherUsername) {
        
        boolean isSubscribed = subscriptionService.isSubscribed(subscriberUsername, publisherUsername);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("isSubscribed", isSubscribed);
        
        return ResponseEntity.ok(result);
    }
}