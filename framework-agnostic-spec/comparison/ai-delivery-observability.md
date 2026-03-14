# AI Delivery Observability

## 目标

把 AI 从“对话过程”变成“可审计的交付过程”。

本项目不仅要比较 `KMP` 和 `CJMP` 的工程能力，也要比较 AI 在两条技术路线上的执行效率、稳定性和问题分布。因此每次有实质产出的 AI 执行，都要留下结构化记录。

## 记录层次

### 1. 每次执行记录

存放在 `framework-agnostic-assets/evaluation/ai-delivery-logs/`。

适用场景:

- 新增或修改一个切片的 spec
- 做 UI / interaction 设计
- 推进 KMP / CJMP 实现
- 调试构建、运行、测试或环境问题
- 做 parity review

### 2. 每个切片汇总

至少回写到:

- `framework-agnostic-assets/evaluation/acceptance-reports/`
- `framework-agnostic-assets/evaluation/parity-matrix.md`

### 3. 跨切片问题管理

对可复现、持续存在或影响多个任务的问题，进入 GitHub Issue。

## 每次执行必须记录什么

### 基本上下文

- `slice id`
- `scope`: `common` / `kmp` / `cjmp`
- `task type`: `spec` / `ui` / `impl` / `debug` / `review`
- 执行目标
- 输入材料链接
- 输出文件链接

### 时间成本

- 开始时间
- 结束时间
- 总历时
- 估算的主动工作时间
- 等待时间
- 人工介入次数

说明:

- 时间统一用绝对时间，避免“今天”“刚才”一类相对表述。
- 如果一次任务被拆成多段，应分别记录，不要合并成模糊总结。

### token 成本

优先记录:

- input tokens
- output tokens
- total tokens
- cache / reasoning 等工具可见的补充项

如果当前宿主工具拿不到精确 token:

- 明确写 `unknown`
- 记录原因
- 写明数据来源，例如“Codex UI 手工摘录”或“API usage 返回值”

不要伪造估算值。

### 执行过程

至少记录:

- 关键提示轮次
- 关键工具调用
- 关键决策点
- 是否发生返工
- 是否需要重新收集上下文

### 结果与阻塞

- 本次完成了什么
- 哪些验收项被推进
- 哪些问题仍阻塞
- 问题属于 `common` / `kmp` / `cjmp` 哪一类
- 是否已创建 GitHub Issue

## 推荐问题分类

- `requirements-ambiguity`
- `ui-ambiguity`
- `docs-gap`
- `tooling-gap`
- `build-failure`
- `runtime-failure`
- `test-instability`
- `context-drift`
- `hallucinated-api`
- `self-repair-failed`
- `external-dependency`
- `needs-human-decision`

## 进入 GitHub Issue 的条件

满足任一条件就应建 issue:

- 同类问题重复出现至少 2 次
- 单次阻塞超过 30 分钟
- 影响多个切片
- 需要仓库级或团队级决策
- 需要后续统计 AI 能力缺陷
- 明显只影响 `KMP` 或 `CJMP` 一条技术路线，且值得单独跟踪

## 命名约定

### AI delivery log

推荐文件名:

`YYYY-MM-DD-<slice>-<scope>-<task>.md`

例如:

- `2026-03-14-S3-common-spec.md`
- `2026-03-18-S3-kmp-debug.md`

### Acceptance report

推荐文件名:

`<slice>-acceptance.md`

## 最小闭环

一个切片如果进入真实执行，至少应形成以下链路:

1. `framework-agnostic-spec/` 中有需求与设计
2. `framework-agnostic-assets/evaluation/acceptance-reports/` 中有验收记录
3. `framework-agnostic-assets/evaluation/ai-delivery-logs/` 中有执行记录
4. `framework-agnostic-assets/evaluation/parity-matrix.md` 中能找到最新状态
5. 关键阻塞能追到 GitHub Issue
