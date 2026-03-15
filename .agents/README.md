# Agent Control Plane

`.agents/` 是本仓库的 agent control plane。

它回答两个问题:

1. Agent 做事时该读什么、遵守什么、调用什么
2. 哪些配置属于项目内，哪些属于用户全局 Codex 环境

## 目录

```text
.agents/
├── skills/     # Codex 项目技能，自动发现入口
├── research/   # 官方资料调研与工作流推导
├── mcp/        # MCP 配置模板
├── prompts/    # prompt 模板
├── setup/      # environment checks 和 bootstrap
└── todos/      # 当前缺失能力
```

## `.agents/skills/` 和其余目录的关系

- `.agents/skills/`
  - 运行时入口
  - 负责告诉 Agent“什么时候用哪个技能”
- `.agents/research/`
  - 这些技能背后的官方依据
  - 包括通用开发流程和 UI design workflow
- `.agents/mcp/`
  - 规定要接哪些外部知识源
- `.agents/prompts/`
  - 提供稳定的 prompt 模板
- `.agents/setup/`
  - 提供环境自检和全局 setup 脚本
- `.agents/todos/`
  - 记录当前缺什么，避免 Agent 假设环境完备

## Android / KMP / CJMP 支撑

当前仓库已经具备:

- KMP 交付技能
- CJMP 交付技能
- UI / interaction 设计技能
- Codex AI infra 自检入口
- Android 环境检查脚本
- KMP / CJMP 环境检查脚本

这意味着 Agent 在开始真实实现前，能先判断“是需求没定义清楚，还是工具链根本没装好”。

## 常用入口

- `bash ./.agents/setup/check-codex-ai-infra.sh`
- `bash ./.agents/setup/bootstrap-codex-mcp.sh`
- `bash ./.agents/setup/install-curated-skills.sh`
- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./.agents/setup/check-cjmp-env.sh`
