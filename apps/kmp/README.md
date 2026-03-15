# KMP App Track

## 目标

实现 Telegram 对比项目的 KMP 版本。

## Agent 入口

- 规格先看 `framework-agnostic-spec/`
- 共享契约和 parity 先看 `framework-agnostic-assets/`
- 实现时优先使用 `$telegram-compare-kmp-delivery`
- 环境检查先跑 `bash ./.agents/setup/check-kmp-env.sh`

## 当前策略

按照 Kotlin 官方建议，优先共享业务逻辑、数据契约和状态机，UI 是否共享在产品切片稳定后再决定。

## 当前结构

```text
apps/kmp/
├── README.md
├── gradle/
├── gradlew
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── shared-domain/   # 领域与用例
├── shared-data/     # 数据与适配占位
├── androidApp/      # Android 壳
├── iosApp/          # iOS 壳说明
└── docs/            # AI 交付与调试 runbook
```

## 当前状态

- 已初始化 Gradle Wrapper
- 已建立 Android-first 的 KMP 根工程骨架
- `S1 登录与会话恢复` 已进入真实切片实现
- `shared-domain` 已补 `UserSession`、restore/login/logout use cases
- `shared-data` 已补 demo session repository 与 Android preferences storage
- `androidApp` 已有启动恢复、登录和 `Chat List Entry Shell` 原生 Android 壳
- `iosApp` 仍是 host shell 计划位，还没有 Xcode 工程
- `S2` 及后续切片仍未完成验收

## 推荐 AI 开发顺序

1. 先读 `framework-agnostic-spec/`、验收报告和 parity matrix
2. 运行 `bash ./scripts/kmp-doctor.sh`
3. 在 `shared-domain` 落领域模型、用例和接口
4. 在 `shared-data` 落数据实现、mock、存储和网络适配
5. 在 `androidApp` 落 Android-first UI 壳
6. 切片稳定后再补 `iosApp`
7. 每次执行都回写 AI delivery log、acceptance report 和 parity matrix

## 常用命令

从仓库根目录执行:

```bash
bash ./scripts/kmp-doctor.sh
./scripts/kmp-gradle-no-proxy.sh :shared-domain:allTests
cd apps/kmp
./gradlew doctor
./gradlew printAiWorkflow
./gradlew :shared-domain:allTests :shared-data:allTests
./gradlew :androidApp:assembleDebug
```

## KMP AI Infra 入口

- 交付 workflow: `apps/kmp/docs/ai-workflow.md`
- 模块边界: `apps/kmp/docs/module-map.md`
- 调试 runbook: `apps/kmp/docs/debug-runbook.md`
- 当前切片计划: `apps/kmp/docs/s1-login-session-restore-plan.md`
- Agent prompt: `.agents/prompts/kmp-delivery.md`
- 调试 prompt: `.agents/prompts/kmp-debug.md`
