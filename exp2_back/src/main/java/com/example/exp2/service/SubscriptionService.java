package com.example.exp2.service;

import com.example.exp2.entity.Subscription;
import com.example.exp2.mapper.SubscriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Transactional
    public void subscribe(Subscription subscription) {
        if (subscriptionMapper.exists(subscription) == 0) {
            subscriptionMapper.insert(subscription);
        }
    }

    @Transactional
    public void unsubscribe(Subscription subscription) {
        subscriptionMapper.delete(subscription);
    }

    public List<Subscription> getSubscriptions(String username) {
        return subscriptionMapper.selectSubscriptions(username);
    }

    public List<Subscription> getFans(String username) {
        return subscriptionMapper.selectFans(username);
    }
} 