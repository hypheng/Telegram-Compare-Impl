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
- `S1` 已有 repo-local 切片计划: `apps/cjmp/docs/s1-login-session-restore-plan.md`

## 下一步

1. 按 `apps/cjmp/docs/s1-login-session-restore-plan.md` 初始化 `S1` 的最小 `cjpm` 工程
2. 先让 `cjpm init` / `cjpm build -V` / `cjpm run` 在 repo 内打通
3. 确认 Android-first 的平台壳承载方式，再决定是否引入 `keels`
4. 在 `S1` 三态打通后，再推进 `S2/S3`
