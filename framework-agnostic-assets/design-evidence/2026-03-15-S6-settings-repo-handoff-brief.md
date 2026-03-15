# 2026-03-15 S6 Repo Handoff Brief

- Slice: `S6 设置与个人资料`
- Scope: `UI-S3 Chat List`, `UI-S5 Search`, `UI-S6 Settings / Profile`
- Status: `ready-for-dev via repo-side brief`
- Figma: `blocked`

## Frame Inventory

1. `S6 / Settings / Loading`
   - 顶部 `Settings`
   - 底部导航高亮 `Settings`
   - profile hero skeleton
   - preference rows skeleton
2. `S6 / Settings / Ready`
   - profile hero
   - `Account` section
   - `Preferences` section
   - `Session` section
3. `S6 / Settings / Preference Toggled`
   - 顶部 info banner
   - 某一行切换到新的 On / Off 状态
4. `S6 / Settings / Failed`
   - 页面级 error card
   - `重试加载`
5. `S6 / Settings / Logout`
   - 危险操作入口明确可见

## Interaction Notes

- `Settings` tab 可从 chat list 和 search 进入。
- 系统返回:
  - 若从 chat list 进入，返回 chat list
  - 若从 search 进入，返回 search 结果页
- preference toggle 不整页刷新，只更新当前行和顶部反馈。
- logout 后回到 `S1 Login`，旧聊天上下文不应被直接恢复。

## Visual Notes

- 设置页继续沿用 Telegram 风格的轻量分组设置页，而不是调试控制台。
- profile hero 使用白底、细边框、低噪声蓝色强调。
- `Preferences` 与 `Session` 分组要有清晰层级，但不做厚重卡片系统。

## Current Blocker

- 真实 Figma file / frame / node 仍未回填，因此当前 handoff 继续由 repo-side brief 代理。
