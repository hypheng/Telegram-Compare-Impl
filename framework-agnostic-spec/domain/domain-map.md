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
- `SettingsOpened`
- `PreferenceChanged`
- `MediaPickerOpened`
- `MediaMessageSent`
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

## S4 补充对象

- `SyncSnapshotRoute`: `CHAT_LIST`、`CHAT_DETAIL`
- `SyncSnapshot`: 最近一次列表或详情快照
- `SyncSnapshotRestoreResult`: `Restored`、`NoSnapshot`、`Failed`
- `SyncSnapshotSaveResult`: `Success`、`Failed`
- `SyncRepository`: snapshot 读写与清空边界

## S5 补充对象

- `SearchQuery`: 全局搜索关键词
- `MessageSearchHit`: 消息级搜索命中
- `SearchLoadResult`: `Success`、`Empty`、`Failed`
- `SearchRepository`: 全局搜索边界

## S6 补充对象

- `UserProfileSummary`: 设置页资料摘要
- `PreferenceKey`: 偏好项标识
- `UserPreference`: 单个偏好定义
- `SettingsSnapshot`: profile 与 preferences 的组合
- `SettingsLoadResult`: `Success`、`Failed`
- `UpdatePreferenceResult`: `Success`、`Failed`
- `SettingsRepository`: 设置读取、偏好写入边界

## S7 补充对象

- `MediaAttachment`: 图片 fixture 描述
- `MediaPickerLoadResult`: `Success`、`Failed`
- `SendMediaResult`: `Success`、`Failed`
- `Message`: 扩展可选媒体负载
- `ChatDetailRepository`: 扩展媒体选择与发送边界

### 会话列表

`empty -> loading -> ready | failed`

### 发送消息

`draft -> sending -> sent | failed -> retrying`

## 设计约束

- 领域模型不能先绑定某个框架的状态容器或 UI 生命周期
- 网络、存储、导航只在适配层绑定平台
- AI 助手能力必须作为独立 overlay，而不是侵入所有业务对象
