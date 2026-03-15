# S2 Repo Handoff Brief

- Slice: `S2 会话列表`
- Date: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s2-chat-list.md`
- UI Design: `framework-agnostic-spec/interface-design/s2-chat-list.md`
- Telegram Style Reference: `framework-agnostic-assets/design-evidence/2026-03-15-telegram-style-reference.md`
- Acceptance Report: `framework-agnostic-assets/evaluation/acceptance-reports/S2-chat-list.md`

## Purpose

当前还没有真实的 Figma 文件和 frame/node links，因此这份 brief 作为 repo-side handoff 代理，用于让 `S2` 的 KMP 实现先围绕同一份视觉和交互约束推进。

## Required States

1. `S2 / Chat list / loading`
2. `S2 / Chat list / default`
3. `S2 / Chat list / refreshing`
4. `S2 / Chat list / empty`
5. `S2 / Chat list / error`
6. `S2 / Search / focused`
7. `S2 / Search / result`
8. `S2 / Chat entry / placeholder`

## Visual Anchors

- 顶部标题居中，左右动作轻量。
- 搜索框紧贴标题区下方，宽度接近全屏，圆角明显。
- 列表为平面结构，避免卡片式堆叠。
- 行内信息层级:
  - 标题最强
  - 时间和未读放右侧
  - 最近消息弱于标题
  - 头像保持规则圆形
- 底部导航使用 Telegram 风格圆角承载区，而不是默认 Android 底栏。

## Interaction Notes

- 下拉刷新必须保留列表内容。
- 搜索结果和默认列表使用同一 row 结构。
- 点击 row 只进入 `S3` placeholder 提示，不构成真实详情页。
- 空态和错误态都应保留顶部导航和搜索框。

## Demo-Only Controls

- 允许保留 debug-only scenario 切换，用于验证 `empty` 和 `error`。
- 允许保留轻量提示说明当前数据由 fixture 驱动。

## Handoff Gap

- Figma file URL: `TODO`
- Ready-for-dev page URL: `TODO`
- Frame / node links:
  - `TODO`
  - `TODO`
  - `TODO`

## Implementation Rule

在真实 Figma 文件补齐之前，KMP 实现必须优先遵守本 brief 和 `s2-chat-list.md`，并把任何偏差回写到 acceptance report。
