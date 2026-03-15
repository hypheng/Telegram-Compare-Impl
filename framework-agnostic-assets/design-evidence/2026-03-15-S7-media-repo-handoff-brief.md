# 2026-03-15 S7 Repo Handoff Brief

- Slice: `S7 媒体消息`
- Scope: `UI-S4 Chat Detail`, `UI-S8 Media Picker Sheet`
- Status: `ready-for-dev via repo-side brief`
- Figma: `blocked`

## Frame Inventory

1. `S7 / Detail / Thread With Media`
   - 顶部聊天栏
   - 消息流中混合文本消息和图片消息
   - 固定 composer，左侧有 `Media` 入口
2. `S7 / Detail / Media Picker Open`
   - 底部 `Media Picker` sheet
   - 至少 3 个 fixture 图片选项
   - `关闭` 入口
3. `S7 / Detail / Media Sent`
   - 顶部 info banner: `图片已发送。`
   - 新出站图片消息出现在消息流尾部
4. `S7 / Detail / Restored With Media`
   - 冷启动恢复后，最近发送的媒体消息仍可见

## Interaction Notes

- 点击 `Media`:
  - 打开底部 picker
  - 系统返回优先关闭 picker
- 点击某个 fixture 图片:
  - 立即发送
  - picker 关闭
  - 详情页保留当前滚动语义
- 媒体消息发送成功后:
  - thread 追加新消息
  - chat list preview 回写最近媒体文案

## Visual Notes

- 图片消息优先使用 Telegram 风格的大圆角缩略卡 + 轻量 caption。
- picker 作为底部覆盖层出现，不把详情页变成整页滚动表单。
- fixture 图片允许使用彩色占位和标签，但整体仍需克制。

## Current Blocker

- 真实 Figma file / frame / node 仍未回填，因此当前 handoff 继续由 repo-side brief 代理。
