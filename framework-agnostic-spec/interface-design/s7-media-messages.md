# S7 UI 设计: 媒体消息

## 涉及屏幕

- `UI-S4 Chat Detail`
- `UI-S8 Media Picker Sheet`

`S7` 在 `S3` 的聊天详情页基础上增加媒体消息和底部 media picker，不新增新的顶层聊天屏幕。

## UI-S4 Chat Detail / Media Ready

### 信息层级

1. 顶部轻量导航区
2. 条件性 banner / 状态反馈区
3. 可滚动消息流 viewport
4. 固定 composer
5. 条件性出现的 `UI-S8 Media Picker Sheet`

### 布局约束

- 页面主壳必须撑满视口。
- 顶部与 composer 固定。
- 只有消息流 viewport 可滚动。
- media picker 以底部覆盖层出现，不把整页变成滚动表单。

## 图片消息气泡

### 信息层级

1. 图片缩略卡
2. caption
3. 时间 / 发送状态

### 视觉规则

- 图片消息必须明显区别于纯文本气泡，可使用大圆角图片容器和较弱 caption。
- 缩略卡优先使用 Telegram 风格的朴素圆角，不做重边框或高饱和装饰。
- caption 与时间的层级弱于图片本身。

## UI-S8 Media Picker Sheet

### 信息层级

1. sheet title
2. 说明文案
3. fixture 媒体网格或列表
4. `关闭` 入口

### 交互规则

- 点击 `Media` 入口后，sheet 从底部出现。
- 点击某个 fixture:
  - 立即发送该图片
  - sheet 关闭
  - 聊天详情显示发送成功反馈
- 点击 `关闭` 或空白区域:
  - 关闭 sheet
  - 不改变消息流

## UI-S4 / Media Sending

- 发送媒体时不使用阻塞式 modal。
- 可以沿用现有顶部 info banner:
  - `正在发送图片...`
  - `图片已发送。`
- 若未来需要失败态，可复用 `S3` 的发送失败语义。

## 状态映射

| State | 页面表达 |
|---|---|
| `thread ready with media` | 消息流中混合文本和图片消息 |
| `media picker closed` | 仅展示正常详情页 |
| `media picker open` | 底部覆盖层可见，列出 fixture 媒体 |
| `media sending success` | 新图片消息出现在 thread 尾部 |
| `restored detail with media` | 冷启动恢复后仍可见图片消息 |

## 无障碍与系统约束

- 图片消息和 picker item 需要有可读的文本标签，不能只靠彩色块表示。
- media picker 的打开、关闭要兼容系统返回手势。
- picker 内的项目与 composer 按钮触控目标不小于平台建议最小尺寸。
- 减少动效时，picker 允许直接淡入淡出，不依赖大位移动画。
