package com.example.exp2.service;

import com.example.exp2.entity.Article;
import com.example.exp2.mapper.ArticleMapper;
import com.example.exp2.mapper.SubscriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.exp2.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Transactional
    public Article createArticle(Article article) {
        articleMapper.insert(article);
        return article;
    }

    @Transactional
    public Article updateArticle(Article article) {
        articleMapper.update(article);
        return article;
    }

    @Transactional
    public boolean deleteArticle(Long articleId, String authorUsername) {
        try {
            int result = articleMapper.deleteByIdAndAuthor(articleId, authorUsername);
            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除文章失败：" + e.getMessage());
        }
    }

    public Article getArticleById(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new ResourceNotFoundException("文章不存在，ID: " + articleId);
        }
        return article;
    }

    public List<Article> getArticlesByAuthor(String username) {
        return articleMapper.selectByAuthor(username);
    }

    public List<Article> getFeed(String username) {
        return articleMapper.selectFeed(username);
    }

    public List<Article> getFollowedArticles(String username) {
        // 1. 获取当前用户关注的所有用户
        Set<String> followedUsers = subscriptionMapper.findFollowedUsers(username);
        
        // 2. 如果没有关注任何用户，返回空列表
        if (followedUsers == null || followedUsers.isEmpty()) {
            return List.of();
        }
        
        // 3. 获取所有关注用户的文章，并按时间排序
        return articleMapper.selectArticlesByAuthors(followedUsers);
    }

    public List<Article> getRecommendedArticles() {
        // 获取最新的10篇文章作为推荐
        return articleMapper.selectRecentArticles(10);
    }

    public Map<String, Object> getArticleStats(String username) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取全平台文章总数
        long totalArticles = articleMapper.countTotalArticles();
        System.out.println("Debug - 总文章数: " + totalArticles);
        
        // 获取本月全平台发文数
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long monthlyArticles = articleMapper.countTotalArticlesAfter(startOfMonth);
        System.out.println("Debug - 本月文章数: " + monthlyArticles);
        
        // 获取上月全平台发文数，用于计算增长率
        LocalDateTime startOfLastMonth = startOfMonth.minusMonths(1);
        long lastMonthArticles = articleMapper.countTotalArticlesBetween(
            startOfLastMonth, startOfMonth);
        System.out.println("Debug - 上月文章数: " + lastMonthArticles);
        
        // 获取活跃作者数据
        List<Map<String, Object>> activeAuthors = articleMapper.findTopAuthors(10);
        System.out.println("Debug - 活跃作者: " + activeAuthors);
        
        // 计算增长率
        double growthRate = lastMonthArticles == 0 ? 0 : 
            ((double)(monthlyArticles - lastMonthArticles) / lastMonthArticles) * 100;
        
        stats.put("totalArticles", totalArticles);
        stats.put("monthlyArticles", monthlyArticles);
        stats.put("growthRate", Math.round(growthRate * 10.0) / 10.0); // 保留一位小数
        stats.put("activeAuthors", activeAuthors);
        
        return stats;
    }

    public Map<String, Object> getArticleTrend(String username) {
        Map<String, Object> trend = new HashMap<>();
        
        // 获取全平台月度统计数据
        List<Map<String, Object>> monthlyData = articleMapper.findMonthlyTotalArticleCounts();
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        
        // 处理数据
        for (Map<String, Object> month : monthlyData) {
            String monthStr = month.get("month").toString();
            labels.add(monthStr.substring(5) + "月"); // 只显示月份
            data.add(Long.parseLong(month.get("count").toString()));
        }
        
        trend.put("labels", labels);
        trend.put("data", data);
        
        return trend;
    }

    public Map<String, Object> getArticleCategories(String username) {
        Map<String, Object> categories = new HashMap<>();
        
        // 获取文章分类统计
        List<Map<String, Object>> categoryStats = articleMapper.findArticleCategoryCounts(username);
        
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        List<String> colors = Arrays.asList("#007AFF", "#34C759", "#FF9500", "#FF3B30");
        
        for (int i = 0; i < categoryStats.size(); i++) {
            Map<String, Object> stat = categoryStats.get(i);
            labels.add((String)stat.get("category"));
            data.add((Long)stat.get("count"));
        }
        
        categories.put("labels", labels);
        categories.put("data", data);
        categories.put("colors", colors);
        
        return categories;
    }

    /**
     * 通过文章标题和作者名删除文章
     * @param title 文章标题
     * @param authorUsername 作者用户名
     * @return 是否删除成功
     */
    public boolean deleteArticleByTitleAndAuthor(String title, String authorUsername) {
        return articleMapper.deleteByAuthorAndTitle(authorUsername, title) > 0;
    }
}