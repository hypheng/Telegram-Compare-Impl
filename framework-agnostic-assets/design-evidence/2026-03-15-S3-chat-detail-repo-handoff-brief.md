# 2026-03-15 S3 Chat Detail Repo-side Handoff Brief

- Slice: `S3 单聊详情与文本发送`
- Handoff Type: `repo-side proxy`
- Status: `ready for KMP implementation`
- Figma Status: `blocked`

## 为什么先用 repo-side brief

当前 `figma_remote` 已接入，但项目仍缺可编辑的目标 Figma 文件和真实 frame/node links。为了不继续阻塞 KMP 主路径，本 brief 暂时作为 `S3` 的 handoff 代理；后续拿到真实文件后，必须回填真实 Figma 证据。

## 目标 frame 结构

1. `30 S3 Chat Detail / Loading`
2. `31 S3 Chat Detail / Default`
3. `32 S3 Chat Detail / Sending`
4. `33 S3 Chat Detail / Failed`
5. `34 S3 Chat Detail / Retrying`
6. `35 S3 Chat Detail / Error`

## Frame 说明

### 30 S3 Chat Detail / Loading

- 顶部轻量返回结构已出现。
- 消息区使用 4-5 条 skeleton bubble。
- composer 区保留尺寸但不激活。

### 31 S3 Chat Detail / Default

- 顶部包含返回、会话标题、弱层级说明。
- 消息区展示 incoming / outgoing 混合历史。
- composer 位于底部，输入框圆角，发送按钮低噪音。

### 32 S3 Chat Detail / Sending

- composer 发送后，新增一条 outgoing bubble。
- 该 bubble 弱层级标记 `发送中`。

### 33 S3 Chat Detail / Failed

- 原 bubble 进入 failed，保留原文本。
- bubble 内或下方提供 `重试` 动作。
- 页面仍允许继续输入其他文本。

### 34 S3 Chat Detail / Retrying

- 原 failed bubble 进入 retrying。
- 重试完成后回到 sent。

### 35 S3 Chat Detail / Error

- 用于详情读取失败场景。
- 页面提供 `重试加载` 与 `返回列表`。

## Prototype 主路径

1. 从 `S2` chat list 点击 row
2. 进入 `30 Loading`
3. 落到 `31 Default`
4. 输入文本后进入 `32 Sending`
5. 正常发送落回 `31 Default`
6. debug 强制失败时进入 `33 Failed`
7. 点击 retry 进入 `34 Retrying`
8. 重试成功后回到 `31 Default`

## Ready-for-dev 注释

- outgoing bubble 右对齐，incoming bubble 左对齐。
- sending / failed / retrying 状态必须有文字语义。
- 返回列表后要刷新最近消息预览与排序。
- debug-only 的 `下一条发送失败` 只用于验收，不计入最终产品交互。

## 当前 blocker

- 仍缺真实 Figma 文件、frame/node links 和 inspect payload。
