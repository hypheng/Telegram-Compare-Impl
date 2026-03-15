# S6 UI 设计: 设置与个人资料

## 涉及屏幕

- `UI-S3 Chat List`
- `UI-S5 Search`
- `UI-S6 Settings / Profile`

`S6` 把底部 `Settings` tab 升级成真实页面，但不破坏 `S1-S5` 已有的聊天与搜索路径。

## UI-S6 Settings / Profile / Default

### 信息层级

1. 顶部轻量导航区
   - 中间标题 `Settings`
   - 右侧允许保留轻量占位，不做重操作区
2. profile hero / account card
3. `Account` section
4. `Preferences` section
5. `Session` section
6. 固定底部导航承载区

### 布局约束

- 页面主壳必须撑满视口。
- 顶部导航和底部导航固定。
- 只有设置内容 viewport 可滚动。
- 不在设置页复用聊天页的 debug 区块。

## UI-S6 / Profile Hero

### 信息层级

1. 圆形头像占位
2. display name
3. phone number
4. username
5. about / bio

### 视觉规则

- profile hero 应该像 Telegram 设置页的轻量账号头部，而不是营销卡片。
- 避免重阴影和大色块，优先使用白底、细边框和系统蓝强调。
- phone、username、about 的层级弱于 display name。

## UI-S6 / Preferences

### 行内容

- title
- supporting text
- 当前状态表达
- 行内 toggle 或等价开关

### 交互规则

- 点击整行或开关都可切换状态。
- 切换反馈优先使用行内状态变化和顶部 info banner，不依赖 toast。
- 用户切换后不应整页重载。

### 建议偏好项

- `Notifications`
- `Auto-download media on Wi-Fi`
- `Reduced motion`

## UI-S6 / Session Actions

- `退出登录` 作为危险操作单独分组，不和偏好项混排。
- 动作按钮视觉上要明确，但不做系统弹窗依赖。
- logout 后回到 `S1 Login`，旧主路径不应被直接恢复。

## UI-S6 / Loading

- 保持顶部与底部导航稳定。
- profile hero 和设置行使用轻量 skeleton。
- 不用全屏 modal loading。

## UI-S6 / Failed

- 保持顶部与底部导航。
- 内容区显示页面级错误说明和 `重试加载`。
- 错误页仍应允许用户通过底部导航回到聊天主壳。

## 状态映射

| State | 页面表达 |
|---|---|
| `settings loading` | 顶部稳定，profile 和设置行 skeleton |
| `settings ready` | profile hero + sectioned preferences + session action |
| `settings failed` | 页面级 error state + retry |
| `preference toggled` | 行内开关变化 + 顶部 info banner |
| `logout from settings` | 退出成功后回到登录页 |

## 无障碍与系统约束

- 设置行、toggle、logout button 和底部导航触控目标不小于平台建议最小尺寸。
- 开关状态不能只靠颜色表达，要有 `On/Off` 或等价文本。
- 系统返回从设置页返回最近的聊天主壳上下文。
- `Reduced motion` 的偏好项至少要在 UI 文案上被明确表达，并为后续切片提供扩展点。
