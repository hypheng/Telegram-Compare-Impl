# CJMP App Track

## 目标

实现 Telegram 对比项目的 CJMP 版本。

## Agent 入口

- 规格先看 `framework-agnostic-spec/`
- 共享契约和 parity 先看 `framework-agnostic-assets/`
- 实现时优先使用 `$telegram-compare-cjmp-delivery`
- 环境检查先跑 `bash ./.agents/setup/check-cjmp-env.sh`
- 文档入口优先用 Context7 的 `/hypheng/cjmp-ai-docs`

## 当前策略

先保留与 `framework-agnostic-spec/` 同步的实现入口和技术决策说明，并把 `/hypheng/cjmp-ai-docs` 作为 CJMP 首选开发文档源。

## 建议结构

```text
apps/cjmp/
├── README.md
├── app-shell/       # 各平台壳层
├── business/        # 业务模块
├── platform/        # Android / iOS / HarmonyOS 适配
└── build/           # CJMP / keels 相关构建配置
```

## 当前状态

- 本机已具备 `cjc`、`cjpm`、`keels`
- `cjfmt`、`cjlint`、`cjdb` 也纳入了环境检查
- CJMP 文档入口已切到 Context7 `/hypheng/cjmp-ai-docs`
- 仓库级最小可运行工程仍未初始化，因此当前还是“有工具链、没项目模板”的状态

## 下一步

1. 先从 `/hypheng/cjmp-ai-docs` 确认当前切片对应的文档入口
2. 基于 `cjpm init` / `cjpm.toml` 初始化最小工程
3. 确认目标平台环境与 target
4. 以 S1/S2/S3 初始化最小可运行工程
