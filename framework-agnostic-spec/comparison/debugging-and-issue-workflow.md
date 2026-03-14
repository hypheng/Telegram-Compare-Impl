# Debugging And Issue Workflow

## 目标

把调试问题从“即时修错”提升成“可比较的研究样本”。

这个项目里的调试不只是为了修好功能，还要回答:

- 问题更常出在 `common`、`KMP` 还是 `CJMP`
- 问题来自需求、设计、实现、工具链还是 AI 本身
- AI 是否能独立定位并修复

## 调试步骤

1. 先归类问题层级
   - `spec`
   - `ui`
   - `domain`
   - `build`
   - `runtime`
   - `test`
   - `ai-workflow`
2. 固化复现条件
   - 切片 ID
   - 平台
   - 分支或提交
   - 输入数据
   - 触发步骤
3. 收集证据
   - 错误日志
   - 构建输出
   - 截图
   - 最小复现命令
4. 判断归因范围
   - `common`
   - `kmp`
   - `cjmp`
5. 决定处理方式
   - 当场修复
   - 记录为待办
   - 升级为 GitHub Issue
6. 回写记录
   - AI delivery log
   - acceptance report
   - parity matrix

## 调试时必须回答的问题

- 这是规格不清，还是实现错误
- 这是框架限制，还是工具链问题
- AI 是否在第一次尝试就定位到根因
- AI 是否出现了无关改动、错误假设或 API 幻觉
- 修复过程是否需要人类补充关键上下文

## 什么时候只记日志，不建 issue

满足以下全部条件时，可以只写日志:

- 影响范围仅限当前一次任务
- AI 或开发者已在当次修复
- 不需要仓库级改动
- 未来统计时只需从 run log 回看即可

## 什么时候必须建 issue

满足任一条件时，建 GitHub Issue:

- 问题会重复出现
- 阻塞超过 30 分钟
- 需要多次人工介入
- 需要新增仓库级规范、脚本或模板
- 需要上游文档、SDK 或工具链支持
- 需要单独比较 `KMP` 与 `CJMP` 的 AI 开发差异

## Issue 标签策略

至少保留 3 个主标签:

- `ai/common`
- `ai/kmp`
- `ai/cjmp`

建议叠加第二层标签:

- `type/spec`
- `type/ui`
- `type/debug`
- `type/tooling`
- `type/docs`
- `type/test`

建议叠加第三层标签:

- `impact/blocker`
- `impact/high`
- `impact/medium`
- `impact/low`

详细约定见 `framework-agnostic-assets/evaluation/issue-taxonomy.md`。

## 与 GitHub Issue 的分工

### 优先放在仓库里的内容

- 每次执行日志
- 切片验收结果
- 具体证据链接

### 优先放在 GitHub Issue 的内容

- 可复现问题
- 跨切片阻塞
- 需要累计统计的问题模式
- 需要后续排期处理的 AI 能力缺口

结论:

GitHub Issue 适合记录“问题”，不适合替代“每次 AI 执行日志”。
最佳实践是两者同时存在，并通过链接互相关联。
