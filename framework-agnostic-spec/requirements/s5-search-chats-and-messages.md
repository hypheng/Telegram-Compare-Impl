# S5 搜索会话 / 消息

## 切片目标

在 `S2` 的会话列表过滤和 `S3` 的详情页基础上，增加一个可验收的全局搜索结果流，使用户不仅能在当前列表里过滤会话，还能跨全部 demo 消息搜索关键词，并从结果直接跳到目标会话或目标消息。

## 用户价值

- 用户输入关键词后，不止能看到当前列表过滤结果，还能看到消息级命中结果。
- 用户可以从搜索结果直接跳到目标会话，而不是只能回到列表里手动继续找。
- 用户点击消息结果后，可以直接进入目标详情，并知道当前是从搜索结果定位到该消息。

## 本切片范围

- 保留 `S2` 的列表内本地过滤，不破坏原有 `title + last preview` 过滤体验。
- 在 chat list 中增加“全局搜索”入口，使用当前输入关键词进入独立 `UI-S5 Search`。
- 搜索范围覆盖:
  - chat title
  - chat last message preview
  - chat detail 中的 message text
- 搜索结果页按两组呈现:
  - `Chats`
  - `Messages`
- 点击 chat result:
  - 进入对应 `S3` 详情页
- 点击 message result:
  - 进入对应 `S3` 详情页
  - 页面内有“来自搜索结果 / 已定位消息”的轻量提示
  - 目标消息需要被高亮或有等价定位表达
- 覆盖 `idle`、`loading`、`result`、`empty`、`error` 五类主要状态。
- 搜索页必须保持固定 top bar + 固定 search field + 仅结果 viewport 滚动的结构。

## 暂不纳入本切片

- 真实服务端全文搜索、排序打分和 typo tolerant/fuzzy search。
- 搜索历史、最近搜索、热门搜索和筛选器。
- 从详情页反向发起全局搜索。
- 复杂消息锚点滚动、逐字高亮、跨页定位和媒体搜索。
- 搜索设置页、AI overlay 或其它非聊天域内容。

## 交付假设

- 当前阶段仍允许使用 fixture-backed repository，但搜索结果必须来自明确的共享层语义，而不是只在 Android UI 层遍历 View。
- 如果 `chat list` 当前处于 `error` scenario，全局搜索允许返回 `error` 状态，以验证搜索页的失败反馈。
- 由于真实 Figma 文件仍未闭环，本轮继续允许以 repo-side handoff brief 代理 `D2`，但必须在 acceptance 和 parity 中显式记录。

## 验收标准

| ID | 验收项 | 通过标准 |
|---|---|---|
| AC-S5-1 | 进入全局搜索 | 用户在 chat list 输入关键词后，可进入独立搜索结果页，而不是停留在同一列表过滤态 |
| AC-S5-2 | 结果分组展示 | 搜索结果页至少展示 `Chats` 和 `Messages` 两组，并复用 Telegram 风格列表层级 |
| AC-S5-3 | 会话结果可跳转 | 点击 chat result 后可进入对应详情页 |
| AC-S5-4 | 消息结果可定位 | 点击 message result 后可进入对应详情页，并有消息定位提示与高亮表达 |
| AC-S5-5 | 无结果可理解 | 关键词无命中时展示明确 empty state，不出现空白页 |
| AC-S5-6 | 搜索失败可恢复 | 搜索失败时展示页面级 error state 和 retry 入口 |
| AC-S5-7 | 视口边界正确 | 搜索页主壳撑满手机屏幕，顶部和搜索框固定，只有结果区域滚动 |

## 核心流程约束

### 从 chat list 进入全局搜索

1. 用户在 `S2 Chat List` 输入关键词。
2. 列表可以继续做轻量过滤。
3. 用户点击“全局搜索”入口后进入独立搜索页。
4. 搜索页先进入 loading，再展示 grouped results / empty / error。

### 从 chat result 跳到详情

1. 用户在 `Chats` 分组点击某条结果。
2. 应用直接打开目标 `S3 Chat Detail`。
3. 返回时应回到搜索结果，而不是丢失搜索上下文。

### 从 message result 定位到详情

1. 用户在 `Messages` 分组点击某条结果。
2. 应用直接打开该消息所在的会话详情。
3. 页面内必须有“定位自搜索结果”的提示。
4. 命中的目标消息必须高亮或以等价方式表达“已定位”。

### 搜索失败与空态

1. 搜索无命中时，用户看到明确 empty state 和改写关键词提示。
2. 搜索失败时，用户看到页面级错误和 retry 按钮。
3. 无论 empty 还是 error，搜索页固定 chrome 不应塌陷。

## 领域落点

- `SearchQuery`: 搜索关键词边界
- `MessageSearchHit`: 消息级命中结果
- `SearchLoadResult`: `Success`、`Empty`、`Failed`
- `SearchRepository`: 全局搜索边界

## 对 KMP 的要求

- `shared-domain` 需要承载全局搜索语义，不把 Android View 遍历写成“搜索能力”。
- `shared-data` 需要从 fixture repository 中统一导出 chat hit 和 message hit。
- `androidApp` 需要显式表达 `UI-S5` 的 `idle / loading / result / empty / error`。
- 点击 message result 后，详情页需要保留搜索上下文，至少能返回搜索结果并表达命中消息定位状态。
