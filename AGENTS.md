# Project Agent Rules

## Mission

本仓库用于对比 `KMP` 与 `CJMP` 构建 Telegram 类商用应用时的:

- 工程表达能力
- 跨平台能力
- AI 辅助开发效率
- AI 生成结果的可维护性与可验证性

## Mandatory Workflow

1. 先改 `framework-agnostic-spec/`，再改 `apps/`
   - 新功能、新页面、新领域对象，先落到框架无关文档。
   - 涉及界面时，先补 `framework-agnostic-spec/interface-design/`。
2. 只做纵切片，不做大爆炸开发
   - 每次只推进一个可验收的用户价值切片。
3. KMP 与 CJMP 必须对齐同一份产品切片
   - 如果只实现了一边，必须在 `framework-agnostic-assets/evaluation/parity-matrix.md` 记录原因。
4. AI 基础设施和业务代码分开维护
   - `.agents/` 只放 Agent 控制面，不放业务实现。

## Directory Contract

- `framework-agnostic-spec/`: 框架无关需求、交互、领域、对比标准
- `framework-agnostic-assets/`: 共享契约、测试夹具、对比证据
- `apps/kmp/`: KMP 方案实现与记录
- `apps/cjmp/`: CJMP 方案实现与记录
- `.agents/`: Agent 控制面

## Done Criteria For Each Slice

一个功能切片只有在以下条件同时满足时才算完成:

1. `framework-agnostic-spec/` 中存在明确的验收描述。
2. `framework-agnostic-spec/interface-design/` 中存在对应 UI/interaction 定义。
3. `framework-agnostic-assets/evaluation/parity-matrix.md` 已登记状态。
4. 双框架至少都有实现计划，或已明确阻塞项。
5. AI 交付观察已沉淀到对比矩阵或 TODO。

## Preferred Skills

- 做框架无关设计时，使用 `$telegram-compare-product-design`
- 做 UI / interaction 设计时，使用 `$telegram-compare-ui-design`
- 做双实现对齐时，使用 `$telegram-compare-parity-delivery`
- 做 MCP、提示词、技能、环境脚本时，使用 `$telegram-compare-ai-infra`
- 做 KMP / Android 实现时，使用 `$telegram-compare-kmp-delivery`
- 做 CJMP 实现时，使用 `$telegram-compare-cjmp-delivery`
