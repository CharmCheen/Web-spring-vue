package com.example.exp2.controller;

import com.example.exp2.entity.Subscription;
import com.example.exp2.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> subscribe(@RequestBody Subscription subscription) {
        subscriptionService.subscribe(subscription);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@RequestBody Subscription subscription) {
        subscriptionService.unsubscribe(subscription);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my/{username}")
    public ResponseEntity<List<Subscription>> getSubscriptions(@PathVariable String username) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(username));
    }

    @GetMapping("/fans/{username}")
    public ResponseEntity<List<Subscription>> getFans(@PathVariable String username) {
        return ResponseEntity.ok(subscriptionService.getFans(username));
    }
} 