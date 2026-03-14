# ADR 0001: 先做框架无关设计，再做双实现

## Status

accepted

## Context

当前目标不是尽快产出某一端代码，而是客观比较 `KMP` 与 `CJMP`:

- 框架能力
- 工具链成熟度
- AI 辅助开发的真实效率

如果直接开始写某一端源码，会出现:

- 产品切片定义不一致
- 双框架无法公平比较
- AI 产出难以复用和审计

## Decision

采用如下仓库拓扑:

- `framework-agnostic-spec/` 先沉淀框架无关设计
- `framework-agnostic-assets/` 保存共享契约和对比证据
- `apps/kmp/`、`apps/cjmp/` 分别承载实现
- `.agents/` 单独管理 Agent 控制面

## Consequences

好处:

- 对比口径稳定
- AI 工作流可复用
- 框架差异更容易观察

代价:

- 首轮不会立刻得到可运行 App
- 需要先建设文档、脚本和流程
