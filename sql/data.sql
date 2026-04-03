-- 游戏道具交易平台 — 测试数据

-- 用户数据
USE game_user;

INSERT INTO user_account (id, username, password_hash, nickname, role, email, phone, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 'ADMIN', 'admin@gametrade.com', '13800000001', 1),
(2, 'merchant001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '游戏商家小王', 'MERCHANT', 'merchant001@gametrade.com', '13800000002', 1),
(3, 'merchant002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '道具商人小李', 'MERCHANT', 'merchant002@gametrade.com', '13800000003', 1),
(4, 'user001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '玩家小张', 'USER', 'user001@gametrade.com', '13800000004', 1),
(5, 'user002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '玩家小陈', 'USER', 'user002@gametrade.com', '13800000005', 1),
(6, 'user003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '玩家小刘', 'USER', 'user003@gametrade.com', '13800000006', 1);

INSERT INTO user_wallet (user_id, balance, frozen_amount) VALUES
(1, 100000.00, 0.00),
(2, 50000.00, 1000.00),
(3, 30000.00, 500.00),
(4, 5000.00, 200.00),
(5, 3000.00, 100.00),
(6, 2000.00, 0.00);

-- 道具数据
USE game_item;

INSERT INTO game_item (id, name, game, category, reference_price, description, icon_url) VALUES
(1, '屠龙刀', '热血传奇', '武器', 5000.00, '传说中的神兵利器，攻击力+999', 'https://example.com/icons/tulongdao.png'),
(2, '裁决之杖', '热血传奇', '武器', 3000.00, '法师专属神器，魔法攻击+888', 'https://example.com/icons/caizhizhang.png'),
(3, '复活戒指', '热血传奇', '饰品', 2000.00, '死亡后自动复活一次，冷却时间24小时', 'https://example.com/icons/fuhuojiezhi.png'),
(4, '麻痹戒指', '热血传奇', '饰品', 1800.00, '攻击时有10%概率使敌人麻痹3秒', 'https://example.com/icons/mabijiezhi.png'),
(5, '传送戒指', '热血传奇', '饰品', 1500.00, '可传送到任意已探索的地图', 'https://example.com/icons/chuansongjiezhi.png'),
(6, '极品龙纹剑', '热血传奇', '武器', 8000.00, '道士专属神剑，道术攻击+1200', 'https://example.com/icons/longwenjian.png'),
(7, '圣战戒指', '热血传奇', '饰品', 2500.00, '全属性+50，PVP必备神器', 'https://example.com/icons/shengzhanjiezhi.png'),
(8, '极品天魔神甲', '热血传奇', '防具', 6000.00, '战士专属铠甲，防御+500，HP+2000', 'https://example.com/icons/tianmoshenjia.png'),
(9, '极品法神披风', '热血传奇', '防具', 5500.00, '法师专属披风，魔法防御+600，MP+3000', 'https://example.com/icons/fashenpifeng.png'),
(10, '极品天尊道袍', '热血传奇', '防具', 5800.00, '道士专属道袍，防御+400，道术防御+500', 'https://example.com/icons/tianzundaopao.png'),
(11, '黑铁矿石', '热血传奇', '材料', 50.00, '用于装备强化的珍贵材料', 'https://example.com/icons/heitiekuangshi.png'),
(12, '祝福油', '热血传奇', '消耗品', 100.00, '使用后装备强化成功率提升10%', 'https://example.com/icons/zhufuyou.png'),
(13, '极品麻痹护腕', '热血传奇', '饰品', 3500.00, '麻痹戒指升级版，麻痹概率提升至15%', 'https://example.com/icons/mabihushou.png'),
(14, '极品复活项链', '热血传奇', '饰品', 4000.00, '复活戒指升级版，冷却时间缩短至12小时', 'https://example.com/icons/fuhuoxianglian.png'),
(15, '传送石', '热血传奇', '消耗品', 30.00, '可传送到指定安全区', 'https://example.com/icons/chuansongshi.png');

INSERT INTO item_listing (id, item_id, seller_id, price, quantity, trade_mode, status, description) VALUES
(1, 1, 2, 4800.00, 1, 'FIXED', 'ACTIVE', '屠龙刀，刚爆出来的，属性完美，需要的私聊'),
(2, 2, 2, 2900.00, 1, 'FIXED', 'ACTIVE', '裁决之杖，法师神器，价格可小刀'),
(3, 3, 3, 1900.00, 2, 'FIXED', 'ACTIVE', '复活戒指，有两枚，先到先得'),
(4, 4, 3, 1750.00, 1, 'FIXED', 'ACTIVE', '麻痹戒指，PK必备，手慢无'),
(5, 6, 2, 7500.00, 1, 'FIXED', 'ACTIVE', '极品龙纹剑，道士毕业武器，带强化+7'),
(6, 8, 3, 5800.00, 1, 'FIXED', 'ACTIVE', '天魔神甲，战士最强防具'),
(7, 9, 2, 5300.00, 1, 'FIXED', 'ACTIVE', '法神披风，法师毕业装备'),
(8, 10, 3, 5600.00, 1, 'FIXED', 'ACTIVE', '天尊道袍，道士毕业防具'),
(9, 11, 2, 45.00, 100, 'FIXED', 'ACTIVE', '黑铁矿石，大量出售，可批发'),
(10, 12, 3, 95.00, 50, 'FIXED', 'ACTIVE', '祝福油，批量出售，买10送1'),
(11, 13, 2, 3300.00, 1, 'FIXED', 'ACTIVE', '极品麻痹护腕，比戒指更强'),
(12, 14, 3, 3800.00, 1, 'FIXED', 'ACTIVE', '极品复活项链，冷却减半'),
(13, 1, 2, 5200.00, 1, 'FIXED', 'ACTIVE', '屠龙刀，强化+10，极品属性'),
(14, 7, 3, 2400.00, 1, 'FIXED', 'ACTIVE', '圣战戒指，全属性加成'),
(15, 15, 2, 28.00, 200, 'FIXED', 'ACTIVE', '传送石，大量现货，批发优惠');

-- 订单数据（示例）
USE game_trade;

INSERT INTO trade_order (id, order_no, buyer_id, seller_id, item_id, listing_id, quantity, unit_price, total_amount, status, trade_mode, created_at, paid_at, completed_at) VALUES
(1, 'O202604010001', 4, 2, 1, 1, 1, 4800.00, 4800.00, 'COMPLETED', 'FIXED', '2026-04-01 10:00:00', '2026-04-01 10:05:00', '2026-04-01 10:30:00'),
(2, 'O202604010002', 5, 3, 3, 3, 1, 1900.00, 1900.00, 'COMPLETED', 'FIXED', '2026-04-01 11:00:00', '2026-04-01 11:10:00', '2026-04-01 11:45:00'),
(3, 'O202604010003', 6, 2, 11, 9, 10, 45.00, 450.00, 'COMPLETED', 'FIXED', '2026-04-01 14:00:00', '2026-04-01 14:05:00', '2026-04-01 14:20:00'),
(4, 'O202604020001', 4, 3, 4, 4, 1, 1750.00, 1750.00, 'DELIVERED', 'FIXED', '2026-04-02 09:00:00', '2026-04-02 09:15:00', NULL),
(5, 'O202604020002', 5, 2, 12, 10, 5, 95.00, 475.00, 'PAID', 'FIXED', '2026-04-02 10:30:00', '2026-04-02 10:35:00', NULL);

INSERT INTO trade_transaction (id, order_id, user_id, type, amount, balance_before, balance_after, remark) VALUES
(1, 1, 4, 'PAY', 4800.00, 5000.00, 200.00, '购买屠龙刀'),
(2, 1, 2, 'INCOME', 4800.00, 50000.00, 54800.00, '出售屠龙刀'),
(3, 2, 5, 'PAY', 1900.00, 3000.00, 1100.00, '购买复活戒指'),
(4, 2, 3, 'INCOME', 1900.00, 30000.00, 31900.00, '出售复活戒指'),
(5, 3, 6, 'PAY', 450.00, 2000.00, 1550.00, '购买黑铁矿石x10'),
(6, 3, 2, 'INCOME', 450.00, 54800.00, 55250.00, '出售黑铁矿石x10'),
(7, 4, 4, 'PAY', 1750.00, 200.00, -1550.00, '购买麻痹戒指'),
(8, 4, 3, 'INCOME', 1750.00, 31900.00, 33650.00, '出售麻痹戒指'),
(9, 5, 5, 'PAY', 475.00, 1100.00, 625.00, '购买祝福油x5');
