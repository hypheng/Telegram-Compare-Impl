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
2. 消息流
3. composer 输入区
4. debug-only 发送失败验证区

### 视觉规则

- 顶部应保持轻量，不做厚重 app bar。
- 消息区应使用 Telegram 风格的高密度气泡和轻背景，不使用卡片瀑布流。
- incoming 与 outgoing 气泡的色块、对齐和元信息层级要明显区分。
- composer 使用圆角输入区和低噪音发送入口。

### 交互

- 点击返回: 回到会话列表。
- 输入非空文本后点击发送: 立即展示 sending。
- failed 气泡内提供 retry。

## UI-S4 Chat Detail / Loading

- 顶部结构与 composer 骨架保持稳定。
- 消息区显示 skeleton bubble 或等价 loading block。
- 不出现全屏白板。

## UI-S4 Chat Detail / Sending

- 新发出的 outgoing message 立即出现在消息流末尾。
- bubble 以弱层级标记 `发送中`，但不阻塞用户浏览历史消息。

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
