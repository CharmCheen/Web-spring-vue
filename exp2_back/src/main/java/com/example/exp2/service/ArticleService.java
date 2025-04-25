package com.example.exp2.service;

import com.example.exp2.entity.Article;
import com.example.exp2.mapper.ArticleMapper;
import com.example.exp2.mapper.SubscriptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
    public void deleteArticle(Long articleId) {
        articleMapper.deleteById(articleId);
    }

    public Article getArticleById(Long articleId) {
        return articleMapper.selectById(articleId);
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
} 