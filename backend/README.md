# 职途安后端基础框架

## 1. 技术栈
- Java 17
- Spring Boot 3
- Spring Web + Validation
- Spring Data JPA
- Spring Security（当前为开发放行模式）
- H2 内存数据库（开发期）

## 2. 分层结构
- controller: 接口层
- service: 业务层接口
- service/impl: 业务实现层
- repository: 数据访问层
- domain/model: 领域模型
- common: 公共响应和异常处理
- config: 安全和基础配置

## 3. 已搭建模块
- 认证与用户
  - 注册、登录
  - 用户资料查询、更新
- AI 助手
  - 合同审查占位实现
  - 黑话识别占位实现
  - 岗位风险评分占位实现
  - 分析历史查询
- 论坛
  - 发帖
  - 列表
  - 帖子详情
- 内容与案例
  - 首页案例推荐
  - 案例详情
- 模拟演练
  - 游戏入口查询
  - 场景列表

## 4. 关键接口（前缀 /api）
- GET /health
- POST /auth/register
- POST /auth/login
- GET /users/{userId}/profile
- PUT /users/{userId}/profile
- POST /ai/analyze/contract
- POST /ai/analyze/black-slang
- POST /ai/analyze/job-risk
- GET /ai/reports/{userId}
- POST /forum/posts
- GET /forum/posts
- GET /forum/posts/{postId}
- GET /content/cases/featured
- GET /content/cases/{caseId}
- GET /simulation/games/{gameId}
- GET /simulation/games/{gameId}/scenes

## 5. 本地启动
1. 安装 JDK 17
2. 进入 backend 目录
3. 执行: mvnw.cmd spring-boot:run
4. 默认端口为 10080，健康检查: http://localhost:10080/api/health
5. H2 控制台: http://localhost:10080/h2-console

## 5.1 协作建议（Gitee）
- 仓库已包含 Maven Wrapper（mvnw.cmd、mvnw、.mvn/wrapper/*）。
- 组员无需全局安装 Maven，拉取代码后可直接运行 mvnw.cmd。

## 6. 下一步建议
- 把 dev-token 替换为 JWT（access + refresh）
- 增加统一鉴权拦截，按角色控制论坛和后台管理接口
- 引入 Flyway 管理数据库版本
- 把 AI 规则库独立为 RiskRule 表并实现可配置规则引擎
- 为核心接口补充集成测试
