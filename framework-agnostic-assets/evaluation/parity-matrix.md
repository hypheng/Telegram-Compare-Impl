# Parity Matrix

| Slice | Product Source | Acceptance | KMP | CJMP | AI Log | Issues | Evidence | Notes |
|---|---|---|---|---|---|---|---|---|
| S1 登录与会话恢复 | `framework-agnostic-spec/requirements/s1-login-session-restore.md` | in-progress | in-progress | planned | `2026-03-15-S1-kmp-login-session-restore.md` | none | `S1-login-and-session-restore.md`, `:shared-domain:allTests`, `:shared-data:allTests`, `:androidApp:assembleDebug` | KMP 已启动真实切片；主壳落点使用 `Chat List Entry Shell`。当前缺口是补回 Figma handoff，再进入设备验收和 CJMP 对齐 |
| S2 会话列表 | `framework-agnostic-spec/requirements/telegram-mvp.md` | pending | planned | planned | pending | none | repo scaffold only | 现在要求先完成 `D1/D2`，即切片级 UI 设计和 Figma handoff，再开始实现 |
| S3 单聊详情与文本发送 | `framework-agnostic-spec/requirements/telegram-mvp.md` | pending | planned | planned | pending | none | repo scaffold only | 作为首个核心对比切片 |
| S4 本地缓存与离线恢复 | `framework-agnostic-spec/requirements/telegram-mvp.md` | pending | planned | planned | pending | none | repo scaffold only | 适合验证状态和存储能力 |
| S8 AI 助手增强能力 | `framework-agnostic-spec/requirements/telegram-mvp.md` | pending | backlog | backlog | pending | none | not started | 先等基础聊天闭环 |
