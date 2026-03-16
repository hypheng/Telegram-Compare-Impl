# 2026-03-16 S8 Contacts Repo Handoff Brief

- Slice: `S8 Contacts 联系人`
- Scope: `UI-S9 Contacts`, `UI-S4 Chat Detail`, `UI-S3 Chat List`
- Status: `ready-for-dev via repo-side brief`
- Figma: `blocked`

## Frame Inventory

1. `S8 / Contacts / Root`
   - 顶部 `Contacts`
   - 右上轻量 `添加` 占位
   - 固定搜索框
   - 联系人列表
   - 轻量 demo scenarios footer chips
2. `S8 / Contacts / Search Result`
   - 搜索关键词保留在搜索框
   - 过滤后只展示命中联系人
3. `S8 / Contacts / Search Empty`
   - 联系人专属空态
   - CTA: `清除搜索`
4. `S8 / Contacts / Error`
   - 页面级错误说明
   - CTA: `重试加载`
5. `S8 / Contacts / Existing Chat`
   - 从已有聊天联系人进入详情
   - 顶部 status pill: `已从联系人进入 <name>`
6. `S8 / Contacts / New Chat`
   - 从无现有会话联系人创建新聊天
   - 顶部 status pill: `已开始与 <name> 的新对话`
7. `S8 / Contacts / Return Refreshed`
   - 从新聊天返回联系人页
   - 原搜索词保留
   - 同一联系人弱提示从 `新对话` 刷新为 `已有聊天`
8. `S8 / Chats / New Contact Visible`
   - 返回 chats 后，新建联系人会话出现在列表顶部附近

## Interaction Notes

- 底部 `Contacts` 与顶部 `新聊天` 都进入同一联系人主路径。
- 点击已有聊天联系人:
  - 直接进入对应详情
  - 系统返回优先回到联系人页
- 点击新联系人:
  - 创建新单聊 fixture
  - 进入详情
  - 返回联系人时必须刷新结果，不能回显旧列表快照
- 冷启动恢复后:
  - 若 chats 中已有从联系人创建的新会话，contacts 也应显示 `已有聊天`

## Visual Notes

- 联系人 row 使用白底平面列表语言，不做卡片化。
- `已有聊天` / `新对话` 用轻量 trailing text，不使用重 badge。
- debug-only 场景入口只保留一行轻量 chips，不做整块 debug card。
- 搜索空态和错误态都保持顶部、搜索框和底部导航稳定。

## Current Blocker

- 真实 Figma file / frame / node 仍未回填，因此当前 handoff 继续由 repo-side brief 代理。
