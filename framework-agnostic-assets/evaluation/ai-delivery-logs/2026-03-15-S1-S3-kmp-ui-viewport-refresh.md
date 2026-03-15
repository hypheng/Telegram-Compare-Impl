# 2026-03-15 S1-S3 KMP UI Viewport Refresh

- Scope: `S1 登录与会话恢复` + `S2 会话列表` + `S3 单聊详情与文本发送`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 把 `S2` 与 `S3` 的 repo-side design truth source 更新为固定 chrome + 局部滚动的页面约束。
2. 修正 Android 壳结构，让 chat list 和 chat detail 只有中间 viewport 可滚动，不再让整个页面一起滚动。
3. 收紧顶部动作、搜索框、列表行、消息气泡和 composer 的视觉密度，减少 demo panel 感。
4. 扩展 `InMemoryChatRepository` fixture，补足可滚动的聊天列表和消息历史，并增加头像色彩与状态文案差异。
5. 回写 handoff brief，明确默认 fixture 必须足以暴露真实滚动与截断行为。

## Verification

- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`

## AI Friction

- 当前没有可用 `adb` 设备或模拟器连接，因此这轮无法重采 `S1-S3` 的新 UI 截图。
- `MainActivity` 仍是原生 View DSL，大幅改 UI 时需要整段重排而不是局部组件替换，编辑成本高于 Compose。
- Kotlin/Gradle 在并行运行 `allTests` 和 `assembleDebug` 时会命中 build cache 竞争，因此本轮验证改为串行执行。

## Outcome

- `S1-S3` 的 KMP UI 约束已从“整页滚动”修正为“固定头尾 + 中间 viewport 滚动”
- richer fixture data 已进入共享 demo repository
- 代码和构建验证已通过
- fresh device evidence 仍待补采
