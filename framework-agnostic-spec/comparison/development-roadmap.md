# Development Roadmap

## 当前策略

按 `S1 -> S2 -> S3 -> S4` 的顺序推进主路径闭环。`S5-S8` 保持 backlog，等基础聊天闭环稳定后再展开。

当前活动切片是 `S1 登录与会话恢复`。本轮允许先启动 KMP 实现，但必须把 CJMP 的对应计划和阻塞记录在 parity matrix 中。

## 分阶段路线

| Phase | Slice | 目标 | Spec 输入 | KMP 任务 | CJMP 任务 | 验证与证据 | 主要风险 |
|---|---|---|---|---|---|---|---|
| P0 | AI infra baseline | 保证 Codex、MCP、skills、自检脚本可复用 | `AGENTS.md`、`.agents/`、`README.md` | 补 repo-local 自检，跑 KMP doctor | 仅做环境检查，不做实现 | `check-codex-ai-infra.sh`、`kmp-doctor.sh`、AI gap list | 把全局依赖误报为已完成 |
| P1 | `S1` 登录与会话恢复 | 打通首次进入、恢复、失效回退、登出 | `requirements/s1-login-session-restore.md`、`interface-design/s1-login-session-restore.md` | shared-domain 状态与用例、shared-data 会话存储、Android 主壳页面 | 对齐同一份状态机，确认 CJMP 存储与启动壳落点 | acceptance report、AI log、shared tests、`androidApp:assembleDebug` | 把 `S2` 范围混入 `S1`，导致验收口径模糊 |
| P2 | `S2` 会话列表 | 建立 Telegram 风格主列表、刷新和空态 | `telegram-mvp.md`、screen inventory、visual system | 复用 `S1` 入口，补 chat list state、mock refresh、row rendering | 对齐列表信息层级和刷新反馈 | acceptance report、UI evidence、列表状态测试 | UI 密度和 Telegram 风格偏差大 |
| P3 | `S3` 单聊详情与文本发送 | 建立消息详情、composer、发送状态 | `ux/core-flows.md`、interaction states、domain map | 消息领域、发送状态机、详情页 Android 壳 | 对齐 composer、失败重试、滚动行为 | acceptance report、shared tests、debug log | 共享层与 Android 壳边界反复重构 |
| P4 | `S4` 本地缓存与离线恢复 | 验证本地恢复、同步快照和重启可用性 | `domain-map.md`、comparison docs | 引入 storage adapter、snapshot 恢复、缓存测试 | 明确 CJMP 本地持久化方案和阻塞 | acceptance report、重启验证记录、issue logs | 双框架存储能力差异导致 parity 失衡 |

## S1 当前执行清单

| Layer | Deliverable | 退出条件 |
|---|---|---|
| framework-agnostic spec | S1 需求、UI handoff、roadmap、parity 更新 | 文档能直接指导实现，不再依赖口头解释 |
| `shared-domain` | `UserSession`、`SessionRestoreResult`、`LoginResult`、use cases | 共享测试覆盖成功、无平台专有状态 |
| `shared-data` | mock `SessionRepository`、会话持久化边界、测试桩 | 可验证 restore / login / logout / expired-session |
| `androidApp` | 启动恢复页、登录页、主壳入口页 | 能演示 AC-S1-1 到 AC-S1-6 的主要路径 |
| evaluation assets | acceptance report、AI log、parity matrix | 能追溯本轮 AI 操作和剩余风险 |

## 执行原则

- 每一轮先完成一个切片的 acceptance 证据，再开下一轮。
- 如果只实现 KMP，一定要在 parity matrix 写明 CJMP 仍处于 `planned` 或 `blocked` 的原因。
- 任何新增页面或状态都先补 `framework-agnostic-spec/interface-design/`，再改 `apps/`。
- 需要真实外部依赖时，先记录缺口，不伪造“已接入 Telegram”。
