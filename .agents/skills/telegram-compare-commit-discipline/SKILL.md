---
name: telegram-compare-commit-discipline
description: Use when preparing a commit, writing a commit message, or reviewing whether a change is ready to commit for this Telegram comparison project. Enforces detailed, professional commits that explain scope, rationale, verification, and remaining risks.
---

# Telegram Compare Commit Discipline

用于保证本仓库的每个 commit 都是可读、可审查、可追溯的专业提交，而不是模糊的“update files”。

## Use This Skill When

- 用户要求你提交代码
- 用户要求你总结变更并生成 commit message
- 你判断当前工作已经形成一个可提交的纵切片
- 你需要检查某批改动是否应该拆成多个 commit

## Workflow

1. 先看 `git status --short` 和 `git diff --stat`，确认提交范围。
2. 判断这批改动是否是单一主题:
   - 一个切片
   - 一个基础设施动作
   - 一个设计交付动作
   - 一个调试修复动作
3. 如果范围过大，先建议拆 commit；如果用户明确要求一次提交，则在正文里把范围说清。
4. 写 commit message 时必须覆盖:
   - 做了什么
   - 为什么做
   - 影响了哪些层
   - 如何验证
   - 还有什么没做完
5. 如果改动涉及切片，优先引用:
   - spec
   - design / handoff
   - acceptance / parity
6. 提交后，再用 `git status --short` 确认工作树干净。

## Message Standard

### Title

- 使用祈使句
- 直接描述变更主题
- 避免空泛标题，例如:
  - `update`
  - `fix stuff`
  - `misc changes`

### Body

正文默认应该是多段落的详细说明，至少回答:

- 这次提交推进了哪个切片或哪个仓库级能力
- 规格 / 设计 / 实现 / 证据分别改了什么
- 为什么这样组织，而不是别的方式
- 实际跑了哪些验证命令
- 剩余风险或下一步是什么

## Preferred Body Structure

按需要组合，但通常至少包含:

1. 范围和目标
2. 关键改动
3. 验证命令
4. 未完成项或风险

## Rules

- 不要提交“顺手混进去”的无关改动
- 不要把多个不相关主题塞进一个 commit message 里假装合理
- 不要写只有标题、没有正文的空提交说明，除非用户明确要求
- 涉及验证时，只写实际执行过的命令，不伪造验证
- 如果有 backfill、特例或已知偏差，必须写在正文里

## Project-specific Expectations

- 本仓库的 commit 应该能看出它影响的是:
  - `framework-agnostic-spec`
  - `framework-agnostic-assets`
  - `apps/kmp`
  - `apps/cjmp`
  - `.agents/`
- 涉及切片时，正文应尽量说明 slice ID，例如 `S1`、`S2`
- 涉及 parity 时，正文应说明另一侧是否仍是 `planned`、`blocked` 或已对齐
