package com.example.exp2.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Article {
    private Long articleId;
    private String authorUsername;
    private String title;
    private String content;
    private String coverImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 