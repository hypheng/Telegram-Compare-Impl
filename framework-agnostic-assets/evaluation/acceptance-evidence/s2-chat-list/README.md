# S2 Acceptance Evidence

- Slice: `S2 会话列表`
- Date: `2026-03-15`
- Device: `Android Emulator Pixel_3a_API_34`
- Resolution: `1080x2220`
- Package: `com.telegram.compare.kmp.android`

## Artifacts

| Artifact | File | Purpose |
|---|---|---|
| 冷启动登录入口 | `s2-login-entry.png` / `s2-login-entry.xml` | 验证从 `S1` 登录入口进入 `S2` 主壳 |
| 默认会话列表 | `s2-default-list.png` / `s2-default-list.xml` | 验证默认 chat list 展示标题、预览、时间和未读数 |
| 搜索命中结果 | `s2-search-result.png` / `s2-search-result.xml` | 验证按标题搜索会话可得到过滤结果 |
| 搜索空态 | `s2-search-empty.png` / `s2-search-empty.xml` | 验证无匹配结果时有明确空态和恢复入口 |
| 下拉刷新 | `s2-refresh.png` / `s2-refresh.xml` | 验证 refresh 成功反馈和未读变化 |
| fixture 空态 | `s2-empty-scenario.png` / `s2-empty-scenario.xml` | 验证 demo 空态 scenario 的页面级恢复表达 |
| fixture 错误态 | `s2-error-scenario.png` / `s2-error-scenario.xml` | 验证 demo 错误态 scenario 的页面级错误与重试入口 |

## Manual Verification Notes

| Path | Result | Notes |
|---|---|---|
| 登录 -> 会话列表默认态 | passed | 登录后直接落到真实 chat list，而不是 debug shell |
| 默认列表信息完整 | passed | 每行展示头像占位、标题、预览、时间和未读数 |
| 搜索命中 | passed | `infra` 返回 `AI Infra` |
| 搜索空态 | passed | `zzz` 返回明确空态和 `清除搜索` |
| 下拉刷新 | passed | 顶部 banner 变为 `会话列表已刷新。`，首行未读数递增 |
| 空态 scenario | passed | `空态` 按钮切换后出现 `暂无会话` 卡片和 `恢复默认数据` |
| 错误态 scenario | passed | `错误态` 按钮切换后出现 `列表加载失败` 和 `重试加载` |

## Known Gap

- 真实 Figma file / frame / node handoff 仍未补齐，当前 `S2` 的 `D2` 仍以 repo-side brief 代理。
