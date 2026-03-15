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

## 手势与反馈

- Chat list row: tap 进入，swipe 作为次级操作预留
- Chat list search: 默认固定在标题区下方，优先做 Telegram 类似的顺滑进入与聚焦反馈
- Chat list scroll: 只允许列表 viewport 滚动，顶部、状态反馈、底部导航保持固定
- Message composer: send 后立即清空输入，但保留失败恢复能力
- Message thread scroll: 只允许消息区滚动，composer 和顶部栏保持固定
- Top actions: 顶部编辑、写消息、添加等入口优先使用轻量按压反馈，不做重按钮态
- Back navigation: 遵循平台默认手势，不自造交互
- Bottom tabs: 当前 tab 高亮切换要平滑，但不能做过重的缩放和弹跳

## Accessibility

- 触控目标不小于平台建议最小尺寸
- 动效需要兼容 reduced motion
- 错误提示不能只靠颜色表达
