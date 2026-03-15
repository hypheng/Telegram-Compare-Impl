# S4 UI 设计: 本地缓存与离线恢复

## 涉及屏幕

- `UI-S1 Launch / Session Restore`
- `UI-S3 Chat List`
- `UI-S4 Chat Detail`

`S4` 不新增独立大页面，而是为 `UI-S1/UI-S3/UI-S4` 增加“从本地缓存恢复最近上下文”的状态与提示。

## UI-S1 Launch / Cached Restore

### 信息层级

1. restoring 进度反馈
2. 恢复来源判断
3. 跳转到最近缓存上下文

### 行为约束

- 若 session 和 snapshot 都有效，restoring 完成后应直接跳到最近的 `Chat List` 或 `Chat Detail`。
- 不允许先闪出登录页，也不允许先回到默认列表再跳详情。
- 若 snapshot 缺失或损坏，可回退到正常主路径，但 restoring 反馈必须完整走完。

## UI-S3 Chat List / Restored From Cache

### 信息层级

1. 顶部轻量导航
2. 固定搜索框
3. 固定缓存恢复 banner
4. 可滚动列表 viewport
5. 固定 debug / bottom navigation

### 视觉规则

- 缓存恢复 banner 继续使用 Telegram 风格的浅色 info banner，不做警告红条。
- banner 文案应表达“来自本地缓存，可能不是最新内容”，但不制造恐慌。
- 列表结构继续保持 `S2` 的 fixed chrome + scoped scroll，不因 S4 恢复逻辑退回整页滚动。

### 交互

- 用户恢复到列表后可以继续搜索、刷新、进入详情。
- debug-only 区域需要有“清空本地缓存”入口，用于验证恢复回退。

## UI-S4 Chat Detail / Restored From Cache

### 信息层级

1. 顶部轻量导航区
2. 固定缓存恢复 banner
3. 可滚动消息流 viewport
4. 固定 composer
5. 低干扰 debug-only 区域

### 视觉规则

- 恢复到详情时，banner 要出现在顶部和消息流之间，不得把 composer 挤出视口。
- 消息气泡、meta 和 composer 继续沿用 `S3` 视觉语言，不因为恢复态引入新卡片层。
- 如果最近一条消息来自 send / retry，恢复后应与此前视觉一致，不能退回旧 fixture 文案。

### 交互

- 用户可直接返回列表或继续发送消息。
- 恢复态不应锁死 composer，也不应阻塞 retry。

## UI-S3 / UI-S4 清空缓存后的回退

- 清空本地缓存后，下一次冷启动不再展示“恢复最近上下文”的 banner。
- 恢复路径回到 `S1-S3` 的正常列表加载。
- 如果 logout 后重启，也不应残留任何旧会话列表或详情内容。

## 状态映射

| State | 页面表达 |
|---|---|
| `restoring-session-only` | `UI-S1` restoring progress，结束后走正常列表加载 |
| `restoring-from-cache:list` | 直接进入 `UI-S3`，显示缓存恢复 banner |
| `restoring-from-cache:detail` | 直接进入 `UI-S4`，显示缓存恢复 banner |
| `cache-missing` | 走 `S1-S3` 正常路径，不显示缓存 banner |
| `cache-cleared` | 下一次冷启动只恢复 session，不恢复上下文 |
| `cache-invalid` | 回退正常路径，并在轻量 banner 中说明缓存不可用 |

## 无障碍与系统约束

- 缓存恢复 banner 不能只靠颜色表达“离线 / 缓存”状态。
- 恢复路径不应依赖复杂动画，需兼容 reduced motion。
- 清空缓存 debug-only 入口必须保持触控尺寸，但不应抢占主路径注意力。
