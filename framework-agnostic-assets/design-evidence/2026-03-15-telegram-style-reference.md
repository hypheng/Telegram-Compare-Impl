# Telegram Style Reference Review

- Date: 2026-03-15
- Source: 用户在 Codex 对话中提供的 Telegram 聊天列表截图
- Primary Scope: `UI-S3 Chat List`
- Secondary Scope: `UI-S4 Chat Detail`, `UI-S5 Search`, bottom navigation, top navigation, search field

## Observed Design Signals

- 使用纯净浅色背景和非常轻的分隔线
- 顶部是轻量导航而不是厚重 app bar
- 标题居中，左右是胶囊化操作入口
- 搜索框大圆角、低对比、接近全宽
- 会话列表是平面结构，不是卡片流
- 头像圆形、会话名粗体、预览文字弱化、时间靠右
- 底部导航具有明显 Telegram iOS 风格的大圆角承载区和当前项底板

## Design Decision

- 本项目 UI 默认以“贴近现有 Telegram 客户端风格”为目标，不另起一套品牌语言
- 会话列表、搜索框、顶部导航、底部导航、聊天详情都应优先复用上述信息层级和视觉节奏
- 实现时允许使用项目自己的图标、假数据、头像和品牌占位，不要求复制 Telegram 的官方资源文件

## Ready-For-Dev Notes

- `visual-system.md` 已补充 Telegram 风格锚点和关键组件约束
- `screen-inventory.md` 已补充 `UI-S3`、`UI-S4`、`UI-S5` 的 Telegram 风格要求
- 后续任何偏离 Telegram 主界面风格的设计，需要在这里补充偏离原因
