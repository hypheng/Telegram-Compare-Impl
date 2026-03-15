# S1 KMP Delivery Plan

## 目标

把 `framework-agnostic-spec/requirements/s1-login-session-restore.md` 转成 KMP 的第一个真实用户价值切片，验证 shared-domain、shared-data 和 Android 壳的协作边界。

## 实现顺序

1. `shared-domain`
   - 定义 `UserSession`
   - 定义 `SessionRestoreResult`、`LoginResult`
   - 定义 `SessionRepository`
   - 落 `RestoreSessionUseCase`、`LoginWithCodeUseCase`、`LogoutUseCase`
2. `shared-data`
   - 用 `DemoSessionRepository` 承接 mock 登录和恢复逻辑
   - 用 `SessionStorage` 抽象持久化边界
   - 在 `androidMain` 落 `PreferencesSessionStorage`
   - 补 `restore / login / expired-session` 测试
3. `androidApp`
   - 启动时进入 restoring state
   - 无会话进入登录页
   - 登录成功或恢复成功进入 `Chat List Entry Shell`
   - 提供登出和写入失效会话的 demo/debug 入口
4. 验证与证据
   - 运行共享测试
   - 构建 `androidApp:assembleDebug`
   - 回写 acceptance report、AI log、parity matrix

## 当前完成度

- 已完成 shared-domain 语义切换，删除 bootstrap 占位用例。
- 已完成 shared-data demo 存储与 Android preferences 适配。
- 已完成 Android 原生 View 壳页，未引入额外 UI 依赖。
- 已通过 `:shared-domain:allTests`、`:shared-data:allTests` 和 `:androidApp:assembleDebug`。

## 未完成项

- Android 模拟器 / 真机手动验收和截图证据。
- `iosApp` 对同一切片的 host shell 规划。
- CJMP 同切片实现。

## 推荐验证命令

在仓库根目录:

```bash
bash ./.agents/setup/check-codex-ai-infra.sh
bash ./.agents/setup/check-kmp-env.sh
bash ./scripts/kmp-doctor.sh
```

在 `apps/kmp/` 下:

```bash
./gradlew :shared-domain:allTests :shared-data:allTests
./gradlew :androidApp:assembleDebug
```

## 风险

- `Chat List Entry Shell` 只用于承接 `S1` 成功落点，不能拿来替代 `S2` 的完整验收。
- 当前 Android 壳采用原生 View 实现，后续如果切到 Compose，需要先确认这是否影响对比口径。
- 会话恢复和失效路径已具备代码支持，但仍需设备侧验证动效、反馈时机和可读性。
