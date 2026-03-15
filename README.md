# Telegram Compare Impl

这个仓库用于并行落地两个 Telegram App 版本:

1. `KMP` 版本
2. `CJMP` 版本

目标不是先拼框架代码，而是先用框架无关的方式完成产品、交互、领域和验收设计，再用同一组产品切片驱动双实现，对比:

- 框架本身的工程能力
- AI 辅助生成商用应用的可行性
- AI 在需求到交付链路上的稳定性、效率和可验证性

## 当前阶段

当前仓库已经完成第一版项目脚手架:

- `framework-agnostic-spec/`：框架无关的应用规格
- `framework-agnostic-assets/`：双实现共享的契约、夹具、对比证据
- `apps/kmp/`：KMP 版实现入口
- `apps/cjmp/`：CJMP 版实现入口
- `.agents/`：Agent 控制面，包括 skills、MCP、提示词、setup 脚本和 TODO

## 建议工作流

1. 先在 `framework-agnostic-spec/` 定义功能切片、核心流程、领域模型和对比口径。
2. 在 `framework-agnostic-assets/evaluation/acceptance-reports/` 产出切片验收记录。
3. 在 `framework-agnostic-assets/evaluation/ai-delivery-logs/` 记录每次 AI 执行过程、耗时、token 和阻塞。
4. 在 `framework-agnostic-assets/evaluation/parity-matrix.md` 记录每个切片的双端状态、验收链接和 issue 链接。
5. 对可复现、跨切片或超过单次任务范围的问题，进入 GitHub Issue。
6. 再分别推进 `apps/kmp/` 和 `apps/cjmp/` 的实现。
7. 每次实现都回写证据、阻塞项和 AI 交付观察。

## 快速开始

```bash
./scripts/check-dev-env.sh
./scripts/verify-layout.sh
bash ./.agents/setup/check-codex-ai-infra.sh
bash ./scripts/kmp-doctor.sh
bash ./.agents/setup/bootstrap-codex-mcp.sh
bash ./.agents/setup/bootstrap-figma-mcp.sh remote
bash ./.agents/setup/install-curated-skills.sh
bash ./.agents/setup/check-android-env.sh
```

说明:

- 上面两个 setup 脚本默认是 dry-run。
- `bootstrap-figma-mcp.sh remote` 会为 Codex 准备 Figma remote MCP；首次真实接入需要 `--apply` 并完成 Figma OAuth。
- `check-codex-ai-infra.sh` 会同时检查 repo-local 控制面、必需 MCP 和推荐全局 skills。
- 需要真实写入 `~/.codex/` 时，请追加 `--apply`。
- 当前本机工具链检查结果见 `.agents/todos/ai-gap-list.md` 和 `./scripts/check-dev-env.sh`。
- KMP 轨道的 repo-level 自检入口是 `bash ./scripts/kmp-doctor.sh`。

## 结构解释

- `framework-agnostic-spec/`
  - 这里是“先不管 KMP/CJMP，用统一语言把应用定义清楚”的地方。
  - 包含范围、流程、领域、UI/interaction 设计和评价口径。
- `framework-agnostic-assets/`
  - 这里不是共享源码，而是共享契约、夹具和对比证据。
  - 其中 `evaluation/` 现在也承载切片验收报告、AI 执行日志和 issue 分类规范。
- `.agents/`
  - 这里是 Agent 控制面。
  - `.agents/skills/` 只是其中一个子目录，负责项目级 skills 自动发现。
  - `.agents/mcp/`、`.agents/prompts/`、`.agents/setup/`、`.agents/todos/` 是这些 skills 和 Agent 运行时要用到的支撑资产。

## UI Design Workflow

移动应用的 UI/interaction 不应直接从文字需求跳到实现。这个仓库现在把 UI 设计作为独立层处理:

1. 在 `framework-agnostic-spec/interface-design/` 定义 screen inventory、交互状态、视觉系统和交付流程。
2. 在 `framework-agnostic-assets/design-evidence/` 保存设计评审证据、截图和 handoff 记录。
3. 在 `.agents/skills/telegram-compare-ui-design/` 里约束 Agent 先做 UI 设计，再做实现。
4. 如果使用 Figma，则通过 `.agents/mcp/figma.mcp.toml.example` 和推荐的 Figma skills 接入设计上下文。

没有 Figma 设计稿时也可以推进:

1. 先在 `framework-agnostic-spec/interface-design/` 写 screen inventory、状态矩阵和低保真结构。
2. 在 `framework-agnostic-assets/design-evidence/` 留截图、草图或 ready-for-dev 清单。
3. 当页面结构稳定后，再在 Figma 补视觉稿和原型，并通过 Figma MCP 把 frame / node 链接带给 Agent。
