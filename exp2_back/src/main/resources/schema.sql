-- 创建subscription表
CREATE TABLE IF NOT EXISTS subscription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subscriber_username VARCHAR(255) NOT NULL,
    publisher_username VARCHAR(255) NOT NULL, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_subscription (subscriber_username, publisher_username)
);