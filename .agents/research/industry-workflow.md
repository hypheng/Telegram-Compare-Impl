# 跨平台应用 AI 开发流程调研

日期: 2026-03-14

## 1. 官方信息摘录

### KMP / Compose Multiplatform

- Kotlin Multiplatform 官方定位是“共享代码，同时保留原生编程的灵活性”，适合先共享业务逻辑，再决定 UI 共享边界。
  - 来源: [Kotlin Multiplatform overview](https://kotlinlang.org/docs/multiplatform.html)
- JetBrains 对 Compose Multiplatform 的说明是可把 Jetpack Compose 的声明式 UI 模型扩展到 iOS、Desktop、Web 等平台。
  - 来源: [Compose Multiplatform and Jetpack Compose](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-and-jetpack-compose.html)

### 仓颉 / CJMP

- Context7 的 `/hypheng/cjmp-ai-docs` 已经把 CJMP 开发文档汇总到一个 GitHub 仓，适合作为 Agent 的首选 CJMP 文档入口。
  - 来源: https://context7.com/hypheng/cjmp-ai-docs
- 该仓库顶层 `README` 会把文档分到 `zh-cn` 和 `cangjie_tools` 两大入口，后者覆盖 `cjpm`、`cjfmt`、`cjlint`、`cjdb`、`cjcov`、`cjprof`、`cjtrace` 等工具文档。
  - 来源: `/hypheng/cjmp-ai-docs` Context7 query
- `cjpm init` 会生成 `src/` 和默认 `cjpm.toml`，这是 Agent 初始化 CJMP 项目时的首选起点。
  - 来源: `/hypheng/cjmp-ai-docs` Context7 query
- `cjpm build`、`cjpm run`、`cjpm test` 和 `cjc -v` 的 `Target` 输出，是 Agent 组织项目、构建、测试和 target 识别的基础。
  - 来源: `/hypheng/cjmp-ai-docs` Context7 query
- 仓颉 1.0.0 官方安装指南显示工具链覆盖 macOS、Linux、Windows。
  - 来源: [仓颉安装指南 1.0.0](https://docs.cangjie-lang.cn/docs/1.0.0/user_manual/source_zh_cn/first_understanding/installation_guide.html)
- 仓颉 VS Code 插件已提供语言基础能力，说明语言层工具链可先独立落地。
  - 来源: [仓颉插件使用指南 1.0.0](https://docs.cangjie-lang.cn/docs/1.0.0/user_manual/source_zh_cn/tooling/manual_cangjie_plugin.html)
- HarmonyOS NEXT 应用开发仍处于公开测试阶段，流程依赖 DevEco Studio 与仓颉插件，因此 App 侧成熟度和可得性要单独评估。
  - 来源: [HarmonyOS 应用开发入门指导](https://docs.cangjie-lang.cn/docs/0.53.13/guide/source_zh_cn/%E4%BB%93%E9%A2%95%E9%B8%BF%E8%92%99%E5%BA%94%E7%94%A8%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8%E6%8C%87%E5%8D%97.html)

### MCP / OpenAI / Codex

- MCP 官方架构是 host-client-server；server 可以暴露 tools、resources、prompts。
  - 来源: [MCP Architecture](https://modelcontextprotocol.io/docs/learn/architecture)
  - 来源: [MCP Server Concepts](https://modelcontextprotocol.io/docs/learn/server-concepts)
- OpenAI 官方建议在 agentic 工作流中使用 Responses API 的内建 tools、background mode、remote MCP。
  - 来源: [Tools guide](https://platform.openai.com/docs/guides/tools)
  - 来源: [Background mode guide](https://platform.openai.com/docs/guides/background)
  - 来源: [MCP guide](https://platform.openai.com/docs/guides/mcp)
- Codex 官方文档把 `AGENTS.md`、skills、MCP 配置当作首选的项目级控制面。
  - 来源: [Codex docs index](https://developers.openai.com/codex/)
- Context7 官方推荐对 AI 编码助手使用远程 HTTP MCP，避免本地 Node 依赖。
  - 来源: [Context7 Installation](https://context7.com/docs/installation)

## 2. 对本仓库的推导

### 先做 framework-agnostic spec

因为当前要比较的是 KMP 和 CJMP，而不是先赌某一个框架，所以需求、流程、领域和评价口径必须先独立定义。

### 再做 agent control plane

Agent 想稳定工作，不能只靠一个 `AGENTS.md`。还需要:

- project skills
- MCP sources
- prompt templates
- environment checks
- missing infra TODO

### 再做双框架实现

并且最好 Android-first，因为:

- Android 工具链可观察性通常更高
- KMP 和 CJMP 都能更早暴露 UI、状态和构建差异
