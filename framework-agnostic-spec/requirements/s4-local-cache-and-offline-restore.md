# S4 本地缓存与离线恢复

## 切片目标

在 `S1-S3` 已打通登录、列表和详情发送主路径的基础上，为 KMP 增加本地快照缓存与冷启动恢复能力，使用户在重新打开应用时能够直接回到上次主路径，并看到最近的会话与消息片段，即使没有重新拉取“在线数据”也不会退回到空白主壳。

## 用户价值

- 用户关闭应用后重新打开，可以回到上次停留的列表或聊天详情，而不是每次都从默认列表重新开始。
- 用户刚发送或刚重试成功的消息，在冷启动后仍然可见，不会因为 demo 仓储重置而消失。
- 用户能明显知道当前内容来自本地缓存，并可以在恢复后继续浏览或手动刷新。

## 本切片范围

- 持久化最近一次成功进入的主路径快照，覆盖：
  - chat list 当前排序和最近消息预览
  - 搜索关键词
  - 当前打开的 chat detail 及最近消息片段
  - 最近一次 refresh / send / retry 后的结果
- 冷启动且 session 有效时，优先尝试恢复本地快照。
- 如果快照有效：
  - 可直接恢复到 `Chat List` 或 `Chat Detail`
  - 页面内必须有“来自本地缓存 / 可能不是最新内容”的内联提示
- 如果快照缺失或损坏：
  - 回退到 `S1-S3` 的正常加载路径
  - 不得卡死在 restoring
- 提供 debug-only 的“清空本地缓存”入口，用于验证 fallback 路径。
- logout 时必须清掉快照，避免旧账号上下文残留。

## 暂不纳入本切片

- 真实数据库、分页缓存和增量同步 cursor。
- 多账号隔离缓存、媒体文件缓存和附件断点续传。
- 未发送草稿的完整恢复。
- 真正的在线 / 离线系统网络检测。

## 交付假设

- 当前阶段仍允许使用 fixture-backed chat repository，但 repository 状态需要具备 snapshot 导出 / 恢复能力。
- “离线恢复”在本轮主要表现为“无须重新生成 fixture，也可从本地 snapshot 恢复到最近主路径”，不要求接入真实网络或飞行模式检测。
- 由于真实 Figma 文件仍未闭环，本轮可继续以 repo-side handoff brief 代理 `D2`，但必须在 acceptance 和 parity 中显式记录。

## 验收标准

| ID | 验收项 | 通过标准 |
|---|---|---|
| AC-S4-1 | 冷启动恢复列表 | 用户停留在 chat list 后杀掉应用并重启，在 session 仍有效的前提下，可直接恢复到最近列表态，并看到缓存恢复 banner |
| AC-S4-2 | 冷启动恢复详情 | 用户停留在 chat detail 后杀掉应用并重启，可直接恢复到同一会话详情，并保留最近消息片段 |
| AC-S4-3 | 最近变更持久化 | refresh、send success 和 retry success 对列表预览 / 详情消息流的修改，在冷启动后仍可见 |
| AC-S4-4 | 快照缺失可回退 | 清空本地缓存后冷启动，不会恢复到旧详情；应用回到正常列表加载路径 |
| AC-S4-5 | 恢复来源可感知 | 恢复成功时，列表页或详情页都必须展示“来自本地缓存、可能不是最新内容”的非阻塞提示 |
| AC-S4-6 | logout 清空缓存 | logout 后再次冷启动，不恢复任何旧聊天上下文 |

## 核心流程约束

### 冷启动恢复列表

1. 用户在 chat list 浏览会话或刷新列表。
2. 应用把当前列表快照落到本地。
3. 用户杀掉应用并重新打开。
4. 如果 session 和快照都有效，直接恢复到最近 chat list，并展示缓存恢复提示。

### 冷启动恢复详情

1. 用户进入 chat detail，并产生最近消息变化。
2. 应用把当前详情快照落到本地。
3. 用户杀掉应用并重新打开。
4. 如果 session 和快照都有效，直接恢复到同一详情页，不先闪回列表。

### 快照缺失回退

1. 用户通过 debug-only 入口清空本地缓存。
2. 用户重新打开应用。
3. 应用仍恢复 session，但走正常列表加载路径，而不是尝试恢复旧详情。

## 领域落点

- `SyncSnapshotRoute`: `CHAT_LIST`、`CHAT_DETAIL`
- `SyncSnapshot`: 最近一次主路径快照
- `SyncSnapshotRestoreResult`: `Restored`、`NoSnapshot`、`Failed`
- `SyncSnapshotSaveResult`: `Success`、`Failed`
- `SyncRepository`: 读写和清空快照的边界

## 对 KMP 的要求

- `shared-domain` 需要承载 snapshot 读写语义，不把 SharedPreferences 或 Android 生命周期直接塞进用例。
- `shared-data` 需要明确 snapshot storage 适配层，允许用内存实现做测试、用 Android 持久化实现做设备验证。
- `androidApp` 必须把“恢复的是缓存内容”表达成页面内联反馈，而不是只靠 log。
- 恢复详情时，不允许退回到 placeholder 或重新生成与上次不一致的 fixture 数据。
