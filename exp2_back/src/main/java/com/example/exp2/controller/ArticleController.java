package com.example.exp2.controller;

import com.example.exp2.entity.Article;
import com.example.exp2.model.ErrorResponse;
import com.example.exp2.service.ArticleService;
import com.example.exp2.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

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

    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(@PathVariable("articleId") Long articleId,
                                          @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 如果没有提供token，返回403错误
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("未提供认证信息，无法删除文章"));
            }
            
            // 从token中提取用户名（在这个应用中，token就是用户名）
            String username = token.startsWith("Bearer ") ? token.substring(7) : token;
            
            // 记录日志，便于调试
            System.out.println("删除文章请求 - 用户: " + username + ", 文章ID: " + articleId);
            
            // 调用service层进行删除
            boolean deleted = articleService.deleteArticle(articleId, username);
            
            // 根据删除结果返回不同的响应
            if (deleted) {
                return ResponseEntity.ok()
                    .body(new ErrorResponse("文章删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("文章不存在或您无权删除此文章"));
            }
        } catch (Exception e) {
            // 打印详细的错误堆栈信息，便于调试
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("删除文章失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/delete-by-title")
    public ResponseEntity<?> deleteArticleByTitle(
            @RequestParam("title") String title,
            @RequestParam("authorUsername") String authorUsername) {
        try {
            boolean deleted = articleService.deleteArticleByTitleAndAuthor(title, authorUsername);
            if (deleted) {
                return ResponseEntity.ok().body(new ErrorResponse("文章删除成功"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("文章不存在或已被删除"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("删除文章失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/recommended")
    public ResponseEntity<List<Article>> getRecommendedArticles() {
        return ResponseEntity.ok(articleService.getRecommendedArticles());
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getArticleStats() {
        try {
            // 获取当前用户
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            
            // 获取用户的文章统计数据
            Map<String, Object> stats = articleService.getArticleStats(username);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取文章统计失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<?> getArticleTrend() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> trend = articleService.getArticleTrend(username);
            return ResponseEntity.ok(trend);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取文章趋势数据失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/categories")
    public ResponseEntity<?> getArticleCategories() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Map<String, Object> categories = articleService.getArticleCategories(username);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取文章分类统计失败: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("服务器内部错误: " + e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("请求参数错误: " + e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("资源未找到: " + e.getMessage()));
    }
}