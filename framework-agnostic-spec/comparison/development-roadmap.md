# Development Roadmap

## 当前策略

项目现在采用 `spec -> design -> figma handoff -> implementation -> evidence` 的顺序，而不是直接从需求跳到代码。

- `S1 -> S2 -> S3 -> S4` 仍然是主路径切片顺序。
- `S5-S8` 保持 backlog，等基础聊天闭环稳定后再展开。
- `framework-agnostic-spec/interface-design/` 仍然是仓库内第一真相源。
- Figma 现在被提升为显式交付阶段，用于视觉稿、prototype、ready-for-dev frame/node 和 MCP 上下文。

当前项目状态已经进入:

- `S1-S3` 的 KMP 主用户工作流已完成代码、构建和设备验收
- `S1-S3` 的 Figma handoff 仍因缺少可编辑文件而未闭环
- `S2-S3` 的 CJMP 仍按当前范围保持 deferred
- 下一条候选切片是 `S4 本地缓存与离线恢复`

## 分阶段路线

| Phase | Focus | 目标 | 主要产物 | 退出条件 | 主要风险 |
|---|---|---|---|---|---|
| P0 | AI infra baseline | 保证 Codex、MCP、skills、自检脚本可复用 | `AGENTS.md`、`.agents/`、setup scripts、自检结果 | repo-local 和 global setup 可复用 | 把全局依赖误报为已完成 |
| D1 | Framework-agnostic design | 先把切片的页面、状态、信息层级和低保真结构定义清楚 | requirements、screen inventory、interaction states、slice UI docs | 不依赖口头解释即可指导设计和实现 | 需求未清时直接跳代码 |
| D2 | Figma design and handoff | 形成视觉稿、prototype、ready-for-dev frame/node | Figma file、frame/node links、prototype、handoff notes | 关键状态在 Figma 中可 inspect，可回写 design evidence | 把 MCP 配置误当成设计交付本身 |
| I1 | KMP implementation | 按已完成 handoff 的切片推进 KMP | shared-domain、shared-data、androidApp、tests | 构建通过，切片证据完整 | 共享层与 UI 壳边界反复重构 |
| I2 | CJMP implementation | 按同一份 spec/handoff 推进 CJMP | CJMP app shell、domain/data 对齐、tests | parity matrix 有可比较证据 | 一侧领先过多导致对比失真 |
| E1 | Acceptance and parity | 固化验收、AI log、对比结论 | acceptance report、AI log、parity matrix、issues | 切片可关闭或明确阻塞 | 证据缺失导致结果不可比 |

## 切片顺序与门槛

| Slice | 当前阶段 | Design 门槛 | Implementation 门槛 | 当前备注 |
|---|---|---|---|---|
| `S1` 登录与会话恢复 | `E1` 已完成，`D2` blocked，`I2` deferred | 已有 repo 内 UI 定义和 backfill brief，但缺真实 Figma file / frame links | KMP 已验收；CJMP 当前不作为闭环要求 | 这是一个 backfill 特例，不应成为后续切片默认方式 |
| `S2` 会话列表 | `E1` 已完成，`D2` blocked，`I2` deferred | 已有 repo 内 UI 定义和 handoff brief，但缺真实 Figma file / frame links | KMP 已验收；CJMP 当前不作为闭环要求 | 设备侧已完成 default / search / refresh / empty / error 验收 |
| `S3` 单聊详情与文本发送 | `E1` 已完成，`D2` blocked，`I2` deferred | 已有 repo 内 UI 定义和 handoff brief，但缺真实 Figma file / frame links | KMP 已验收；CJMP 当前不作为闭环要求 | KMP 主用户工作流现已从登录贯通到详情发送 |
| `S4` 本地缓存与离线恢复 | backlog | 依赖前序切片 UI 和状态稳定 | 依赖 `S1-S3` 的领域与存储边界 | 更适合在主路径闭环后推进 |

## S1 当前执行清单

| Layer | Deliverable | 退出条件 |
|---|---|---|
| framework-agnostic spec | S1 需求、UI 定义、roadmap、parity 更新 | 文档能直接指导实现，不再依赖口头解释 |
| Figma handoff | S1 关键 frame、状态 frame、prototype、ready-for-dev 标注 | 当前 blocked: 仍缺可编辑 Figma 文件、frame/node links 和 handoff payload |
| `shared-domain` | `UserSession`、`SessionRestoreResult`、`LoginResult`、use cases | 共享测试覆盖成功、无平台专有状态 |
| `shared-data` | mock `SessionRepository`、会话持久化边界、测试桩 | 可验证 restore / login / logout / expired-session |
| `androidApp` | 启动恢复页、登录页、主壳入口页 | 已完成 AC-S1-1 到 AC-S1-6 的模拟器验收 |
| evaluation assets | acceptance report、AI log、parity matrix | 已有 acceptance evidence、AI log 和 parity 记录 |

## 执行原则

- 每一轮先完成一个切片的 `D1/D2/I1(or I2)/E1` 证据，再开下一轮。
- 如果只实现 KMP，一定要在 parity matrix 写明 CJMP 仍处于 `planned` 或 `blocked` 的原因。
- 任何新增页面或状态都先补 `framework-agnostic-spec/interface-design/`，再改 `apps/`。
- 从 `S2` 开始，默认要求先有 Figma handoff，再开始实现。
- 需要真实外部依赖时，先记录缺口，不伪造“已接入 Telegram”。
