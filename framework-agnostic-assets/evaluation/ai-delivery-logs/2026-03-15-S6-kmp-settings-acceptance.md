# 2026-03-15 S6 KMP Settings Acceptance

- Scope: `S6 设置与个人资料`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 为 framework-agnostic spec 新增 `S6` 的 requirement 与 UI design，并把 `S6` 登记到 MVP、UX flow、domain map、screen inventory、interaction states。
2. 为 `shared-domain` 新增 profile / preference 模型、settings repository 边界和 load / toggle use cases。
3. 为 `shared-data` 新增 `LocalSettingsRepository` 与 `PreferencesUserSettingsStorage`，并修正 `PreferencesSessionStorage.clear()` 只删除 session keys，避免误清 settings / snapshot。
4. 为 Android 壳新增 `Settings` screen、底部导航跳转、profile hero、preference rows 和设置页 logout。
5. 完成 `S6` 的设备验收，补齐 overview、preference toggled、cold-start persistence、logout 四组证据。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell cmd activity start-activity -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell cmd input tap ...`
- `adb -s emulator-5554 shell am force-stop com.telegram.compare.kmp.android`
- `adb -s emulator-5554 exec-out screencap -p ...`
- `adb -s emulator-5554 shell uiautomator dump ...`

## Evidence Produced

- `framework-agnostic-assets/evaluation/acceptance-evidence/s6-settings-and-profile/s6-settings-overview.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s6-settings-and-profile/s6-preference-toggled.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s6-settings-and-profile/s6-settings-restored.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s6-settings-and-profile/s6-settings-logout.png`

## AI Friction

- `S6` 暴露了一个真实边界问题: `PreferencesSessionStorage.clear()` 原本会清空整个 `SharedPreferences`，会连带删掉 snapshot 与未来 settings 数据，必须在本轮修正为 key-scoped clear。
- `uiautomator dump` 在快速 `force-stop -> cold start` 后偶发触发 ANR，本轮改成“冷启动 screenshot 为主，XML 只在稳定页面补采样”。
- 模拟器中同时存在 `CJMP` demo app 时，底部导航和恢复路径的前台可能被另一侧应用抢走，因此设备验收期间需要显式 force-stop 非当前 track。

## Outcome

- `S6` on KMP 已 accepted
- `Settings` tab 现在是可持久化的真实页面，不再只是底部占位入口
- `S1-S6` 的 KMP 主路径现已覆盖登录、列表、详情、恢复、搜索和设置
