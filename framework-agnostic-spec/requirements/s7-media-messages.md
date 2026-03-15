# S7 媒体消息

## 切片目标

在 `S3` 的文本聊天基础上，增加一个可验收的图片消息流，使会话详情不再只承载纯文本，能够展示 fixture 媒体并发送新的 mock 图片消息。

## 用户价值

- 用户可以在消息流里看到更接近 Telegram 的图片消息表达，而不是只有文字气泡。
- 用户可以从聊天详情直接发出一条图片消息，验证聊天产品的非文本主路径。
- 发送后的媒体消息会反映到详情和列表预览中，而不是成为孤立的 UI 演示。

## 本切片范围

- 在 `S3 Chat Detail` 中增加图片消息渲染能力，覆盖入站和出站消息。
- 在 composer 附近增加轻量 `Media` 入口，打开 `UI-S8 Media Picker Sheet`。
- media picker 至少提供 3 个 fixture-backed 图片选项。
- 选择图片后，应用发送一条图片消息:
  - 插入消息流
  - 更新 chat list preview
  - 保持既有返回路径与 snapshot 语义
- 图片消息允许带短 caption。
- 覆盖 `thread ready with media`、`media picker open`、`media sending success` 三类主要状态。
- 聊天详情仍必须保持固定 top bar + 固定 composer + 仅 thread viewport 滚动。

## 暂不纳入本切片

- 真正的相册访问、拍照、系统权限申请和文件选择器。
- 大图查看器、缩放手势、转场动效和原图下载。
- 视频、语音、文件、位置和 sticker。
- 真实上传进度、断点续传、失败重传队列。
- 复杂媒体搜索、媒体分组相册和云端缓存策略。

## 交付假设

- 当前阶段允许使用 mock media fixture，不要求真实图片资产，但消息气泡必须明显区分于纯文本。
- 图片消息的 caption 继续复用消息文本语义，便于列表预览、搜索和 snapshot 持久化复用。
- 由于真实 Figma 文件仍未闭环，本轮继续允许以 repo-side handoff brief 代理 `D2`，但必须在 acceptance 和 parity 中显式记录。

## 验收标准

| ID | 验收项 | 通过标准 |
|---|---|---|
| AC-S7-1 | 详情页展示媒体消息 | 会话详情中至少有入站和出站两类图片消息的稳定渲染 |
| AC-S7-2 | 可打开媒体选择器 | 用户可从详情 composer 打开独立 media picker sheet |
| AC-S7-3 | 媒体消息可发送 | 选择 fixture 图片后，消息流出现新的出站图片消息，并更新列表预览 |
| AC-S7-4 | 媒体消息可恢复 | 冷启动恢复到详情时，最近发送的媒体消息仍可见 |
| AC-S7-5 | 视口边界正确 | 聊天页固定 top bar 与 composer，仅 thread viewport 滚动；picker 作为底部覆盖层出现 |

## 核心流程约束

### 在详情中查看媒体消息

1. 用户进入 `S3 Chat Detail`。
2. 消息流中可见图片消息。
3. 图片消息必须和纯文本气泡有足够明显的结构差异。

### 打开媒体选择器

1. 用户点击 composer 左侧 `Media` 入口。
2. 页面底部出现轻量 media picker sheet。
3. 用户可以选择某个 fixture 图片，或关闭 picker。

### 发送媒体消息

1. 用户在 picker 中选择图片。
2. 应用返回详情并插入新的出站图片消息。
3. 列表预览与最近消息时间同步更新。

### 冷启动恢复媒体上下文

1. 用户在含媒体消息的详情页退出应用。
2. 重新启动并恢复详情。
3. 最近发送的媒体消息仍然可见，不退化成丢失字段的普通文本。

## 领域落点

- `MediaAttachment`: 可发送的图片 fixture 描述
- `MediaPickerLoadResult`: `Success`、`Failed`
- `SendMediaResult`: `Success`、`Failed`
- `ChatDetailRepository`: 扩展媒体选择与发送边界
- `Message`: 增加可选的媒体负载，而不是只能承载纯文本

## 对 KMP 的要求

- `shared-domain` 需要为图片消息提供明确的模型，而不是在 Android UI 层用 `view tag` 假装媒体。
- `shared-data` 需要提供 fixture 媒体列表、媒体消息发送和 snapshot 持久化能力。
- `androidApp` 需要为媒体消息定义真实的 bubble、media picker sheet 和发送反馈。
- 新增媒体能力不能破坏既有文本发送、失败重试和搜索路径。
