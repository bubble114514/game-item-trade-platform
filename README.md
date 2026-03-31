# 游戏道具交易平台（毕业设计）

基于 **领域驱动设计（DDD）** 的微服务拆分：**用户、道具、交易、搜索、支付、消息**；同步 **REST**，异步 **RocketMQ**；**Nacos** 注册发现、**Redis** 缓存、**Elasticsearch** 检索、**Sa-Token** 认证、**Sentinel** 容错、可配合 **Nginx** 与 **Docker** 部署。

## 与论文章节的对应关系

| 论文要点 | 工程中的体现 |
|---------|-------------|
| DDD 与微服务拆分 | 各 `*-service` 独立部署；包结构 `domain` / `application` / `interfaces`（或 `infrastructure`） |
| REST + RocketMQ | 对外 HTTP；`trade-service` 下单后发 `ORDER_TOPIC:CREATED`，`message-service` 订阅 |
| MySQL 分库与索引 | `sql/init.sql` 多库与索引示例；分表策略见注释 |
| Redis 缓存 | `item-service`：`@Cacheable` + `RedisCacheManager`（热点道具） |
| ES 多维度检索 | `search-service`：`ItemDocument` + `Criteria` 查询 |
| Sa-Token | `user-service` 登录签发；`gateway-service` 与 Redis 共用校验 |
| Sentinel | `gateway-service`、`item-service` 已引入依赖，可在控制台配置规则 |
| Nginx | `deploy/nginx-gateway.example.conf` 示例 |

## 环境要求

- JDK 17+
- Maven 3.9+
- Docker（可选，用于一键起中间件）

## 启动中间件

```bash
docker compose -f deploy/docker-compose-infra.yml up -d
```

RocketMQ Broker 依赖 NameServer 地址；本机开发时设置环境变量：

```text
ROCKETMQ_NAMESRV=127.0.0.1:9876
```

（若 Broker 在 Docker 内、应用在宿主机，需将 `rocketmq.name-server` 指向可访问的 `9876` 地址。）

## 启动顺序建议

1. MySQL、Redis、Nacos、RocketMQ、Elasticsearch  
2. **user-service** → **item-service** → **trade-service** → **search-service** → **payment-service** → **message-service**  
3. 最后启动 **gateway-service**（统一入口 `8080`）

各模块端口：`gateway 8080`，`user 8081`，`item 8082`，`trade 8083`，`search 8084`，`payment 8085`，`message 8086`。

## 编译

```bash
mvn -DskipTests package
```

## 前端（Vue 前后端分离）

前端工程在 `frontend/`，使用 Vite + Vue3，开发环境会把请求代理到后端网关 `http://localhost:8080`。

1. 安装依赖与启动前端

```bash
cd frontend
npm install
npm run dev
```

2. 访问地址

- `http://localhost:5173/`

登录后，`token/userId/nickname` 会存入 `localStorage`，后续调用 `/api/**` 时自动携带 `Authorization: Bearer <token>`。

## 接口示例

- 注册：`POST http://localhost:8080/api/user/auth/register`  
  Body：`{"username":"u1","password":"p1","nickname":"玩家1"}`
- 登录：`POST http://localhost:8080/api/user/auth/login`  
  Body：`{"username":"u1","password":"p1"}` → 返回 `token`
- 带鉴权请求：Header `Authorization: Bearer <token>`
- 道具详情：`GET http://localhost:8080/api/item/{id}`
- 创建订单：`POST http://localhost:8080/api/trade/orders`
- 搜索：`GET http://localhost:8080/api/search/items?q=关键词`
- 模拟支付：`POST http://localhost:8080/api/payment/orders/{orderId}/mock-pay`

## 检索索引说明

Elasticsearch 需存在索引 `game_items` 及文档数据后搜索才有结果；可通过 Dev Tools 写入测试文档，或后续增加「从道具库同步到 ES」的定时/消息任务（论文可写为展望）。

## 分布式事务说明

完整 **Seata** 或与 RocketMQ 事务消息结合可在 `trade-service` / `payment-service` 中扩展；当前工程以「下单发消息 + 消息服务异步处理」体现解耦与最终一致性思路。
