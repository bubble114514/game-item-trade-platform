-- 游戏道具交易平台 — 各服务独立库（论文：按领域拆分库，可配合用户 ID 分表扩展）
CREATE DATABASE IF NOT EXISTS game_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS game_item DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS game_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS game_message DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 以下为示意 DDL；开发环境可使用 JPA ddl-auto=update 自动建表
-- 生产环境建议固定版本迁移（Flyway/Liquibase）

USE game_user;
-- 用户账户表
CREATE TABLE IF NOT EXISTS user_account (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password_hash VARCHAR(128) NOT NULL,
    nickname VARCHAR(32),
    role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, MERCHANT-商家, ADMIN-管理员',
    email VARCHAR(128),
    phone VARCHAR(20),
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB;

-- 用户钱包表
CREATE TABLE IF NOT EXISTS user_wallet (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
    frozen_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

-- 水平分表示意（按 user_id 取模路由，需 ShardingSphere 等中间件落地）
-- CREATE TABLE user_account_0 LIKE user_account;
-- CREATE TABLE user_account_1 LIKE user_account;

USE game_item;
-- 游戏道具表（基础信息）
CREATE TABLE IF NOT EXISTS game_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL COMMENT '道具名称',
    game VARCHAR(64) NOT NULL COMMENT '所属游戏',
    category VARCHAR(32) COMMENT '道具分类',
    reference_price DECIMAL(12,2) COMMENT '参考价格',
    description VARCHAR(512) COMMENT '道具描述',
    icon_url VARCHAR(256) COMMENT '道具图标',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)
) ENGINE=InnoDB;

-- 道具挂牌表（用户发布的交易）
CREATE TABLE IF NOT EXISTS item_listing (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '道具ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    price DECIMAL(12,2) NOT NULL COMMENT '售价',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    trade_mode VARCHAR(16) NOT NULL DEFAULT 'FIXED' COMMENT '交易模式: FIXED-一口价, AUCTION-拍卖',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-在售, SOLD-已售, CANCELLED-已取消',
    description VARCHAR(512) COMMENT '卖家描述',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    INDEX idx_item_id (item_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status)
) ENGINE=InnoDB;

USE game_trade;
-- 交易订单表
CREATE TABLE IF NOT EXISTS trade_order (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    item_id BIGINT NOT NULL COMMENT '道具ID',
    listing_id BIGINT COMMENT '挂牌ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price DECIMAL(12,2) NOT NULL COMMENT '单价',
    total_amount DECIMAL(12,2) NOT NULL COMMENT '总价',
    status VARCHAR(16) NOT NULL DEFAULT 'CREATED' COMMENT '状态: CREATED-已创建, PAID-已支付, DELIVERED-已发货, COMPLETED-已完成, CANCELLED-已取消',
    trade_mode VARCHAR(16) NOT NULL DEFAULT 'FIXED' COMMENT '交易模式: FIXED-一口价, LISTING-挂牌',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    paid_at TIMESTAMP(3) COMMENT '支付时间',
    completed_at TIMESTAMP(3) COMMENT '完成时间',
    INDEX idx_order_no (order_no),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- 交易流水表
CREATE TABLE IF NOT EXISTS trade_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(16) NOT NULL COMMENT '类型: PAY-支付, REFUND-退款, INCOME-收入',
    amount DECIMAL(12,2) NOT NULL COMMENT '金额',
    balance_before DECIMAL(15,2) NOT NULL COMMENT '变动前余额',
    balance_after DECIMAL(15,2) NOT NULL COMMENT '变动后余额',
    remark VARCHAR(256) COMMENT '备注',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_order_id (order_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB;

-- 消息服务数据库
USE game_message;
-- 用户消息表
CREATE TABLE IF NOT EXISTS user_messages (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type VARCHAR(32) NOT NULL COMMENT '消息类型: ORDER_CREATED-订单创建, ORDER_PAID-已支付, ORDER_DELIVERED-已发货, ORDER_COMPLETED-交易完成, ORDER_CANCELLED-订单取消',
    title VARCHAR(128) NOT NULL COMMENT '消息标题',
    content VARCHAR(512) COMMENT '消息内容',
    order_id BIGINT COMMENT '关联订单ID',
    related_user_id BIGINT COMMENT '关联用户ID（对方用户）',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    INDEX idx_user_id (user_id),
    INDEX idx_user_is_read (user_id, is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB;
