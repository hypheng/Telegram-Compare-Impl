# CJMP Doc Query Prompt

使用 Context7 的 `/hypheng/cjmp-ai-docs` 查询 CJMP 文档时，优先按下面格式组织问题:

## Query Template

我正在处理一个 CJMP 应用开发问题，请优先从 `/hypheng/cjmp-ai-docs` 中返回最相关的文档入口和命令。

上下文:

- 目标平台: `Android` / `iOS` / `HarmonyOS`
- 当前任务: `project init` / `build` / `run` / `test` / `debug` / `platform adaptation`
- 关心的配置文件: `cjpm.toml` / 其他
- 需要的输出:
  - 推荐先看的文档分组
  - 关键命令
  - 关键配置项
  - 常见陷阱

## Example

我正在做 CJMP Android-first 应用初始化。请从 `/hypheng/cjmp-ai-docs` 返回最相关的 `cjpm init`、`cjpm.toml`、build/run/test、target 配置和 Android 相关文档入口。

