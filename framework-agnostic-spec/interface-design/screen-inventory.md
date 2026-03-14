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

## 关键屏幕的 Telegram 风格约束

### UI-S3 Chat List

- 视觉目标:
  - 接近 Telegram 当前会话列表的层级、密度和留白
  - 第一眼就像一个熟悉的 Telegram 主列表，而不是通用 IM 模板
- 顶部结构:
  - 轻量顶部区域
  - 中心标题
  - 左侧编辑入口
  - 右侧新增 / 写消息入口
  - 标题区下方紧接全宽搜索框
- 列表结构:
  - 白底平面列表
  - 极细分隔线
  - 圆形头像 + 两行文本
  - 时间、静音、未读等元信息放在右上或弱层级位置
- 底部导航:
  - 允许使用 Telegram iOS 类似的大圆角承载区
  - 当前 tab 需要明确高亮，但不破坏整体克制感

### UI-S4 Chat Detail

- 视觉目标:
  - 接近 Telegram 当前聊天详情页的朴素、高密度和原生感
- 顶部结构:
  - 返回、会话信息、次级操作保持轻量
  - 不做品牌化头图或沉浸式大横幅
- 消息区:
  - 气泡大小、圆角、垂直节奏需接近 Telegram
  - 发送态、失败态、时间戳的表达优先简洁
- 输入区:
  - 使用 Telegram 风格圆角 composer
  - 附件、发送、语音等次级入口要保持低噪音

### UI-S5 Search

- 搜索页和搜索态要延续 Telegram 的搜索框、列表密度和结果层级
- 搜索结果优先复用会话列表和消息行的视觉语言，不单独发明卡片样式

## 每个屏幕至少要定义

- 主任务
- 主信息层级
- 初始态 / 空态 / 错误态 / 加载态
- 关键手势和反馈
- 无障碍关注点
