---
name: telegram-compare-cjmp-delivery
description: Use when implementing or planning the CJMP side of this Telegram comparison project, especially when selecting app/module/logic-module templates, following the official `cjmp/zh-cn/app-dev` quick-start flow, using `keels` for app bootstrap/build/run, or separating CJMP app docs from raw Cangjie tool docs.
---

# Telegram Compare CJMP Delivery

用于把 framework-agnostic spec 转成 CJMP 实现任务，并按官方 `cjmp/zh-cn/app-dev` 主线选择正确的应用模板、工具链和 UI 入口，而不是把纯仓颉模块流程误当成 CJMP 应用流程。

## Use This Skill When

- 修改 `apps/cjmp/`
- 需要把切片转成 CJMP `app` / `module` / `logic-module`
- 需要判断当前能不能先从 Android 落地
- 需要区分“CJMP 应用框架问题”和“原始仓颉语言 / `cjpm` 问题”
- 需要快速定位 `keels`、应用工程结构、CJ UI 组件或平台打包文档入口

## Workflow

1. 先读取 `framework-agnostic-spec/` 中对应切片。
2. 读取 `framework-agnostic-assets/evaluation/parity-matrix.md`。
3. 把 Context7 的 `/hypheng/cjmp-ai-docs` 作为 CJMP 首选文档入口，并先读 `references/doc-entrypoints.md`。
4. 默认从 `cjmp/zh-cn/app-dev/README.md` 和 `cjmp/zh-cn/app-dev/quick-start/README.md` 起步，再按任务继续下钻:
   - 工程创建 / CLI 流程: `quick-start/start-tools.md`
   - VS Code / 模板创建流: `quick-start/start-plugins.md`、`quick-start/get-start.md`
   - 工程类型和目录结构: `quick-start/app-info.md`
   - build / run / devices / signing: `app-dev/tools/tools-cmd.md`
   - UI 组件能力: `framework-dev/cj-ui/README.md`
5. 先判断当前任务对应哪类工程:
   - `app`: 独立 CJMP 应用
   - `module`: 嵌入已有原生工程的 CJMP 页面或组件
   - `logic-module`: 可复用逻辑模块
6. 跑 `bash ./.agents/setup/check-cjmp-env.sh`；如果是官方 SDK / 设备问题，再补查 `keels doctor -v`、`keels devices` 对应文档。
7. 如果环境不齐或文档未覆盖当前问题，先登记阻塞；如果环境齐，再按 Android-first 或当前可得平台顺序推进。

## Rules

- 不要把语言工具链就绪等同于应用框架就绪
- 文档优先走 `/hypheng/cjmp-ai-docs` 中的 `cjmp/zh-cn/app-dev`，不要默认跳到顶层 `cangjie_tools`
- `apps/cjmp/` 的应用初始化优先遵循 `keels create` 或官方插件模板流，不要默认用 `cjpm init` 创建整个应用
- `cjpm` / `cjpm.toml` / `cjpm test` 主要用于 `lib/`、`logic-module/` 或纯仓颉子模块，不是 CJMP app bootstrap 的第一反应
- 平台适配、SDK、插件、签名、真机依赖要单独记录
- UI 实现前先确认 `framework-dev/cj-ui/README.md` 是否已有对应组件或状态管理能力
- 每次推进都要回写 parity matrix

## References

- `references/platform-constraints.md`
- `references/doc-entrypoints.md`
