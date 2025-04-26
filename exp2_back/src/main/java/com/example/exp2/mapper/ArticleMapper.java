package com.example.exp2.mapper;

import com.example.exp2.entity.Article;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    int deleteById(@Param("articleId") Long articleId);

    @Delete("DELETE FROM articles WHERE article_id = #{articleId} AND author_username = #{authorUsername}")
    int deleteByIdAndAuthor(@Param("articleId") Long articleId, @Param("authorUsername") String authorUsername);

    @Select("SELECT * FROM articles WHERE article_id = #{articleId}")
    Article selectById(Long articleId);

    @Select("SELECT * FROM articles WHERE author_username = #{username} ORDER BY created_at DESC")
    List<Article> selectByAuthor(String username);

    @Select("SELECT a.* FROM articles a " +
            "JOIN subscriptions s ON a.author_username = s.publisher_username " +
            "WHERE s.subscriber_username = #{username} " +
            "ORDER BY a.created_at DESC")
    List<Article> selectFeed(String username);

    @Select("SELECT a.* FROM articles a " +
            "JOIN subscriptions s ON a.author_username = s.publisher_username " +
            "WHERE s.subscriber_username = #{username} " +
            "ORDER BY a.created_at DESC")
    List<Article> selectFollowedArticles(String username);

    @Select({
        "<script>",
        "SELECT * FROM articles",
        "WHERE author_username IN",
        "<foreach collection='authors' item='author' open='(' separator=',' close=')'>",
        "#{author}",
        "</foreach>",
        "ORDER BY created_at DESC",
        "</script>"
    })
    List<Article> selectArticlesByAuthors(@Param("authors") Set<String> authors);

    @Select("SELECT * FROM articles ORDER BY created_at DESC LIMIT #{limit}")
    List<Article> selectRecentArticles(@Param("limit") int limit);

    @Select("SELECT COUNT(1) FROM articles")
    long countTotalArticles();

    @Select("SELECT COUNT(1) FROM articles WHERE created_at >= #{startDate}")
    long countTotalArticlesAfter(@Param("startDate") LocalDateTime startDate);

    @Select("SELECT COUNT(1) FROM articles WHERE created_at >= #{startDate} AND created_at < #{endDate}")
    long countTotalArticlesBetween(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Select("SELECT author_username as name, COUNT(1) as articles " +
            "FROM articles " +
            "GROUP BY author_username " +
            "ORDER BY articles DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> findTopAuthors(int limit);

    @Select("SELECT DATE_FORMAT(created_at, '%Y-%m') as month, COUNT(1) as count " +
            "FROM articles " +
            "GROUP BY DATE_FORMAT(created_at, '%Y-%m') " +
            "ORDER BY month DESC " +
            "LIMIT 6")
    List<Map<String, Object>> findMonthlyTotalArticleCounts();

    @Select("SELECT CASE " +
            "WHEN title LIKE '%技术%' THEN '技术' " +
            "WHEN title LIKE '%生活%' THEN '生活' " +
            "WHEN title LIKE '%学习%' THEN '学习' " +
            "WHEN title LIKE '%工作%' THEN '工作' " +
            "ELSE '其他' END as category, " +
            "COUNT(1) as count " +
            "FROM articles " +
            "WHERE author_username = #{username} " +
            "GROUP BY category")
    List<Map<String, Object>> findArticleCategoryCounts(String username);

    @Delete("DELETE FROM articles WHERE author_username = #{authorUsername} AND title = #{title}")
    int deleteByAuthorAndTitle(@Param("authorUsername") String authorUsername, @Param("title") String title);

    /**
     * 通过标题和作者查找文章
     * @param title 文章标题
     * @param authorUsername 作者用户名
     * @return 匹配的文章列表
     */
    List<Article> findByTitleAndAuthor(@Param("title") String title, @Param("authorUsername") String authorUsername);
}