# S1 CJMP Delivery Plan

- Slice: `S1 登录与会话恢复`
- Status: `deferred`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- UI Design: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Parity Source: `framework-agnostic-assets/evaluation/parity-matrix.md`

> 当前说明: 该计划保留为后续恢复双实现对齐时的起点，但不是本阶段的闭环要求。

## Goal

在 `apps/cjmp/` 中为 `S1` 初始化一个 Android-first 的最小可运行工程，先打通 `restoring -> login -> home entry shell` 三态，再补会话持久化和失效回退。

## Current Baseline

- `cjc`、`cjpm`、`keels`、`cjfmt`、`cjlint`、`cjdb` 已通过 `bash ./.agents/setup/check-cjmp-env.sh`
- 仓库里还没有任何 `cjpm.toml`、`src/` 或平台壳工程
- `S1` 的真相源已齐:
  - requirements
  - UI design
  - repo-side Figma backfill brief
  - KMP 设备验收 evidence

## Repo Mapping

建议把 `apps/cjmp/` 的 `S1` 先拆成以下最小结构：

```text
apps/cjmp/
├── README.md
├── docs/
│   └── s1-login-session-restore-plan.md
├── business/
│   └── session/
├── app-shell/
│   └── android/
└── build/
```

第一轮不追求完整多模块，只要目录意图清晰并能支持后续切片扩展。

## Bootstrap Order

1. 初始化最小 `cjpm` 工程
   - 在目标模块目录执行 `cjpm init`
   - 先让仓库出现 `cjpm.toml`、`src/` 和默认入口文件
2. 把 `S1` 三态映射到单一 app shell
   - `Restoring`
   - `Login`
   - `Home entry shell`
3. 再把会话逻辑拆到 `business/session`
   - `restore`
   - `login`
   - `logout`
   - `expired-session`
4. 最后再考虑 `keels`
   - 只有当最小 `cjpm build` / `cjpm run` 已稳定，且需要应用级打包或平台集成时再接入

## Practical Commands

根据 `/hypheng/cjmp-ai-docs` 当前可用文档，第一轮应围绕以下命令组织：

```bash
bash ./.agents/setup/check-cjmp-env.sh
cd apps/cjmp/<target-module>
cjpm init
cjpm build -V
cjpm run
cjpm test
```

如果开始拆模块，`cjpm.toml` 使用本地依赖：

```toml
[dependencies]
session = { path = "./business/session" }
```

`cjpm.toml` 的第一轮重点是：

- `[package]`
- `name`
- `version`
- `output-type`
- 本地 `path` 依赖

## S1 Scope Mapping

| S1 capability | CJMP first-pass target |
|---|---|
| 启动 restoring 反馈 | app shell 默认先进入 restoring 视图 |
| 无会话进入登录 | `restore` 返回 no-session 时切到登录页 |
| 登录成功进入主壳 | `login` 成功后进入 entry shell |
| 会话可恢复 | 本地 demo session snapshot 恢复 |
| 失效会话回退 | 恢复失败时清理快照并显示错误 |
| 登出清理状态 | `logout` 后回登录页并清空本地状态 |

## Why Keels Is Deferred

- 当前最主要的不确定性不是应用打包，而是 repo 内还没有最小 `cjpm` 工程
- 在 `cjpm build` / `cjpm run` 还没打通前就引入 `keels`，会把“语言工程初始化问题”和“平台打包问题”混在一起
- 因此 `keels` 在 `S1` 中只作为已安装能力记录，不作为第一轮退出条件

## Exit Criteria

`CJMP` 侧 `S1` 达到第一轮 ready 状态时，应满足：

1. 仓库内存在最小 `cjpm` 工程
2. `cjpm build -V` 可通过
3. `cjpm run` 能展示 `restoring -> login -> home` 主路径
4. parity matrix 中 `CJMP` 状态从 `planned` 升级到至少 `in-progress`

## Risks

- 当前没有 repo-local CJMP 工程模板，第一步就可能暴露目录与 target 选择问题
- `S1` 的真实 Figma file / frame links 仍未补回，CJMP UI 仍需先以 repo-side brief 为依据
- 如果一开始就拆太多模块，容易把 `S1` 变回大爆炸初始化
