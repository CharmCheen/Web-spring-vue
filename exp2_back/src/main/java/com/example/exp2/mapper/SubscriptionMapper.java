package com.example.exp2.mapper;

import com.example.exp2.entity.Subscription;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SubscriptionMapper {
    @Insert("INSERT INTO subscriptions (subscriber_username, publisher_username) " +
            "VALUES (#{subscriberUsername}, #{publisherUsername})")
    int insert(Subscription subscription);

    @Delete("DELETE FROM subscriptions WHERE subscriber_username = #{subscriberUsername} " +
            "AND publisher_username = #{publisherUsername}")
    int delete(Subscription subscription);

    @Select("SELECT * FROM subscriptions WHERE subscriber_username = #{username}")
    List<Subscription> selectSubscriptions(String username);

    @Select("SELECT * FROM subscriptions WHERE publisher_username = #{username}")
    List<Subscription> selectFans(String username);

    @Select("SELECT COUNT(*) FROM subscriptions WHERE subscriber_username = #{subscriberUsername} " +
            "AND publisher_username = #{publisherUsername}")
    int exists(Subscription subscription);
} 