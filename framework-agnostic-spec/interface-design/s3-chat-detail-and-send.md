# S3 UI 设计: 单聊详情与文本发送

## 涉及屏幕

- `UI-S4 Chat Detail`
- `UI-S3 Chat List` 的进入与返回承接

`S3` 的成功状态是真实聊天详情，不再使用 `S2` 的 placeholder banner 代替会话进入。

## UI-S4 Chat Detail / Default

### 信息层级

1. 顶部轻量导航区
   - 返回
   - 会话标题
   - 次级说明或状态
2. 固定状态反馈区
3. 可滚动消息流 viewport
4. 固定 composer 输入区
5. 低干扰 debug-only 发送失败验证区

### 布局约束

- 页面主壳必须撑满视口高度。
- 顶部导航、状态反馈和 composer 保持固定。
- 只有消息流 viewport 可滚动。
- debug-only 控件要保持紧凑，不能把 composer 顶成中部卡片布局。

### 视觉规则

- 顶部应保持轻量，不做厚重 app bar。
- 消息区应使用 Telegram 风格的高密度气泡和轻背景，不使用卡片瀑布流。
- incoming 与 outgoing 气泡的色块、对齐和元信息层级要明显区分。
- composer 使用圆角输入区和低噪音发送入口。
- 默认 fixture 至少要让消息区具备真实滚动长度，而不是只展示 2-3 条短消息。

### 交互

- 点击返回: 回到会话列表。
- 输入非空文本后点击发送: 立即展示 sending。
- failed 气泡内提供 retry。
- 滚动只发生在消息区，不允许顶部和 composer 一起整体滚动。

## UI-S4 Chat Detail / Loading

- 顶部结构与 composer 骨架保持稳定。
- 消息区显示 skeleton bubble 或等价 loading block。
- 不出现全屏白板。
- 如果消息数量不足一屏，消息 viewport 仍应被撑满。

## UI-S4 Chat Detail / Sending

- 新发出的 outgoing message 立即出现在消息流末尾。
- bubble 以弱层级标记 `发送中`，但不阻塞用户浏览历史消息。
- 发送中不应把 composer 顶走，也不应把整个页面滚成长表单。

## UI-S4 Chat Detail / Failed

- failed bubble 需显式标记失败，不得仅依赖颜色。
- failed bubble 保留原文本，并提供 retry 按钮。
- composer 不应被失败状态锁死。

## UI-S4 Chat Detail / Retrying

- retry 后原 failed bubble 进入 retrying，而不是新增一条重复消息。
- retry 成功后转为 sent。

## UI-S4 Chat Detail / Error

- 顶部结构保留。
- 消息区显示错误说明和重试入口。
- 错误态仍允许返回会话列表。

## 返回列表承接

- 返回列表后，列表应刷新到最新预览。
- 刚发送过消息的会话应提升到靠前位置，符合 Telegram 风格预期。

## 状态映射

| State | 页面表达 |
|---|---|
| `loading` | 顶部稳定，消息 skeleton |
| `ready` | 完整消息流 + composer |
| `sending` | 末尾 outgoing bubble 标记 `发送中` |
| `failed` | 末尾 failed bubble + retry |
| `retrying` | 原 failed bubble 标记 `重试中` |
| `error` | 页面级错误说明 + retry |

## 无障碍与系统约束

- 返回、发送、retry 和 composer 触控目标不小于平台建议最小尺寸。
- failed / sending / retrying 不能只靠颜色表达。
- 消息区和 composer 的切换要兼容 reduced motion。
- 长消息文本要保持 Telegram 风格的窄而高密度气泡，不要拉成整宽横条。
