# 2026-03-15 S1-S3 KMP Secondary Evidence Refresh

- Scope: `S1 登录与会话恢复` + `S2 会话列表` + `S3 单聊详情与文本发送`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 对 `S1` 的 secondary evidence 重新走通设备路径，重采 `home-after-login`、`home-restored`、`home-debug-seeded`、`login-restore-failed`、`login-logged-out`、`login-after-logout-cold-start`。
2. 对 `S2` 的 refresh 路径重新触发下拉刷新，并重采 `s2-refresh`。
3. 复核 `S2` 的 search / empty / error 与 `S3` 的 failed / retry 证据时间戳，确认它们已经来自当前 fixed chrome + scoped scroll UI。
4. 清理 acceptance report / parity matrix 中“secondary evidence still historical”的陈旧说明。

## Verification

- `adb -s emulator-5554 shell input tap ...`
- `adb -s emulator-5554 shell input swipe ...`
- `adb -s emulator-5554 shell am force-stop com.telegram.compare.kmp.android`
- `adb -s emulator-5554 shell am start -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell uiautomator dump ...`
- `adb -s emulator-5554 exec-out screencap -p ...`

## Evidence Refreshed

- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-restored.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-debug-seeded.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-restore-failed.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-logged-out.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-after-logout-cold-start.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/s2-refresh.png`

## AI Friction

- 批量 heredoc capture 在这轮没有稳定落盘到目标 evidence 文件，导致部分截图看似执行成功但时间戳未更新。
- `S1` 登录页在 UI 刷新后改成了居中的 card 布局，旧坐标会误点空白区，必须先从 XML 提取按钮 bounds 再继续。
- 切换成“单步操作 + XML 验证 + 逐文件写入”之后，证据采集恢复稳定。

## Outcome

- `S1-S3` 的 KMP 设备证据已与当前 UI 形态完全对齐
- parity matrix 不再需要保留“secondary evidence still historical”问题
- 剩余 blocker 只剩真实 Figma frame/node handoff 缺失和 `CJMP deferred by scope`
