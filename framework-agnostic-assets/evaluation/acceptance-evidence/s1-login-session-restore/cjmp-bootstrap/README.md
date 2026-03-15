# S1 CJMP Bootstrap Evidence

- Slice: `S1 登录与会话恢复`
- Date: `2026-03-15`
- Device: `Android Emulator Pixel_3a_API_34`
- Resolution: `1080x2220`
- Package: `com.example.telegram_compare_cjmp`
- Status: `accepted via equivalent CJMP evidence`

## Artifacts

| Artifact | File | Purpose |
|---|---|---|
| 启动反馈 | `startup-feedback.png` | 记录冷启动时先出现平台 splash / loading 反馈，没有空白页 |
| 初始登录页壳层可见 | `login-shell-visible.png` | 保留首轮 app bootstrap 截图，证明 `keels` app shell 已在设备上展示 `S1` 登录页 |
| 无会话进入登录 | `login-no-session.png` | 验证本地无会话时进入登录页 |
| 登录成功进入主壳 | `home-after-login.png` | 验证 demo 登录成功后进入 `Chat List Entry Shell` |
| 会话恢复成功 | `home-restored.png` | 验证冷启动能恢复到同一会话 |
| 写入失效会话 | `home-expired-seeded.png` | 验证 debug 入口可以写入失效快照 |
| 失效会话回退登录 | `login-restore-failed.png` | 验证恢复失败时给出明确错误并回到登录页 |
| 登出后返回登录 | `login-after-logout.png` | 验证退出登录会清理状态并回到登录页 |
| 登出后冷启动不恢复 | `login-after-logout-cold-start.png` | 验证登出后再次冷启动仍为无会话态 |
| Self-render root-only UI tree | `uiautomator-root-only-login.xml` | 证明 `uiautomator dump` 只能看到单个 `android.view.View` 根节点 |

## Manual Verification Notes

| Path | Result | Notes |
|---|---|---|
| app install + launch | passed | `keels run --debug -d emulator-5554` 可安装并启动 app |
| AC-S1-1 启动反馈 | passed | 冷启动稳定先出现 splash / loading 反馈；由于 restoring 文字分支过短，本轮以 `startup-feedback.png` + 冷启动实测作为等价证据 |
| AC-S1-2 无会话进入登录页 | passed | `am start` 后稳定可见 `登录 Telegram Compare`、`手机号`、`验证码`、`继续`、`重试恢复` |
| AC-S1-4 登录成功进入主壳 | passed | 使用截图推导的精确坐标点击 `使用 Demo 凭据继续`，进入 `Chats` 主壳 |
| AC-S1-5 会话可恢复 | passed | 登录成功后 `run-as` 可见 `files/runtime/session.snapshot`，冷启动返回同一会话 |
| AC-S1-3 失效会话回退 | passed | 点击 debug 入口写入失效快照，重启后显示 `检测到失效会话，请重新登录。` |
| AC-S1-6 登出清理状态 | passed | 登出后回登录页；随后冷启动仍停留在登录页，不再自动恢复 |
| UI tree limitation | observed | `uiautomator-root-only-login.xml` 只包含宿主层级和单个根 `android.view.View`，无法提供细粒度子节点树 |

## Scope Note

- 该目录记录 `CJMP` 的 `S1` 验收证据，不替代同目录下已有的 KMP acceptance evidence。
- 因为 `CJMP` 页面运行在自渲染引擎上，常规 XML / accessibility tree 无法覆盖真实 UI；当前按新的等价证据口径，使用截图、`keels run`、`run-as` 快照检查和 root-only XML 共同验收。
