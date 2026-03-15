# 2026-03-15 S1-S3 KMP UI Device Recapture

- Scope: `S1 登录与会话恢复` + `S2 会话列表` + `S3 单聊详情与文本发送`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 启动 `Pixel_3a_API_34` 模拟器并安装最新 `androidApp` debug 包。
2. 重采 `S1` 登录页与登录成功进入主壳的主路径截图。
3. 重采 `S2` 默认列表截图，确认 fixed chrome + scoped scroll 结构已落在设备上。
4. 重采 `S3` 默认详情、发送成功和返回列表截图，确认固定 composer 和消息区滚动结构已落在设备上。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell pm clear com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell am start -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell uiautomator dump ...`
- `adb -s emulator-5554 exec-out screencap -p ...`

## Evidence Refreshed

- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-no-session.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/s2-default-list.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s3-chat-detail-and-send/s3-default-detail.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s3-chat-detail-and-send/s3-send-success.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s3-chat-detail-and-send/s3-return-to-list.png`

## AI Friction

- `PATH` 命中了过时的 `~/Library/Android/sdk/tools/emulator`，会因为错误的相对库路径直接启动失败。
- 实际可用的二进制是 `~/Library/Android/sdk/emulator/emulator`，切到该路径后模拟器正常启动。
- `S3` 发送成功第一次重采时只打开了键盘，没有真正发送消息；第二次通过收起键盘再点击发送完成修正。

## Outcome

- 新 UI 已在真实模拟器上验证，不再只是代码和构建层面的修正
- `S1-S3` 主路径关键截图已经与当前 fixed chrome / scoped scroll 结构对齐
- 仍有部分次级状态截图沿用本轮 UI refresh 前的历史证据
