# S2 UI 设计: 会话列表

## 涉及屏幕

- `UI-S3 Chat List`
- `UI-S5 Search`
- `UI-S4 Chat Detail Entry Placeholder`

`S2` 的成功状态是真实 chat list，不再使用 `S1` 的 `Chat List Entry Shell` 调试占位。

## UI-S3 Chat List / Default

### 信息层级

1. 顶部轻量导航区
   - 左侧 `编辑`
   - 中心标题 `Chats`
   - 右侧 `写消息`
2. 固定搜索框
3. 固定状态反馈区
4. 可滚动会话列表 viewport
5. 固定底部导航承载区

### 布局约束

- 页面主壳必须撑满视口高度。
- 顶部导航、搜索框、状态反馈区和底部导航保持固定。
- 只有列表 viewport 可滚动。
- demo-only debug 控件如果存在，必须压缩成低干扰区域，不能把列表挤成残余空间。

### 视觉规则

- 顶部不是厚重 app bar，整体更接近 Telegram iOS 的轻量导航。
- 顶部左右动作更接近 Telegram 的轻量文字按钮，不要做厚重胶囊按钮。
- 搜索框应接近全宽、大圆角、低对比。
- 列表为平面结构，不使用卡片流。
- 每行包含:
  - 左侧圆形头像占位
  - 中间两行文本: 标题、最近消息
  - 右上时间
  - 右侧未读徽标和静音指示
- 分隔线极轻，整体留白紧凑。
- 默认 fixture 至少应能填满 1.5 屏列表，以暴露真实滚动和信息密度。

### 交互

- 点击列表行: 给出轻量按压反馈，进入 `S3` placeholder。
- 下拉: 触发 refreshing。
- 搜索框: 聚焦后列表即进入搜索态。
- 下拉和滚动只作用于列表 viewport，不带动整个页面一起移动。

## UI-S3 Chat List / Loading

- 保持顶部和搜索框结构不跳变。
- 列表区域使用 skeleton row 或内联 loading block。
- 不出现系统级 modal 或全屏空白。
- 当 skeleton 少于一屏时，viewport 仍应被撑满，而不是露出大片空白。

## UI-S3 Chat List / Refreshing

- 通过顶部下拉 indicator 或等价内联 refresh indicator 表达。
- refreshing 时保留当前列表内容，不整页清空。
- refreshing 不应让底部导航、搜索框或 debug 区跟着滚动。

## UI-S3 Chat List / Empty

- 保持顶部和搜索框。
- 列表区域显示空态插图占位或轻量文字区。
- CTA 使用轻量按钮，文案偏“重新加载”或“恢复默认数据”。

## UI-S3 Chat List / Error

- 保持顶部和搜索框。
- 列表区域显示错误说明和重试入口。
- 失败反馈不能只用 toast；页面必须可理解。

## UI-S5 Search / Focused

- 搜索框聚焦后仍保留顶部结构。
- 允许展示“清除”动作。
- 默认列表与搜索结果使用同一视觉语言。

## UI-S5 Search / Result

- 匹配标题的关键词可轻量高亮。
- 无结果时使用明确空态:
  - 标题: `未找到匹配的会话`
  - 说明: `试试搜索会话名或最近消息中的关键词。`

## UI-S4 Chat Detail Entry Placeholder

`S2` 只负责“点进下一步”的承接提示，不实现真实聊天详情。

- 列表行点击后:
  - 顶部或页面内出现轻量 info banner
  - 文案说明 `S3` 尚未验收，但目标会话已被选中

## 状态映射

| State | 页面表达 |
|---|---|
| `loading` | 顶部+搜索框稳定，列表 skeleton |
| `ready` | 完整会话列表 |
| `refreshing` | 保留列表内容 + refresh indicator |
| `empty` | 空态文案 + 恢复入口 |
| `error` | 错误说明 + 重试按钮 |
| `search focused` | 搜索框聚焦，结果区开始更新 |
| `search result` | 过滤后的会话列表或无结果空态 |

## 无障碍与系统约束

- 列表行、顶部动作、搜索框、底部导航触控目标不小于平台建议最小尺寸。
- 错误、未读、静音和刷新状态不能只靠颜色。
- 搜索聚焦、列表刷新和底部导航切换需兼容 reduced motion。
- 长标题和长预览要被安全截断，不能把右侧时间和未读挤变形。

## 与 S1 的边界

- `S1` 的成功落点只证明进入主壳。
- 从 `S2` 起，主壳必须表现为真正 chat list，而不是带数据卡片的调试页。
