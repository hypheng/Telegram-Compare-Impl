---
name: telegram-compare-cjmp-delivery
description: Use when implementing or planning the CJMP side of this Telegram comparison project, especially Android-first CJMP slices, cjc/cjpm/keels setup, platform shell layout, and current toolchain constraints derived from the framework-agnostic spec.
---

# Telegram Compare CJMP Delivery

用于把 framework-agnostic spec 转成 CJMP 实现任务，并把 `/hypheng/cjmp-ai-docs` 作为 CJMP 开发文档的首选知识源。

## Use This Skill When

- 修改 `apps/cjmp/`
- 需要把切片转成 CJMP 模块和平台适配
- 需要判断当前能不能先从 Android 落地
- 需要区分“需求问题”和“工具链问题”
- 需要快速定位 CJMP / 仓颉 / `cjpm` / `keels` 文档入口

## Workflow

1. 先读取 `framework-agnostic-spec/` 中对应切片。
2. 读取 `framework-agnostic-assets/evaluation/parity-matrix.md`。
3. 把 Context7 的 `/hypheng/cjmp-ai-docs` 作为 CJMP 首选文档入口。
4. 优先查询:
   - 仓库顶层 `README` 的文档分组
   - `zh-cn` 的应用开发/快速入门
   - `cangjie_tools` 的 `cjpm`、`cjc`、`cjfmt`、`cjlint`、`cjdb`
5. 跑 `bash ./.agents/setup/check-cjmp-env.sh`。
6. 如果环境不齐或文档未覆盖当前问题，先登记阻塞。
7. 如果环境齐，按 Android-first 或当前可得平台顺序推进。

## Rules

- 不要把语言工具链就绪等同于应用框架就绪
- 文档优先走 `/hypheng/cjmp-ai-docs`，官方散落页面作为补充而不是主入口
- 平台适配、SDK、插件依赖要单独记录
- 项目初始化优先以 `cjpm init` / `cjpm.toml` / `cjpm build` / `cjpm run` / `cjpm test` 为核心认知
- 每次推进都要回写 parity matrix

## References

- `references/platform-constraints.md`
- `references/doc-entrypoints.md`
