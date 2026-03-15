# S1 Acceptance Evidence

- Slice: `S1 登录与会话恢复`
- Date: `2026-03-15`
- Device: `Android Emulator Pixel_3a_API_34`
- Resolution: `1080x2220`
- Package: `com.telegram.compare.kmp.android`

## Artifacts

| Artifact | File | Purpose |
|---|---|---|
| 无会话进入登录 | `login-no-session.png` / `login-no-session.xml` | 验证冷启动无会话时直接进入登录页 |
| 登录成功进入主壳 | `home-after-login.png` / `home-after-login.xml` | 验证合法输入后进入 `Chat List Entry Shell` |
| 会话恢复成功 | `home-restored.png` / `home-restored.xml` | 验证登录后冷启动会自动恢复 |
| 写入失效会话 | `home-debug-seeded.png` / `home-debug-seeded.xml` | 验证 debug 入口成功写入失效快照 |
| 失效会话回退登录 | `login-restore-failed.png` / `login-restore-failed.xml` | 验证恢复失败时给出明确错误并回到登录页 |
| 登出后返回登录 | `login-logged-out.png` / `login-logged-out.xml` | 验证退出登录动作会清理状态并回到登录页 |
| 登出后冷启动不恢复 | `login-after-logout-cold-start.png` / `login-after-logout-cold-start.xml` | 验证登出后再次冷启动仍为无会话态 |

## Manual Verification Notes

| Path | Result | Notes |
|---|---|---|
| 冷启动 -> 无会话 -> 登录页 | passed | 首屏出现登录页，字段和 CTA 可用 |
| 登录 -> 主壳入口 | passed | 使用预填 demo 验证码进入主壳 |
| 冷启动 -> 会话恢复 | passed | 状态条文案变为“已恢复上次会话，主壳入口已就绪。” |
| 主壳 -> 写入失效会话 -> 冷启动 | passed | 下次启动显示“已保存的会话已失效，请重新登录。” |
| 主壳 -> 登出 | passed | 回到登录页并显示“已退出登录。” |
| 登出 -> 冷启动 | passed | 冷启动后仍是无会话登录页，不再直接恢复 |

## Known Gap

- `MainScreenState.Restoring` 的视觉态已经在模拟器上人工观察到，但它只持续 `650ms`，本轮没有稳定截到独立单帧静态图。
- 该状态的存在仍可从 `apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivity.kt` 的 restoring 渲染分支和冷启动实测行为共同验证。
