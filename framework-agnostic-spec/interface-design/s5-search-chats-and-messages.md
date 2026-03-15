# S5 UI 设计: 搜索会话 / 消息

## 涉及屏幕

- `UI-S3 Chat List`
- `UI-S5 Search`
- `UI-S4 Chat Detail`

`S5` 在 `S2` 的列表过滤之上新增独立搜索结果页，不替换 `S2` 的原有列表内搜索。

## UI-S3 Chat List / Search Entry

### 信息层级

1. 顶部轻量导航区
2. 固定搜索框
3. 条件性出现的“全局搜索”入口
4. 固定状态反馈区
5. 可滚动列表 viewport
6. 固定 debug / bottom navigation

### 交互规则

- 当关键词为空时，不展示“全局搜索”入口。
- 当关键词非空时，列表仍按 `S2` 规则做本地过滤。
- 同时提供一个轻量入口把当前关键词带入 `UI-S5 Search`。

### 视觉规则

- “全局搜索”入口不应喧宾夺主，优先使用低干扰 info row 或次级 chip。
- 它应该看起来像对 Telegram 搜索流程的扩展，而不是新卡片模块。

## UI-S5 Search / Default

### 信息层级

1. 顶部轻量导航区
   - 左侧 `返回`
   - 中间标题 `Search`
   - 右侧切片标记或轻量空白
2. 固定搜索框
3. 固定状态反馈区
4. 可滚动结果 viewport
5. 固定底部导航承载区

### 布局约束

- 页面主壳必须撑满视口。
- 顶部导航、搜索框、状态反馈区和底部导航保持固定。
- 只有搜索结果 viewport 可滚动。
- 不在搜索页复用 `S2` 的 debug 区块。

## UI-S5 Search / Loading

- 保持顶部和搜索框不跳变。
- 结果区使用轻量 skeleton rows。
- 不使用 modal loading。

## UI-S5 Search / Result

### 信息层级

1. `Chats` section label
2. chat result rows
3. `Messages` section label
4. message result rows

### 视觉规则

- chat result 优先复用 `S2` 会话列表行语言。
- message result 继续复用头像、标题、时间、两行文本的 Telegram 风格密度。
- 可以轻量高亮关键词，但不应变成高饱和搜索页。
- section label 需要明显，但不做大卡片标题。

### 交互规则

- 点击 chat result: 进入对应会话详情。
- 点击 message result: 进入对应会话详情，并带上“命中消息定位”上下文。
- 从详情返回时，恢复原搜索结果页与关键词。

## UI-S5 Search / Empty

- 保持顶部和搜索框。
- 结果区显示明确空态:
  - 标题: `未找到匹配的结果`
  - 说明: `试试更短的关键词，或改搜会话名与消息里的核心词。`
- 提供轻量 CTA:
  - `清除关键词`

## UI-S5 Search / Error

- 保持顶部和搜索框。
- 结果区显示页面级错误说明和 retry 入口。
- 失败反馈不能只用 toast。

## UI-S4 Chat Detail / Search Return

### 信息层级

1. 顶部轻量导航区
2. 条件性出现的搜索定位 banner
3. 可滚动消息流 viewport
4. 固定 composer
5. 低干扰 debug-only 区域

### 交互规则

- 从 search result 进入详情时:
  - 顶部 info banner 需要说明当前来自搜索结果
  - 命中的消息气泡要被轻量高亮，或提供等价定位表达
- 返回行为:
  - 若当前详情来自搜索结果，则 `返回` 回到搜索结果页
  - 若来自普通列表，则 `返回` 仍回到 chat list

## 状态映射

| State | 页面表达 |
|---|---|
| `search idle` | 搜索框可编辑，结果区提示输入关键词 |
| `search loading` | 顶部稳定，结果区 skeleton |
| `search result: chats+messages` | 同时展示两组结果 |
| `search result: chats-only` | 只展示 `Chats` 分组 |
| `search result: messages-only` | 只展示 `Messages` 分组 |
| `search empty` | 空态文案 + 清除入口 |
| `search error` | 页面级错误 + retry |
| `detail from search-hit` | 详情页顶部 banner + 命中消息高亮 |

## 无障碍与系统约束

- 搜索结果行、返回按钮、搜索框和底部导航触控目标不小于平台建议最小尺寸。
- 命中消息定位不能只靠颜色表达，最好同时有 banner 或标签提示。
- 搜索页和详情页的返回路径要兼容系统返回手势。
- 关键词高亮需兼顾对比度，不能牺牲正文可读性。
