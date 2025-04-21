package com.example.exp2.mapper;

import com.example.exp2.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleMapper {
    @Insert("INSERT INTO articles (author_username, title, content, cover_image) " +
            "VALUES (#{authorUsername}, #{title}, #{content}, #{coverImage})")
    @Options(useGeneratedKeys = true, keyProperty = "articleId")
    int insert(Article article);

    @Update("UPDATE articles SET title = #{title}, content = #{content}, " +
            "cover_image = #{coverImage} WHERE article_id = #{articleId}")
    int update(Article article);

    @Delete("DELETE FROM articles WHERE article_id = #{articleId}")
    int deleteById(Long articleId);

    @Select("SELECT * FROM articles WHERE article_id = #{articleId}")
    Article selectById(Long articleId);

    @Select("SELECT * FROM articles WHERE author_username = #{username} ORDER BY created_at DESC")
    List<Article> selectByAuthor(String username);

    @Select("SELECT a.* FROM articles a " +
            "JOIN subscriptions s ON a.author_username = s.publisher_username " +
            "WHERE s.subscriber_username = #{username} " +
            "ORDER BY a.created_at DESC")
    List<Article> selectFeed(String username);
} 