# 2026-03-15 S4 KMP Cache And Offline Acceptance

- Scope: `S4 本地缓存与离线恢复`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 为 `shared-domain` 增加 `SyncSnapshot`、`SyncSnapshotRoute`、restore/save/clear use cases。
2. 为 `shared-data` 增加 snapshot storage 抽象、内存实现和 Android `SharedPreferences` 持久化实现。
3. 把 `InMemoryChatRepository` 扩展为可导出和恢复 list/detail snapshot，并在 snapshot 无效时回退到默认 fixtures。
4. 把 `androidApp` 启动流程改成优先恢复 snapshot，并在 list/detail 页面内联表达“来自本地缓存”的恢复来源。
5. 增加 debug-only 的清空本地缓存入口，并在 logout 时强制清空 snapshot。
6. 完成 `S4` 的模拟器验收，补齐 list restore、detail restore、latest message persisted、cache cleared fallback、logout clears cache 五组证据。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell pm clear com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell input tap ...`
- `adb -s emulator-5554 shell input swipe ...`
- `adb -s emulator-5554 shell am force-stop com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell am start -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell uiautomator dump ...`
- `adb -s emulator-5554 exec-out screencap -p ...`

## Evidence Produced

- `framework-agnostic-assets/evaluation/acceptance-evidence/s4-local-cache-and-offline-restore/s4-restore-list.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s4-local-cache-and-offline-restore/s4-restore-detail.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s4-local-cache-and-offline-restore/s4-restore-detail-latest-message.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s4-local-cache-and-offline-restore/s4-cache-cleared-fallback.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s4-local-cache-and-offline-restore/s4-logout-clears-cache.png`
- `framework-agnostic-assets/evaluation/acceptance-reports/S4-local-cache-and-offline-restore.md`

## AI Friction

- Android 软键盘弹起后，composer 和发送按钮的 Y 坐标会整体上移，旧坐标会把文本继续打进输入框而不是触发发送。
- `uiautomator dump` 输出是单行 XML，直接 `rg` 容易把整棵树回显出来，不适合人工判断，需要用更小的目标串做计数或先提取 bounds。
- 冷启动恢复详情页时，banner 与最新消息通常不能同时出现在一个视口里，因此需要拆成“恢复页 banner”与“恢复后最新消息”两组证据，分别证明 AC-S4-2 和 AC-S4-3。

## Outcome

- `S4` on KMP 已 accepted
- `S1-S4` 的 KMP 核心聊天工作流已完成代码、构建和设备侧验收
- 当前剩余外部 blocker 仍是 `figma handoff blocked` 与 `CJMP deferred by scope`
