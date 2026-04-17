# 职途安各模块详细设计（Detailed Design）

## 1. 系统概览
- 前端：多页面静态站点（HTML + CSS + Vanilla JS），部署在 Render Static Site。
- 后端：Spring Boot 3.3.5 + Java 17，部署在 Render Web Service。
- 数据层：TiDB Cloud（MySQL 协议）。
- AI能力：后端中转调用 Coze API，前端不直连第三方密钥。

## 2. 鉴权与会话模块
### 2.1 目标
- 提供登录/注册能力。
- 使用 JWT 鉴权，保护用户资料、论坛互动、AI历史等接口。

### 2.2 核心流程
1. 前端登录页提交账号密码到 `/api/auth/login`。
2. 后端验证后返回 `token + userId + username`。
3. 前端写入 localStorage/sessionStorage，并在导航栏渲染登录态。
4. 受保护接口通过 `Authorization: Bearer <token>` 访问。

### 2.3 关键设计点
- 前端登录/注册请求带超时与重试，降低 Render 冷启动影响。
- 多页面统一读取 token/userId/username，避免游客态闪烁。

## 3. 用户资料与头像模块
### 3.1 目标
- 支持个人资料编辑、头像上传展示、跨页面同步。

### 3.2 核心接口
- `GET /api/users/{id}/profile`
- `PUT /api/users/{id}/profile`
- `PUT /api/users/{id}/avatar`

### 3.3 前端实现
- 个人中心先渲染本地缓存头像/用户名，再异步拉后端最新头像。
- 头像缓存按 `userId + username` 双键存储，减少切页丢失。
- 图片加载失败统一回退为首字母占位头像。

## 4. 风险评估模块
### 4.1 目标
- 计算并展示用户风险相关行为指标和反诈值。

### 4.2 核心接口
- `GET /api/users/{id}/risk-assessment`
- `PUT /api/users/{id}/activity`

### 4.3 指标维度
- 在线时长（onlineSeconds）
- AI使用次数（aiQueryCount）
- 案例学习次数（caseStudyCount）
- 模拟次数（simulationCount）
- 论坛访问次数（forumViewCount）

### 4.4 前端渲染策略
- 反诈值默认展示 `60`，避免首屏显示“未知”。
- 风险页自动刷新：
  - 切换到雷达图标签时刷新
  - 页面从后台回到前台时刷新
  - 定时轮询刷新（25s）

## 5. AI对话与文件问答模块
### 5.1 目标
- 支持文本问答、文件问答、流式输出、历史会话管理。

### 5.2 核心接口
- `POST /api/ai/coze/query-stream`
- `POST /api/ai/coze/query-with-file-stream`
- `GET /api/ai/coze/history?sessionId=...&limit=30`

### 5.3 历史与会话策略
- 前端会话列表最多保留最近 `30` 个会话（`SESSION_LIST_LIMIT = 30`）。
- 每个会话消息最多保留最近 `30` 条（`CHAT_HISTORY_LIMIT = 30`）。
- 后端历史接口默认/推荐读取最近 `30` 条（可传 `limit`，最大 200）。
- 为提速体验：前端先读取本地会话缓存渲染，再异步请求后端刷新。

### 5.4 流式响应设计
1. 前端建立 SSE/XHR 流连接。
2. 后端分片返回 chunk。
3. 前端逐段拼接并实时显示。
4. `done` 事件返回最终 answer 与 sessionId。
5. 前后端都进行会话落库/缓存更新。

## 6. 论坛模块
### 6.1 目标
- 支持发帖、评论、点赞、收藏、个人侧栏（我的帖子/点赞/收藏）。

### 6.2 核心接口
- `GET /api/forum/posts`
- `POST /api/forum/posts`
- `POST /api/forum/posts/{id}/like`
- `POST /api/forum/posts/{id}/favorite`
- `POST /api/forum/posts/{id}/comments`
- `GET /api/forum/posts/{id}/comments`

### 6.3 前端提速策略
- 首屏先尝试读取本地帖子缓存，立即渲染已有内容。
- 网络返回后再刷新最新列表并更新缓存。
- 侧栏的“我的点赞/收藏”延后异步加载，避免阻塞主列表展示。

## 7. 案例与模拟模块
### 7.1 目标
- 通过案例学习和模拟流程提升用户识别风险能力。

### 7.2 行为上报
- 进入相关页面或完成关键操作后上报 activity，参与风险评估计算。

## 8. 部署与运维模块
### 8.1 部署结构
- 后端：`backend/Dockerfile` 构建 Spring Boot JAR。
- 前端：`frontend/` 目录静态发布到 Render。

### 8.2 架构图文档策略
- 内部设计图已迁移到 `docs/internal/`，不随前端静态目录发布。
- 当前内部图文件：
  - `docs/internal/architecture-flow.html`
  - `docs/internal/communication-diagram.html`

## 9. 性能与可用性补充
- 冷启动容错：登录/注册加唤醒和重试。
- 数据读取容错：关键请求增加超时，避免长时间卡死。
- 体验兜底：头像、用户名、风险值均有本地回退显示。

## 10. 后续优化建议
1. 论坛后端支持分页查询，减少首包数据量。
2. AI历史接口支持“会话摘要列表”分页，提升会话管理可扩展性。
3. 风险评估改为后端异步聚合 + 前端增量拉取，降低重复计算压力。
4. 增加端到端自动化测试，覆盖登录态、头像、AI历史和论坛关键链路。
