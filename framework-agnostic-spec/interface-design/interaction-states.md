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

## 手势与反馈

- Chat list row: tap 进入，swipe 作为次级操作预留
- Chat list search: 默认固定在标题区下方，优先做 Telegram 类似的顺滑进入与聚焦反馈
- Message composer: send 后立即清空输入，但保留失败恢复能力
- Top actions: 顶部编辑、写消息、添加等入口优先使用轻量按压反馈，不做重按钮态
- Back navigation: 遵循平台默认手势，不自造交互
- Bottom tabs: 当前 tab 高亮切换要平滑，但不能做过重的缩放和弹跳

## Accessibility

- 触控目标不小于平台建议最小尺寸
- 动效需要兼容 reduced motion
- 错误提示不能只靠颜色表达
