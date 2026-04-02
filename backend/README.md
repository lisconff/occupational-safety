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

## 7. Coze 智能体接入

### 7.1 配置
推荐在环境变量或 `application-local.yml` 中配置：

- `COZE_TOKEN`: Coze Bearer Token
- `COZE_PROJECT_ID`: Coze 项目 ID
- `COZE_SESSION_ID`: 默认会话 ID（可选，接口里也可传）
- `COZE_STREAM_URL`: 流式地址（默认 `https://k7zcnck7q6.coze.site/stream_run`）

示例（PowerShell）：

```powershell
$env:COZE_TOKEN="你的token"
$env:COZE_PROJECT_ID="你的project_id"
$env:COZE_SESSION_ID="你的默认session_id"
```

### 7.2 后端接口

新增接口：`POST /api/ai/coze/query`

请求体：

```json
{
  "prompt": "请帮我分析这段合同文本",
  "sessionId": "可选，不传则用默认配置"
}
```

响应体（`data` 字段）：

```json
{
  "answer": "智能体返回文本",
  "eventCount": 12,
  "sessionId": "Mfh0..."
}
```

说明：
- 后端会调用 Coze `text/event-stream` 并自动聚合文本。
- 当前返回聚合后的最终文本，适合先快速接入业务页面。

### 7.3 文件上传审查接口

新增接口：`POST /api/ai/coze/query-with-file`

请求类型：`multipart/form-data`

字段：
- `file`: 选填，支持 `pdf/doc/docx/txt`，最大 10MB
- `prompt`: 可选，用户补充提问
- `sessionId`: 可选，Coze 会话 ID

约束：`prompt` 和 `file` 不能同时为空。

响应体（`data` 字段）：

```json
{
  "answer": "智能体返回文本",
  "eventCount": 18,
  "sessionId": "Mfh0...",
  "fileName": "劳动合同.pdf",
  "fileType": "pdf",
  "extractedTextLength": 6321
}
```

前端调用示例（fetch）：

```javascript
const formData = new FormData();
formData.append('file', file);
formData.append('prompt', '请重点检查试用期和违约金条款');
// formData.append('sessionId', '可选');

const res = await fetch('/api/ai/coze/query-with-file', {
  method: 'POST',
  headers: {
    Authorization: `Bearer ${token}`
  },
  body: formData
});
const json = await res.json();
```
