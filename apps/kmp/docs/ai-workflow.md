# KMP AI Workflow

## 目标

让 AI 在 `apps/kmp/` 里能稳定重复同一套实现和调试闭环，而不是每次从头猜结构。

## 开始前必须读取

1. `framework-agnostic-spec/requirements/telegram-mvp.md`
2. `framework-agnostic-spec/ux/core-flows.md`
3. `framework-agnostic-spec/interface-design/`
4. `framework-agnostic-assets/evaluation/parity-matrix.md`
5. 对应切片的 acceptance report
6. `apps/kmp/docs/module-map.md`

## 开始前必须执行

从仓库根目录:

```bash
bash ./.agents/setup/check-kmp-env.sh
bash ./scripts/check-kmp-project.sh
bash ./scripts/kmp-doctor.sh
```

## 交付顺序

1. `shared-domain`
   - 领域模型
   - 用例
   - repository interface
   - 可跨平台验证的状态机
2. `shared-data`
   - mock / fixture-backed implementation
   - network adapter
   - storage adapter
3. `androidApp`
   - Android-first host shell
   - 页面入口
   - 状态映射
   - 调试入口
4. `iosApp`
   - 仅当切片稳定后再补

## 每次执行必须回写

- `framework-agnostic-assets/evaluation/ai-delivery-logs/`
- 对应切片 acceptance report
- `framework-agnostic-assets/evaluation/parity-matrix.md`

## 推荐验证命令

在 `apps/kmp/` 下执行:

```bash
./gradlew doctor
./gradlew printAiWorkflow
./gradlew :shared-domain:allTests :shared-data:allTests
./gradlew :androidApp:assembleDebug
```

说明:

- 当前 wrapper 锁定 `Gradle 9.3.1`，因为 `AGP 9.1.0` 要求至少该版本。
- 第一次冷启动需要从 `services.gradle.org` 拉取 distribution；如果网络较慢，先预热本机缓存再做完整验证。

## 进入 GitHub Issue 的常见场景

- Android-KMP plugin 或 built-in Kotlin 兼容性问题
- KMP source set / Gradle 配置导致 AI 多次返工
- 某个切片在 `shared-domain` 和 `androidApp` 的边界反复不稳定
- 同类构建或 IDE 问题重复出现
