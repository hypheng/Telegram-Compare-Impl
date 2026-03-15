# CJMP Platform Constraints

重点检查:

- `CJMP_SDK_HOME`
- `keels`
- `cjc`
- `cjpm`
- `cjfmt`
- `cjlint`
- `cjdb`
- Android SDK / `adb`
- DevEco / Harmony 相关环境
- Xcode / `xcodebuild`

在当前阶段，Agent 先做两件事:

1. 明确能不能真正创建并运行官方 CJMP 工程模板
2. 如果不能，阻塞要写清楚是 SDK、插件、签名、设备还是 repo 范围问题

应用级核心命令:

- `keels doctor -v`
- `keels devices`
- `keels create --app`
- `keels create --module`
- `keels create --logic-module`
- `keels build`
- `keels run`

子模块级核心命令:

- `cjpm.toml`
- `cjpm build`
- `cjpm test`

当前约束提醒:

- 对 `apps/cjmp/` 这种应用目录，优先验证 `keels create` / `keels build` / `keels run` 路径，不要先把 `cjpm init` 当整应用模板
- `app-info.md` 明确区分了 `app`、`module`、`logic-module` 三类工程，开工前先选型
- `get-start.md` 把 VS Code 插件流列为“第一个应用”的官方 happy path；如果当前任务必须纯命令行推进，再切到 `start-tools.md`
- Android / Harmony / iOS 的真机、签名、平台包类型都由 `keels` 层控制，不应混成单纯仓颉编译问题
