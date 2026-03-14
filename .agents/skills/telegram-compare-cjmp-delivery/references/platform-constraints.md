# CJMP Platform Constraints

重点检查:

- `cjc`
- `cjpm`
- `keels`
- `cjfmt`
- `cjlint`
- `cjdb`
- Android SDK / `adb`
- DevEco / Harmony 相关环境

在当前阶段，Agent 先做两件事:

1. 明确能不能真正初始化工程
2. 如果不能，阻塞要写清楚

核心项目配置:

- `cjpm.toml`
- `src/`

核心命令:

- `cjpm init`
- `cjpm build`
- `cjpm run`
- `cjpm test`
