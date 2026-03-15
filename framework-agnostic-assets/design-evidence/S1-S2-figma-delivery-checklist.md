# S1-S2 Figma Delivery Checklist

## 目的

把当前最关键的两个切片拆成可执行的 Figma 交付清单。

- `S1` 是 backfill：代码已开始，Figma 需要补回视觉和 handoff 证据。
- `S2` 是前置：必须先完成 Figma handoff，才能进入实现。

## S1 登录与会话恢复

### 目标

为已经存在的 KMP `S1` 实现补上视觉对照、关键状态稿和 ready-for-dev 证据。

### 必需 frame

- `S1 / Launch / restoring`
- `S1 / Login / idle`
- `S1 / Login / restore failed`
- `S1 / Login / invalid input`
- `S1 / Login / submitting`
- `S1 / Entry shell / restored`
- `S1 / Entry shell / just logged in`

### 必需注释

- 恢复态文案和过渡规则
- 登录页错误 banner 的层级和位置
- demo 提示是否展示给开发/测试 build
- `Chat List Entry Shell` 与真正 `S2` 列表的边界
- 登出和“写入失效会话用于测试”是否为 debug-only

### 必需 prototype

- 冷启动无会话 -> 登录 -> 进入 entry shell
- 冷启动失效会话 -> restore failed -> 重新登录

### Ready-for-dev 条件

- 关键状态 frame 已齐
- 关键流程 prototype 已可演示
- frame/node links 已登记
- 与现有 KMP 实现的偏差已备注

## S2 会话列表

### 目标

在代码开始前，先把 Telegram 风格 chat list 的视觉层级、密度、状态和导航承接做完整。

### 必需 frame

- `S2 / Chat list / default`
- `S2 / Chat list / loading`
- `S2 / Chat list / empty`
- `S2 / Chat list / refresh`
- `S2 / Chat list / error`
- `S2 / Search / idle`
- `S2 / Search / focused`
- `S2 / Search / result`

### 必需注释

- 顶部标题、编辑入口、写消息入口的层级
- 搜索框位置、尺寸和滚动行为
- 列表行高、头像尺寸、分隔线和未读表达
- 底部导航承载区样式
- loading / refresh / error 的反馈形式
- 与 Telegram 参考图相比哪些点是必须贴近，哪些允许偏差

### 必需 prototype

- 默认进入 chat list
- 下拉刷新
- 点搜索进入结果
- 从 chat list 点进 chat detail 的承接动作

### Ready-for-dev 条件

- 默认态和关键异常态 frame 已齐
- prototype 能覆盖主路径和失败路径
- 关键 token 和 spacing 已标注
- 已明确哪些部分后续会复用到 `S3`

## 交付记录

完成后在 `framework-agnostic-assets/design-evidence/` 中补:

- Figma 文件链接
- frame/node links
- review 结论
- ready-for-dev 清单
- 设计与实现偏差说明
