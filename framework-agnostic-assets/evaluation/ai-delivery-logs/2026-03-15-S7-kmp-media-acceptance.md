# 2026-03-15 S7 KMP Media Acceptance

- Scope: `S7 媒体消息`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 为 framework-agnostic spec 新增 `S7` 的 requirement 与 UI design，并把 `S7` 登记到 MVP、UX flow、domain map、screen inventory、interaction states。
2. 为 `shared-domain` 扩展 `Message` 为“文本 + 可选媒体负载”，新增 media picker / send media use cases。
3. 为 `shared-data` 的 `InMemoryChatRepository` 增加 fixture 图片、媒体消息发送逻辑、列表预览回写与 snapshot 持久化。
4. 为 Android 壳新增 `Media` 入口、`Media Picker` sheet、图片气泡渲染和发送后的线程回写。
5. 完成 `S7` 的设备验收，补齐 thread with media、media picker open、media sent、cold-start restored 四组主要证据，并额外补了一张恢复后滚到底部的媒体确认图。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell cmd activity start-activity -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell cmd input tap ...`
- `adb -s emulator-5554 shell cmd input swipe ...`
- `adb -s emulator-5554 shell am force-stop com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell am force-stop com.example.telegram_compare_cjmp`
- `adb -s emulator-5554 exec-out screencap -p ...`
- `adb -s emulator-5554 shell uiautomator dump ...`

## Evidence Produced

- `framework-agnostic-assets/evaluation/acceptance-evidence/s7-media-messages/s7-thread-with-media.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s7-media-messages/s7-media-picker-open.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s7-media-messages/s7-media-sent.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s7-media-messages/s7-media-restored.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s7-media-messages/s7-media-restored-bottom.png`

## AI Friction

- `Message` 模型扩展到媒体负载后，`Search`, `Snapshot`, `Chat List Preview` 和 `Chat Detail Bubble` 都必须同步对齐，否则很容易出现“能发，但不能搜/不能恢复/不能预览”的半成品状态。
- 模拟器里 `CJMP` demo app 会抢焦点，尤其在误触系统手势区后更明显；本轮需要多次强制拉回 `KMP` task。
- 详情页在发送或恢复后默认回到线程上半段，因此“恢复成功”和“新媒体消息仍存在”必须拆成两步取证。

## Outcome

- `S7` on KMP 已 accepted
- 当前 `S1-S7` 的 KMP 主路径已覆盖文本与图片消息
- 下一切片是 `S8 Contacts 联系人`
- 当前剩余 backlog 只剩 `S9 AI 助手增强能力`
