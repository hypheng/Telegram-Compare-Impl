# 2026-03-15 S5 KMP Search Acceptance

- Scope: `S5 搜索会话 / 消息`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 为 framework-agnostic spec 新增 `S5` 的 requirement 与 UI design，并把 `S5` 登记到 MVP、UX flow、domain map、screen inventory、interaction states。
2. 为 `shared-domain` 新增搜索领域模型、repository 边界和 use case。
3. 为 `shared-data` 的 `InMemoryChatRepository` 增加 chat hit + message hit 搜索实现，并把 `ChatListScenario.ERROR` 映射为搜索失败态。
4. 为 Android 壳增加独立 `Search` screen、从列表进入全局搜索、分组结果、从 chat result 跳详情、从 message result 定位详情、返回恢复搜索上下文。
5. 在详情页增加命中消息预览卡与命中 bubble 高亮表达，避免 message jump 只剩 banner。
6. 完成 `S5` 的设备验收，补齐 grouped results、chat jump、message jump、empty、error 五组证据。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell pm clear com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell am start -W -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell input tap ...`
- `adb -s emulator-5554 shell input text ...`
- `adb -s emulator-5554 shell uiautomator dump ...`
- `adb -s emulator-5554 exec-out screencap -p ...`

## Evidence Produced

- `framework-agnostic-assets/evaluation/acceptance-evidence/s5-search-chats-and-messages/s5-search-results.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s5-search-chats-and-messages/s5-chat-result-jump.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s5-search-chats-and-messages/s5-message-result-jump.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s5-search-chats-and-messages/s5-search-empty.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s5-search-chats-and-messages/s5-search-error.png`

## AI Friction

- 模拟器里同时存在 `KMP` 和 `CJMP` demo app，前台焦点会被另一侧应用任务抢走，导致设备证据采集混入错误 app。
- `message result` 的首版实现只有 banner，没有保证命中消息一定在首屏可见；后续通过“命中消息预览卡”补齐了稳定取证路径。
- 软键盘弹起后，搜索页的输入和动作按钮坐标会变化，必须从 XML 提取 bounds 再触发搜索。

## Outcome

- `S5` on KMP 已 accepted
- 当前 `S1-S5` 的 KMP 主路径 + 搜索路径已完成代码、构建和设备侧验收
- 剩余 blocker 仍是 `figma handoff blocked` 与 `CJMP deferred by scope`
