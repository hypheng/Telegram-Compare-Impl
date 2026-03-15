# 2026-03-15 S5 Repo Handoff Brief

- Slice: `S5 搜索会话 / 消息`
- Scope: `UI-S3 Chat List`, `UI-S5 Search`, `UI-S4 Chat Detail`
- Status: `ready-for-dev via repo-side brief`
- Figma: `blocked`

## Frame Inventory

1. `S5 / Chat List / Filtered + Global Search Entry`
   - 搜索框已有关键词
   - 列表仍执行 `S2` 的本地过滤
   - 顶部以下新增低干扰“全局搜索”入口
2. `S5 / Search / Results`
   - 顶部 `Search`
   - 固定搜索框
   - 状态 banner: `命中 x 个会话，x 条消息。`
   - `Chats` section
   - `Messages` section
3. `S5 / Search / Empty`
   - 顶部和搜索框保持固定
   - 空态文案与 `清除关键词`
4. `S5 / Search / Error`
   - 顶部和搜索框保持固定
   - 页面级 error card 和 `重试加载`
5. `S5 / Detail / Opened From Chat Result`
   - 顶部 info banner: `已从搜索结果打开 ...`
6. `S5 / Detail / Opened From Message Result`
   - 顶部 info banner: `已定位到包含 "<query>" 的消息。`
   - 命中消息预览卡
   - 若命中消息在当前视口，也应以高亮 bubble 呈现

## Interaction Notes

- `S2` 的列表内搜索不删除，`S5` 是额外的全局搜索路径。
- 点击 chat result:
  - 打开详情
  - 返回时回到原搜索结果页
- 点击 message result:
  - 打开详情
  - 首屏必须可见“命中消息定位”表达
  - 返回时回到原搜索结果页

## Visual Notes

- 搜索页继续沿用 Telegram 风格的轻量顶部、圆角搜索框和平面结果列表。
- `Chats` 与 `Messages` 两组都优先复用现有 list row 语言，不另起卡片系统。
- 搜索页仍需遵守固定顶部 + 固定搜索框 + 仅结果 viewport 滚动的边界。

## Current Blocker

- 真实 Figma file / frame / node 仍未回填，因此当前 handoff 继续由 repo-side brief 代理。
