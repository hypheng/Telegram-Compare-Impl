# CJMP Doc Entrypoints

首选文档源:

- Context7 library: `/hypheng/cjmp-ai-docs`

Agent 查询顺序建议:

1. 顶层 `README`
   - 用来判断应该去 `zh-cn` 还是 `cangjie_tools`
2. `zh-cn`
   - 看 CJMP 应用开发、快速入门、平台相关说明
3. `cangjie_tools`
   - 看 `cjpm`、`cjc`、`cjfmt`、`cjlint`、`cjdb`、`cjcov`、`cjprof`、`cjtrace`

Agent 最先建立的 CJMP 心智模型:

- 项目初始化: `cjpm init`
- 核心配置文件: `cjpm.toml`
- 构建: `cjpm build`
- 运行: `cjpm run`
- 测试: `cjpm test`
- 目标平台名: 用 `cjc -v` 查看 `Target`

当问题涉及:

- 项目结构 / 初始化 / 依赖: 优先查 `cjpm` 手册
- 平台编译 / target: 查 `cjc -v` 对应 target 和 `cjpm` target 配置
- 格式化 / lint / debug / profiling: 查 `cangjie_tools`

