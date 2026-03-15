# Interaction States

## 设计原则

- 用户每次操作都要有立即反馈
- 长操作优先用内联反馈，不用阻塞式弹窗
- 失败操作要可恢复
- AI 功能必须是 overlay，不阻塞聊天主流程

## 关键状态矩阵

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Login | 点击登录 | CTA loading + form disabled | 自动进入会话列表 | 顶部错误 + 可重试 |
| Refresh chat list | 下拉刷新 | 内联 refresh indicator | 列表内容更新 | 非阻塞 toast / banner |
| Send message | 点击发送 | 气泡进入 sending 状态 | 气泡变 sent | 气泡标记 failed + retry |
| Restore session | App launch | skeleton / splash progress | 直接恢复上下文 | fallback 到登录 |
| AI summarize | 打开 AI overlay | overlay loading | 流式或分段展示结果 | overlay error + retry |

## S1 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Retry restore | 登录页点击重试恢复 | 顶部 restoring banner | 直接进入主壳入口 | 回到登录页并更新错误文案 |
| Logout | 主壳点击退出登录 | 次级按钮禁用 | 返回登录页 | 轻量 banner，允许重试 |
| Seed expired session (debug) | 主壳点击调试入口 | 轻量 toast / banner | 下次冷启动可验证 restore failed | 不展示系统级错误，只提示写入失败 |

## S2 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Load chat list | 登录成功或恢复成功进入主壳 | 固定 chrome + 列表 skeleton / inline loading | 进入默认列表态 | 页面级 error state + retry |
| Search chat list | 搜索框输入关键词 | 结果区域轻量更新 | 展示过滤结果 | 无结果空态，不用系统错误 |
| Open chat from list | 点击列表行 | 行按压反馈 + 固定顶部结构 | 进入 `S3` 承接提示 | 仍停留列表并给出 banner |

## S3 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Load chat detail | 点击会话列表行 | 固定顶部 + 固定 composer 占位 + 详情 skeleton bubble | 进入聊天详情默认态 | 页面级 error state + retry |
| Send message | 点击发送 | 末尾 bubble 标记 sending | 气泡变 sent，列表预览回写 | 气泡标记 failed + retry |
| Retry failed message | 点击 failed bubble 的 retry | 原 bubble 标记 retrying | 原气泡恢复 sent | 保持 failed，并给出恢复提示 |

## S4 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Restore snapshot | App launch with active session + cached snapshot | `UI-S1` restoring progress | 直接恢复列表或详情，并显示缓存恢复 banner | 回退正常列表加载，并提示缓存不可用 |
| Clear local cache (debug) | 点击 debug-only 清空缓存 | debug chip 短暂禁用 | 下次冷启动走正常路径 | banner 提示清空失败 |

## S5 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Open global search | 列表内输入关键词后点击全局搜索 | 搜索页固定 chrome + 结果 skeleton | grouped results / empty state | 页面级 search error + retry |
| Open chat result | 点击 `Chats` 分组中的结果 | 行按压反馈 + 轻量页面跳转反馈 | 进入对应详情 | 停留搜索页并给出 banner |
| Open message result | 点击 `Messages` 分组中的结果 | 行按压反馈 + 轻量页面跳转反馈 | 进入详情并高亮命中消息 | 停留搜索页并给出 banner |

## S6 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Open settings | 点击底部 `Settings` tab | 顶部稳定 + profile / rows skeleton | 进入设置页 ready state | 页面级 error state + retry |
| Toggle preference | 点击设置行或开关 | 当前行短暂禁用或状态更新中 | 行内状态更新 + 顶部 info banner | 保持原状态并给出 banner |
| Logout from settings | 点击 `退出登录` | 按钮短暂禁用 | 回到登录页 | 保持设置页并给出 banner |

## S7 补充状态

| Flow | Trigger | Loading Feedback | Success Feedback | Failure Feedback |
|---|---|---|---|---|
| Open media picker | 点击 composer 中的 `Media` 入口 | 底部 sheet loading / 占位 | picker ready，展示 fixture 图片 | sheet 内 error + retry / close |
| Send media message | 在 picker 中选择图片 | 顶部 banner + 详情保持稳定 | 新媒体消息追加到 thread 尾部 | 保持当前 thread，给出发送失败提示 |
| Restore detail with media | 冷启动恢复到包含媒体消息的详情 | 继续沿用 `S4` restoring 反馈 | 最近媒体消息仍可见 | 回退正常详情加载并提示缓存不可用 |

## 手势与反馈

- Chat list row: tap 进入，swipe 作为次级操作预留
- Chat list search: 默认固定在标题区下方，优先做 Telegram 类似的顺滑进入与聚焦反馈
- Chat list scroll: 只允许列表 viewport 滚动，顶部、状态反馈、底部导航保持固定
- Message composer: send 后立即清空输入，但保留失败恢复能力
- Message thread scroll: 只允许消息区滚动，composer 和顶部栏保持固定
- Snapshot restore: 恢复到列表或详情时优先使用内联 banner，而不是阻塞弹窗
- Global search: 搜索页只允许结果 viewport 滚动，保持顶部搜索框和返回结构稳定
- Settings scroll: 设置页只允许内容 viewport 滚动，顶部与底部导航固定
- Media picker: 作为底部覆盖层出现，系统返回优先关闭 picker，再返回上一页
- Top actions: 顶部编辑、写消息、添加等入口优先使用轻量按压反馈，不做重按钮态
- Back navigation: 遵循平台默认手势，不自造交互
- Bottom tabs: 当前 tab 高亮切换要平滑，但不能做过重的缩放和弹跳

## Accessibility

- 触控目标不小于平台建议最小尺寸
- 动效需要兼容 reduced motion
- 错误提示不能只靠颜色表达
