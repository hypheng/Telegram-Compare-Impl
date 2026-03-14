# Screen Inventory

## 核心屏幕

| ID | Screen | Purpose | Primary States |
|---|---|---|---|
| UI-S1 | Launch / Session Restore | 恢复会话或进入登录 | loading, restored, failed |
| UI-S2 | Login | 手机号 / 验证码 / 登录反馈 | idle, submitting, error |
| UI-S3 | Chat List | 浏览会话与未读状态 | loading, empty, ready, error |
| UI-S4 | Chat Detail | 查看消息、发送文本、失败重试 | loading, ready, sending, failed |
| UI-S5 | Search | 搜索会话和消息 | idle, searching, empty, result |
| UI-S6 | Settings / Profile | 管理资料与偏好 | loading, ready |
| UI-S7 | AI Overlay | 展示 AI summary / draft / todo | closed, loading, ready, failed |

## 每个屏幕至少要定义

- 主任务
- 主信息层级
- 初始态 / 空态 / 错误态 / 加载态
- 关键手势和反馈
- 无障碍关注点

