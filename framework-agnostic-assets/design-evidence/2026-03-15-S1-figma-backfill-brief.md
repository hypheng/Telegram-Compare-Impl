# S1 Figma Backfill Brief

- Slice: `S1 登录与会话恢复`
- Date: `2026-03-15`
- Status: `backfill-in-progress`
- Source UI Spec: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Acceptance Report: `framework-agnostic-assets/evaluation/acceptance-reports/S1-login-and-session-restore.md`

## Purpose

`S1` 已经先进入 KMP 实现，因此这份文档用于把现有代码行为反向整理成可落到 Figma 的 handoff brief，避免后续设计证据继续缺位。

这不是替代 Figma 文件本身，而是 Figma 落图前的执行说明。

## Current Implementation Snapshot

KMP 侧当前已经存在以下可运行路径：

- 冷启动 restoring
- 无会话进入登录
- restore failed 后回退登录
- 登录成功进入 `Chat List Entry Shell`
- 已登录会话可恢复
- 登出回到登录
- debug-only 的失效会话写入入口

## Proposed Figma Page Structure

建议把 `S1` 放进 Figma 文件的 `10 S1 Login / Restore` 页面下，并使用以下 frame 命名：

1. `S1 / Launch / restoring`
2. `S1 / Login / idle`
3. `S1 / Login / restore failed`
4. `S1 / Login / invalid input`
5. `S1 / Login / submitting`
6. `S1 / Entry shell / restored`
7. `S1 / Entry shell / just logged in`
8. `S1 / Entry shell / debug seeded`

## Frame-by-frame Notes

### `S1 / Launch / restoring`

- 居中标题 `Telegram Compare`
- loading spinner
- 主文案 `正在恢复上次会话...`
- 次文案 `如果没有保存的会话，将自动进入登录。`
- 视觉应保持轻量，不能做品牌化 splash

### `S1 / Login / idle`

- 标题 `登录 Telegram Compare`
- 手机号输入框
- 验证码输入框
- 主按钮 `继续`
- 次按钮 `重试恢复`
- demo 提示 `Demo 环境固定验证码: 2046`

### `S1 / Login / restore failed`

- 在 idle 基础上增加错误 banner
- 错误 banner 位于标题说明下方、输入框上方
- 文案需要承接启动恢复失败，而不是重新编写一个不相关错误

### `S1 / Login / invalid input`

- 展示输入校验失败的 banner
- 用户输入内容保留
- 主按钮恢复可点击

### `S1 / Login / submitting`

- 主按钮切换为 loading / disabled
- 表单不可重复提交
- 视觉上不跳页

### `S1 / Entry shell / restored`

- 顶部标题 `Chats`
- 会话身份卡片
- 状态说明 `已恢复上次会话，主壳入口已就绪。`
- 两条 mock chat rows
- `退出登录`
- `写入失效会话用于测试`

### `S1 / Entry shell / just logged in`

- 与 restored frame 基本一致
- 状态说明改为 `登录成功，已进入 Chat List Entry Shell。`

### `S1 / Entry shell / debug seeded`

- 与 entry shell 一致
- 状态说明改为 `已写入失效会话。请重新启动应用验证恢复失败路径。`
- 该 frame 应标注为 debug-only，不计入正式产品界面

## Prototype Flows

### P1 无会话登录主路径

`Launch / restoring -> Login / idle -> Login / submitting -> Entry shell / just logged in`

### P2 失效会话回退路径

`Launch / restoring -> Login / restore failed -> Login / submitting -> Entry shell / just logged in`

### P3 已登录会话恢复路径

`Launch / restoring -> Entry shell / restored`

## Handoff Notes

### Must Match Implementation

- 登录页必须保留 `重试恢复`
- entry shell 必须明确只是 `S1` 成功落点，不是完整 `S2`
- debug-only 入口必须被标记为开发/测试用途
- 错误文案优先复用当前实现语义

### Allowed To Change During Design

- 顶部说明文字的更细微语气
- 身份卡片的视觉样式
- mock chat rows 的具体内容文案
- 按钮视觉细节和 spacing 微调

## Mapping To Current KMP States

| KMP State | Expected Figma Frame | Notes |
|---|---|---|
| `MainScreenState.Restoring` | `S1 / Launch / restoring` | 启动自动恢复态 |
| `MainScreenState.Login()` | `S1 / Login / idle` | 默认登录态 |
| `MainScreenState.Login(restoreMessage=...)` | `S1 / Login / restore failed` | 承接 restore failed |
| `MainScreenState.Login(formMessage=...)` | `S1 / Login / invalid input` | 输入校验或登录失败 |
| `MainScreenState.Login(isSubmitting=true)` | `S1 / Login / submitting` | 提交中 |
| `MainScreenState.Home(statusMessage=restored)` | `S1 / Entry shell / restored` | 恢复成功 |
| `MainScreenState.Home(statusMessage=just logged in)` | `S1 / Entry shell / just logged in` | 登录成功 |
| `MainScreenState.Home(statusMessage=debug seeded)` | `S1 / Entry shell / debug seeded` | debug-only |

## Figma Link Placeholders

- Figma file URL: `TODO`
- Ready-for-dev page URL: `TODO`
- Key frame / node links:
  - `TODO`
  - `TODO`
  - `TODO`

## Review Checklist

- [ ] 所有 `S1` 关键状态已落成 frame
- [ ] 至少 3 条 prototype 主路径已可点击演示
- [ ] `Chat List Entry Shell` 的边界已明确标注
- [ ] debug-only 内容已标注
- [ ] frame / node links 已回写到本文件
- [ ] 与现有 KMP 实现的偏差已记录

## Open Risks

- 当前 Figma 账号信息显示为 `View` seat；如果目标文件没有编辑权限，需要额外确认可编辑上下文。
- 当前还没有真实 Figma 文件链接，因此 handoff 仍未完成。
- 设备截图和视觉取证尚未补齐，Figma backfill 需要与真实运行效果互相校准。
