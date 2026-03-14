---
name: telegram-compare-ai-infra
description: Use when setting up or maintaining the AI engineering layer for this project, including AGENTS.md, project skills, MCP services, prompts, environment checks, setup scripts, or TODO tracking for missing AI infrastructure.
---

# Telegram Compare Ai Infra

用于维护项目级 AI 工作流，而不是业务功能本身。

## Use This Skill When

- 修改 `.agents/`
- 修改 `AGENTS.md`
- 修改 `.agents/skills/`
- 配置或检查 MCP 服务
- 更新提示词模板、setup 脚本或缺口清单

## Workflow

1. 先确认变更属于 repo-local 还是 global Codex setup。
2. Repo-local 内容直接进仓库:
   - prompts
   - skills
   - docs
   - env checks
3. Global setup 只生成脚本、模板或显式执行命令:
   - `~/.codex/config.toml`
   - `~/.codex/skills`
4. 任何缺口都登记到 `.agents/todos/ai-gap-list.md`。

## Rules

- 优先使用官方文档和官方 MCP
- 尽量保持脚本可 dry-run
- 不要把“需要用户环境权限”的动作伪装成已完成
- MCP 和 skills 的选择要服务于当前项目，不做无关扩张

## References

- 运维规则: `references/ops.md`
