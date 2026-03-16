# S1 CJMP Delivery Plan

- Slice: `S1 登录与会话恢复`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- UI Design: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Parity Source: `framework-agnostic-assets/evaluation/parity-matrix.md`

> 当前说明: 该计划已经从“保留起点”升级为 `CJMP` 侧 `S1` 验收记录；当前 app shell、业务模块、设备截图和 self-render 约束说明已齐，可以在 parity 中按等价证据计为 `accepted`。

## Goal

在 `apps/cjmp/` 中为 `S1` 维护一个 Android-first 的最小可运行工程，用官方 `keels` app template 承接 `restoring -> login -> home entry shell` 三态，并把会话持久化、失效回退和登出冷启动都闭合到设备证据。

## Current Baseline

- `cjc`、`cjpm`、`keels`、`cjfmt`、`cjlint`、`cjdb` 已通过 `bash ./.agents/setup/check-cjmp-env.sh`
- `apps/cjmp/telegram_compare_app` 已通过 `keels create --app -n telegram_compare_cjmp` 初始化
- `apps/cjmp/telegram_compare_app/lib/session.cj` 已承载 `restore/login/logout/expired-session` demo 逻辑
- `apps/cjmp/telegram_compare_app/lib/index.cj` 已落地 `restoring/login/home` 三态 UI
- `S1` 的真相源已齐:
  - requirements
  - UI design
  - repo-side Figma backfill brief
  - KMP 设备验收 evidence

## Repo Mapping

当前 `S1` 已采用以下 repo-local 结构：

```text
apps/cjmp/
├── README.md
├── docs/
│   └── s1-login-session-restore-plan.md
└── telegram_compare_app/
```

当前不再单独维护 `app-shell/android/` 空目录，而是直接使用 `keels` 生成的 app 工程承载 Android-first 验证。

## Bootstrap Order

1. 先检查工具链和设备
   - `bash ./.agents/setup/check-cjmp-env.sh`
   - `keels devices --verbose`
2. 用官方 app template 初始化应用壳
   - `keels create --app -n telegram_compare_cjmp apps/cjmp/telegram_compare_app`
3. 把 `S1` 三态映射到单一 app shell
   - `Restoring`
   - `Login`
   - `Home entry shell`
4. 把会话逻辑直接收进 app `lib/`
   - `restore`
   - `login`
   - `logout`
   - `expired-session`
5. 用 `keels` 做应用构建与安装
   - `keels build apk --platform android-arm64 --debug`
   - `keels run --debug -d emulator-5554`

## Practical Commands

根据 `/hypheng/cjmp-ai-docs` 当前 quick-start / tools 文档，当前计划围绕以下命令组织：

```bash
bash ./.agents/setup/check-cjmp-env.sh
keels devices --verbose
keels create --app -n telegram_compare_cjmp apps/cjmp/telegram_compare_app

cd /Users/haifengsong/code-base/telegram/Telegram-Compare-Impl-cjmp/apps/cjmp/telegram_compare_app
keels build apk --platform android-arm64 --debug
keels run --debug -d emulator-5554
```

## S1 Scope Mapping

| S1 capability | CJMP first-pass target |
|---|---|
| 启动 restoring 反馈 | app shell 默认先进入 restoring 视图 |
| 无会话进入登录 | `restore` 返回 no-session 时切到登录页 |
| 登录成功进入主壳 | `login` 成功后进入 entry shell |
| 会话可恢复 | `SessionRepository` + app-private snapshot + 冷启动恢复截图已闭环 |
| 失效会话回退 | 恢复失败时清理快照并显示错误 |
| 登出清理状态 | `logout` 后回登录页并清空本地状态 |

## Validation Snapshot

- `bash ./.agents/setup/check-cjmp-env.sh`: passed
- `keels build apk --platform android-arm64 --debug`: passed
- `keels run --debug -d emulator-5554`: install + launch passed
- `adb shell run-as com.example.telegram_compare_cjmp sh -c 'find . -maxdepth 4 -name "session.snapshot"'`: confirmed `./files/runtime/session.snapshot`
- CJMP device evidence:
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/startup-feedback.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-no-session.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-after-login.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-restored.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-expired-seeded.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-restore-failed.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-after-logout.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-after-logout-cold-start.png`
- `uiautomator dump`: only exposes a root `android.view.View`, archived as `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/uiautomator-root-only-login.xml`

## Exit Criteria Review

1. 仓库内已存在可构建的 `keels` app 工程，且 `S1` 会话逻辑已经收进 app `lib/`。
2. `keels build apk --platform android-arm64 --debug`、`keels run --debug -d emulator-5554` 已通过。
3. 设备侧已可靠展示 `startup feedback -> login -> home` 主路径。
4. `restore failed`、`logout` 和 `logout cold start` 已形成 CJMP 侧截图和等价证据。
5. parity matrix 中 `CJMP` 状态已从 `in-progress` 升级到 `accepted`。

## Risks

- `CJMP` 自渲染页面在 `uiautomator dump` 中仍只暴露单个根 `android.view.View`，后续自动化若要扩展需继续走坐标 / 图像或 app-level test hook。
- 当前 app 侧 snapshot 目录写死为 Android 包名私有目录；如果后续调整 applicationId，需要同步更新该路径。
- `S1` 的真实 Figma file / frame links 仍未补回，CJMP UI 仍需先以 repo-side brief 为依据。
