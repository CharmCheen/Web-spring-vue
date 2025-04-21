package com.example.exp2.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Subscription {
    private String subscriberUsername;
    private String publisherUsername;
    private LocalDateTime createdAt;
} 