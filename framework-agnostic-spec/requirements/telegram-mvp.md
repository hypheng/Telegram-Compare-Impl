# Telegram MVP 范围

## 目标

构建一个足以比较 `KMP` 和 `CJMP` 的 Telegram 类应用 MVP，不追求一次覆盖 Telegram 全量能力，而是优先覆盖最能暴露框架差异的主路径。

## 用户角色

- 普通聊天用户
- 重度消息用户
- 需要跨设备恢复会话的用户
- 未来 AI 助手能力的使用者

## MVP 功能切片

| ID | 切片 | 优先级 | 验收标准 |
|---|---|---|---|
| S1 | 登录与会话恢复 | P0 | 可登录、可恢复上次会话、失败有明确反馈 |
| S2 | 会话列表 | P0 | 可展示会话、未读、最近消息、下拉刷新 |
| S3 | 单聊详情与文本发送 | P0 | 可拉取消息、发送文本、展示发送状态 |
| S4 | 本地缓存与离线恢复 | P0 | 重启后可恢复最近会话与消息片段 |
| S5 | 搜索会话 / 消息 | P1 | 支持基础搜索，结果可跳转 |
| S6 | 设置与个人资料 | P1 | 支持查看基础资料和偏好项 |
| S7 | 媒体消息 | P2 | 支持图片消息的展示与发送 |
| S8 | AI 助手增强能力 | P2 | 至少有一个 AI 场景用于展示“AI 生成商用品”的潜力 |

## 已细化切片

- `S1 登录与会话恢复`: 详见 `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- `S2 会话列表`: 详见 `framework-agnostic-spec/requirements/s2-chat-list.md`
- `S3 单聊详情与文本发送`: 详见 `framework-agnostic-spec/requirements/s3-chat-detail-and-send.md`
- `S4 本地缓存与离线恢复`: 详见 `framework-agnostic-spec/requirements/s4-local-cache-and-offline-restore.md`
- `S5 搜索会话 / 消息`: 详见 `framework-agnostic-spec/requirements/s5-search-chats-and-messages.md`
- `S6 设置与个人资料`: 详见 `framework-agnostic-spec/requirements/s6-settings-and-profile.md`
- `S7 媒体消息`: 详见 `framework-agnostic-spec/requirements/s7-media-messages.md`

## 非功能要求

| 类型 | 要求 |
|---|---|
| 体验 | 首屏主路径流畅，交互反馈明确 |
| 视觉一致性 | UI 风格应尽量贴近现有 Telegram 客户端，尤其是会话列表、聊天详情、搜索和底部导航的视觉层级与密度 |
| 可维护性 | 结构清晰，能支撑双框架长期对比 |
| 可测试性 | 核心状态、网络和存储可以隔离验证 |
| 可追踪性 | 每个切片都有对比证据和 AI 交付记录 |

## 明确不在首轮范围内

- 复杂群组管理
- 音视频通话
- 端到端加密完整实现
- 复杂贴纸 / 表情生态
- 完整推送链路
