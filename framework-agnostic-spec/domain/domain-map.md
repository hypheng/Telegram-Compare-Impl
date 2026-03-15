# Domain Map

## 核心领域

| 领域 | 责任 | 关键对象 |
|---|---|---|
| Session | 登录态、token、恢复 | `UserSession`, `AuthState` |
| Chats | 会话列表、排序、未读 | `ChatSummary`, `UnreadCounter` |
| Messages | 消息流、发送状态、重试 | `Message`, `MessageDraft`, `DeliveryState` |
| Sync | 拉取、增量更新、缓存恢复 | `SyncCursor`, `SyncSnapshot` |
| Search | 会话 / 消息检索 | `SearchQuery`, `SearchResult` |
| Settings | 偏好项、主题、实验开关 | `UserPreference`, `FeatureFlag` |
| AI Overlay | AI 助手入口、上下文、结果展示 | `AiIntent`, `AiOutput`, `AiTrace` |

## 关键事件

- `AppStarted`
- `SessionRestored`
- `LoginSucceeded`
- `ChatListLoaded`
- `ChatOpened`
- `MessageSendRequested`
- `MessageSendSucceeded`
- `MessageSendFailed`
- `SyncCompleted`
- `SearchSubmitted`
- `AiActionTriggered`

## 关键状态机

### 登录

`idle -> loading -> authenticated | failed`

### S1 启动恢复

`booting -> restoring -> authenticated | login-required | restore-failed`

## S1 补充对象

- `UserSession`: 当前账号、展示名称、手机号和会话标识
- `SessionRestoreResult`: `Restored`、`NoSession`、`Failed`
- `LoginResult`: `Success`、`InvalidInput`、`Failed`
- `SessionRepository`: 恢复、登录、登出与持久化边界

## S2 补充对象

- `ChatListQuery`: 会话列表搜索关键词
- `ChatListLoadResult`: `Success`、`Empty`、`Failed`
- `ChatListRepository`: 列表读取、刷新与搜索边界

## S3 补充对象

- `ChatThread`: 会话摘要与消息流组合对象
- `ChatDetailLoadResult`: `Success`、`Failed`
- `SendMessageResult`: `Success`、`InvalidInput`、`Failed`
- `RetryMessageResult`: `Success`、`Failed`
- `ChatDetailRepository`: 详情读取、发送与失败重试边界

### 会话列表

`empty -> loading -> ready | failed`

### 发送消息

`draft -> sending -> sent | failed -> retrying`

## 设计约束

- 领域模型不能先绑定某个框架的状态容器或 UI 生命周期
- 网络、存储、导航只在适配层绑定平台
- AI 助手能力必须作为独立 overlay，而不是侵入所有业务对象
