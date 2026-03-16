# Screen Inventory

## 核心屏幕

| ID | Screen | Purpose | Primary States |
|---|---|---|---|
| UI-S1 | Launch / Session Restore | 恢复会话或进入登录 | loading, restored, failed |
| UI-S2 | Login | 手机号 / 验证码 / 登录反馈 | idle, submitting, error |
| UI-S3 | Chat List | 浏览会话与未读状态 | loading, empty, ready, error |
| UI-S4 | Chat Detail | 查看消息、发送文本、失败重试 | loading, ready, sending, failed |
| UI-S5 | Search | 搜索会话和消息 | idle, searching, empty, result |
| UI-S6 | Settings / Profile | 管理资料与偏好 | loading, ready, failed |
| UI-S7 | AI Overlay | 展示 AI summary / draft / todo | closed, loading, ready, failed |
| UI-S8 | Media Picker Sheet | 选择并发送 fixture 图片 | closed, loading, ready, failed |
| UI-S9 | Contacts | 浏览、搜索联系人并发起聊天 | loading, ready, empty, failed |

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
- 布局边界:
  - 页面撑满视口
  - 顶部、搜索和底部导航固定
  - 只有列表区域滚动
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
- 布局边界:
  - 页面撑满视口
  - 顶部和 composer 固定
  - 只有消息区滚动
- 输入区:
  - 使用 Telegram 风格圆角 composer
  - 附件、发送、语音等次级入口要保持低噪音

### UI-S5 Search

- 搜索页和搜索态要延续 Telegram 的搜索框、列表密度和结果层级
- 搜索结果优先复用会话列表和消息行的视觉语言，不单独发明卡片样式

### UI-S6 Settings / Profile

- 设置页延续 Telegram 的轻量分组设置风格，而不是调试控制台
- profile hero、偏好行和危险操作分组要清晰，但保持克制的白底 + 细边框语义
- 页面撑满视口，顶部和底部导航固定，只有内容区滚动

### UI-S9 Contacts

- 视觉目标:
  - 接近 Telegram 当前联系人页的轻量顶部、平面列表和高密度 row 结构
  - 联系人列表应该看起来是 Telegram 主壳的一部分，而不是第二套通讯录应用
- 顶部结构:
  - 中心标题 `Contacts`
  - 搜索框固定在标题区下方
  - 允许轻量 `Add` 占位，但不能盖过主列表
- 列表结构:
  - 白底平面列表
  - 圆形头像 + 姓名 + 状态/手机号
  - 已有聊天和“将创建新聊天”的联系人不应使用重卡片分割
- 布局边界:
  - 页面撑满视口
  - 顶部和底部导航固定
  - 只有联系人列表 viewport 滚动

### UI-S8 Media Picker Sheet

- media picker 作为底部覆盖层出现，不应把聊天详情改造成整页表单
- fixture 图片优先使用统一圆角缩略卡和短标签，保持与 Telegram 风格同向

## 每个屏幕至少要定义

- 主任务
- 主信息层级
- 初始态 / 空态 / 错误态 / 加载态
- 关键手势和反馈
- 无障碍关注点

## 当前 ready-for-dev 切片

### S1 登录与会话恢复

- 设计 handoff: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- 涉及屏幕: `UI-S1`、`UI-S2`，以及作为成功落点的 `UI-S3 Chat List Entry Shell`
- 范围说明: `UI-S3` 在 `S1` 中只承担主壳到达验证，不计入 `S2` 完整验收

### S2 会话列表

- 设计 handoff: `framework-agnostic-spec/interface-design/s2-chat-list.md`
- repo-side handoff: `framework-agnostic-assets/design-evidence/2026-03-15-S2-chat-list-repo-handoff-brief.md`
- 涉及屏幕: `UI-S3`、`UI-S5`，以及作为承接提示的 `UI-S4 Chat Detail Entry Placeholder`
- 范围说明: `UI-S4` 在本切片只承担“点进下一步”的占位说明，不计入 `S3` 验收

### S3 单聊详情与文本发送

- 设计 handoff: `framework-agnostic-spec/interface-design/s3-chat-detail-and-send.md`
- repo-side handoff: `framework-agnostic-assets/design-evidence/2026-03-15-S3-chat-detail-repo-handoff-brief.md`
- 涉及屏幕: `UI-S4`
- 范围说明: 覆盖真实详情、发送中、失败和重试；媒体、附件和群聊仍不纳入本切片

### S4 本地缓存与离线恢复

- 设计 handoff: `framework-agnostic-spec/interface-design/s4-local-cache-and-offline-restore.md`
- repo-side handoff: `framework-agnostic-assets/design-evidence/2026-03-15-S4-cache-offline-repo-handoff-brief.md`
- 涉及屏幕: `UI-S1`、`UI-S3`、`UI-S4`
- 范围说明: 不新增独立主屏，而是在启动、列表和详情中增加 snapshot 恢复与缓存来源提示

### S5 搜索会话 / 消息

- 设计 handoff: `framework-agnostic-spec/interface-design/s5-search-chats-and-messages.md`
- 涉及屏幕: `UI-S3`、`UI-S5`、`UI-S4`
- 范围说明: 保留 `S2` 的列表内过滤，并新增独立全局搜索结果页与消息命中跳转

### S6 设置与个人资料

- 设计 handoff: `framework-agnostic-spec/interface-design/s6-settings-and-profile.md`
- 涉及屏幕: `UI-S3`、`UI-S5`、`UI-S6`
- 范围说明: 把底部 `Settings` tab 升级成真实设置页，覆盖资料摘要、偏好项切换和设置页 logout

### S7 媒体消息

- 设计 handoff: `framework-agnostic-spec/interface-design/s7-media-messages.md`
- 涉及屏幕: `UI-S4`、`UI-S8`
- 范围说明: 在聊天详情中增加图片消息与 media picker，不扩展到真实相册权限和复杂媒体浏览

### S8 Contacts 联系人

- 设计 handoff: `framework-agnostic-spec/interface-design/s8-contacts.md`
- 涉及屏幕: `UI-S9`、`UI-S4`
- 范围说明: 覆盖联系人列表、搜索、打开已有聊天和创建新聊天；不扩展到真实系统通讯录权限和邀请非 Telegram 用户
