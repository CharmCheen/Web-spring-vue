package com.example.exp2.controller;

import com.example.exp2.entity.Article;
import com.example.exp2.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }

    @GetMapping("/followed")
    public ResponseEntity<List<Article>> getFollowedArticles(@RequestHeader("Authorization") String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        // 直接使用token作为用户名，因为我们在登录时将用户名存为token
        String username = token;
        return ResponseEntity.ok(articleService.getFollowedArticles(username));
    }

    @GetMapping("/author/{username}")
    public ResponseEntity<List<Article>> getArticlesByAuthor(@PathVariable String username) {
        return ResponseEntity.ok(articleService.getArticlesByAuthor(username));
    }

    @GetMapping("/feed/{username}")
    public ResponseEntity<List<Article>> getFeed(@PathVariable String username) {
        return ResponseEntity.ok(articleService.getFeed(username));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        article.setArticleId(id);
        return ResponseEntity.ok(articleService.updateArticle(article));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<Article>> getRecommendedArticles() {
        return ResponseEntity.ok(articleService.getRecommendedArticles());
    }
} 