package com.example.exp2.entity;

import java.time.LocalDateTime;

public class Subscription {
    private String subscriberUsername;  // 关注者的用户名
    private String publisherUsername;   // 被关注者的用户名
    private LocalDateTime createdAt;    // 关注时间

    // 无参构造函数
    public Subscription() {
    }

    // 带参构造函数
    public Subscription(String subscriberUsername, String publisherUsername) {
        this.subscriberUsername = subscriberUsername;
        this.publisherUsername = publisherUsername;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getSubscriberUsername() {
        return subscriberUsername;
    }

    public void setSubscriberUsername(String subscriberUsername) {
        this.subscriberUsername = subscriberUsername;
    }

    public String getPublisherUsername() {
        return publisherUsername;
    }

    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}