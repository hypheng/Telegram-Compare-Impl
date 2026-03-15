# S3 Acceptance Evidence

- Slice: `S3 单聊详情与文本发送`
- Date: `2026-03-15`
- Device: `Android Emulator Pixel_3a_API_34`
- Resolution: `1080x2220`
- Package: `com.telegram.compare.kmp.android`

## Artifacts

| Artifact | File | Purpose |
|---|---|---|
| 默认聊天详情 | `s3-default-detail.png` / `s3-default-detail.xml` | 验证从 `S2` 进入真实聊天详情 |
| 文本发送成功 | `s3-send-success.png` / `s3-send-success.xml` | 验证消息进入 sent 并保留在消息流中 |
| 文本发送失败 | `s3-send-failed.png` / `s3-send-failed.xml` | 验证 failed bubble 与 retry 入口 |
| 失败重试成功 | `s3-retry-success.png` / `s3-retry-success.xml` | 验证 failed message 经过 retry 转为 sent |
| 返回会话列表 | `s3-return-to-list.png` / `s3-return-to-list.xml` | 验证列表预览与状态 banner 回写 |

## Manual Verification Notes

| Path | Result | Notes |
|---|---|---|
| 会话列表 -> 聊天详情 | passed | 点击 `Telegram Compare` 后进入真实 detail loading / ready |
| 默认消息历史 | passed | 同时展示 incoming / outgoing bubble、时间和 composer |
| 文本发送成功 | passed | `KMP_S3` 进入消息流并显示 `刚刚 · 已发送` |
| 强制发送失败 | passed | `FAIL2` 进入 `刚刚 · 发送失败`，并显示 `重试` |
| 失败重试 | passed | `重试` 后 `FAIL2` 变为 `刚刚 · 已发送` |
| 返回列表承接 | passed | 返回后列表 banner 显示重试成功，`Telegram Compare` 保持在首位 |

## Known Gap

- 真实 Figma file / frame / node handoff 仍未补齐，当前 `S3` 的 `D2` 仍以 repo-side brief 代理。
