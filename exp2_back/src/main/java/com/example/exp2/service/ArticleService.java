package com.example.exp2.service;

import com.example.exp2.entity.Article;
import com.example.exp2.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

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
} 