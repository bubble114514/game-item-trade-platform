-- 游戏道具交易平台 — 各服务独立库（论文：按领域拆分库，可配合用户 ID 分表扩展）
CREATE DATABASE IF NOT EXISTS game_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS game_item DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS game_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 以下为示意 DDL；开发环境可使用 JPA ddl-auto=update 自动建表
-- 生产环境建议固定版本迁移（Flyway/Liquibase）

USE game_user;
CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    nickname VARCHAR(32),
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB;

-- 水平分表示意（按 user_id 取模路由，需 ShardingSphere 等中间件落地）
-- CREATE TABLE user_account_0 LIKE user_account;
-- CREATE TABLE user_account_1 LIKE user_account;

USE game_item;
CREATE TABLE IF NOT EXISTS game_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    game VARCHAR(32),
    category VARCHAR(32),
    reference_price DECIMAL(12,2) NOT NULL,
    description VARCHAR(512)
) ENGINE=InnoDB;

USE game_trade;
CREATE TABLE IF NOT EXISTS trade_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
) ENGINE=InnoDB;
