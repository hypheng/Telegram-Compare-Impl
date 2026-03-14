# Issue Taxonomy

## 目标

为 GitHub Issue 提供稳定标签体系，使问题能够按 AI 能力、框架归属和影响范围做统计。

## 结论

GitHub Issue 适合记录“需要持续跟踪的问题”。

它不适合替代:

- 每次 AI 执行日志
- 每个切片的验收记录

推荐分工:

- run log 在仓库内
- acceptance report 在仓库内
- issue 在 GitHub

## 一级标签: 问题归属

至少保留以下 3 个标签:

- `ai/common`
- `ai/kmp`
- `ai/cjmp`

说明:

- `ai/common`: 与产品定义、通用 AI 工作流、通用工具、跨框架共享规范有关
- `ai/kmp`: 只影响 KMP 路线
- `ai/cjmp`: 只影响 CJMP 路线

## 二级标签: 问题类型

- `type/spec`
- `type/ui`
- `type/domain`
- `type/debug`
- `type/tooling`
- `type/docs`
- `type/test`
- `type/perf`

## 三级标签: 影响级别

- `impact/blocker`
- `impact/high`
- `impact/medium`
- `impact/low`

## 四级标签: 问题状态

- `status/needs-repro`
- `status/needs-decision`
- `status/needs-doc`
- `status/needs-upstream`
- `status/ready`

## 建 issue 的最低字段

- 影响的切片 ID
- 归属范围: `common` / `kmp` / `cjmp`
- 问题类型
- 复现步骤
- 预期结果
- 实际结果
- 已尝试的 AI / 人工修复动作
- 对耗时、token 或交付质量的影响
- 关联的 AI delivery log

## 统计建议

后续复盘时，至少按下面几个维度看 issue:

- 每个切片新引入了多少 issue
- `ai/common` / `ai/kmp` / `ai/cjmp` 分布
- 哪类问题最耗时
- 哪类问题最耗 token
- 哪类问题最依赖人工介入
