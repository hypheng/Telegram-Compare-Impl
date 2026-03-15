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

遵循官方 `app-dev -> quick-start -> keels` 路径先建应用壳，把当前切片逻辑直接收在 `telegram_compare_app` 内。`cjpm` 在这里主要作为 app `lib/` 的包配置与构建依赖描述，而不是单独模块层。

## 当前结构

```text
apps/cjmp/
├── README.md
├── docs/
│   └── s1-login-session-restore-plan.md
└── telegram_compare_app/     # keels create --app 生成的 app shell
```

## 当前状态

- 本机已具备 `cjc`、`cjpm`、`keels`
- `cjfmt`、`cjlint`、`cjdb` 也纳入了环境检查
- CJMP 文档入口已切到 Context7 `/hypheng/cjmp-ai-docs`
- `apps/cjmp/telegram_compare_app` 已通过 `keels create --app` 初始化
- `apps/cjmp/telegram_compare_app/lib/session.cj` 已内联 `SessionRepository` 与本地 snapshot 逻辑
- `keels build apk --platform android-arm64 --debug` 和 `keels run --debug -d emulator-5554` 已在新 worktree 中打通
- Android 模拟器上已完成 `S1` 的登录成功、冷启动恢复、失效会话回退、登出和登出后冷启动证据，当前 `S1` 可在 CJMP 侧标为 `accepted`
- `CJMP` 页面为自渲染，`uiautomator dump` 仅暴露单个根 `android.view.View`；当前已按等价证据口径保存截图和 root-only XML

## 当前工作方式

1. 先跑 `bash ./.agents/setup/check-cjmp-env.sh`
2. app 壳使用 `keels create/build/run`
3. 当前切片逻辑直接维护在 `telegram_compare_app/lib/`
4. 参考 `apps/cjmp/docs/s1-login-session-restore-plan.md` 的同一流程继续推进下一个切片，并保留 self-render 证据口径
