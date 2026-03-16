# S8 UI 设计: Contacts 联系人

## 涉及屏幕

- `UI-S3 Chat List`
- `UI-S4 Chat Detail`
- `UI-S9 Contacts`

`S8` 把底部 `Contacts` tab 升级成真实联系人页，并把 `写消息` 顶部入口接回联系人主路径。

## UI-S9 Contacts / Default

### 信息层级

1. 顶部轻量导航区
   - 中间标题 `Contacts`
   - 右侧允许保留轻量 `Add` 占位，不做重编辑模式
2. 固定搜索框
3. 联系人列表
4. 轻量状态反馈 / 可选 debug-only 场景入口
5. 固定底部导航承载区

### 布局约束

- 页面主壳必须撑满视口。
- 顶部导航、搜索框和底部导航固定。
- 只有联系人列表 viewport 可滚动。
- debug-only 场景入口只允许作为轻量 footer chips 出现，不复用聊天页的整块 debug panel。

## UI-S9 / Contact Row

### 信息层级

1. 圆形头像占位
2. display name
3. status / phone secondary line
4. 条件性弱提示:
   - `已有聊天`
   - 或弱语义的 `新对话`

### 视觉规则

- 联系人 row 延续 Telegram 联系人页与聊天列表的白底平面列表语言。
- 避免联系人卡片化或过重色块分割。
- secondary line 优先弱化，不与姓名竞争层级。
- 是否已有聊天使用轻量 trailing text，不使用重色块 badge。

## UI-S9 / Search

### 交互规则

- 搜索框默认固定在标题区下方。
- 支持按姓名和手机号过滤。
- 输入后应在当前页内轻量更新，不跳出联系人页。
- 清空关键词后回到完整联系人列表。

### 结果规则

- 命中结果继续使用联系人 row 视觉。
- 搜索无结果时显示联系人专属空态:
  - title: `未找到匹配的联系人`
  - body: `试试姓名拼音、昵称或手机号片段。`

## UI-S9 / Failed

- 保持顶部、搜索框和底部导航稳定。
- 内容区显示页面级错误说明和 `重试加载`。
- 错误页仍应允许用户通过底部导航回到 `Chats` 或 `Settings`。

## UI-S9 / Open Existing Chat

- 点击已有聊天的联系人:
  - 行按压反馈
  - 进入对应 `Chat Detail`
  - 顶部 status pill 可提示 `已从联系人进入 <name>`

## UI-S9 / Start New Chat

- 点击无现有会话的联系人:
  - 行按压反馈
  - 创建新的单聊 fixture
  - 进入 `Chat Detail`
  - 顶部 status pill 可提示 `已开始与 <name> 的新对话`
- 从该详情返回联系人页时:
  - 原搜索关键词保留
  - 同一联系人弱提示刷新为 `已有聊天`

## UI-S9 / Return Path

- 从联系人进入 `Chat Detail` 后，系统返回优先回到联系人页。
- 若联系人页之前处于搜索态，返回后应保留原查询和结果。
- 返回时不能复用过期列表快照，必须刷新联系人结果，确保新聊天状态已回写。
- 从联系人进入 `Settings` 后返回，也应优先回到联系人页，而不是聊天列表。

## 状态映射

| State | 页面表达 |
|---|---|
| `contacts loading` | 顶部和底部固定，列表 skeleton |
| `contacts ready` | 联系人 row 列表 |
| `contacts empty` | 页面级 empty state |
| `contacts failed` | 页面级 error state + retry |
| `contact search active` | 固定搜索框 + 过滤结果 |
| `contact search empty` | 联系人专属空态 |
| `open existing contact chat` | 进入既有聊天详情 |
| `start new contact chat` | 创建新聊天并进入详情 |

## 无障碍与系统约束

- 联系人 row、搜索框、重试按钮和底部导航触控目标不小于平台建议最小尺寸。
- 联系人是否已有聊天不能只靠颜色区分，必要时用弱文本提示。
- 系统返回遵循平台默认手势，不自造联系人专属手势。
- 联系人搜索、状态提示和新聊天反馈都不能只靠动画表达，要保留清晰文案。
