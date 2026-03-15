# S7 KMP Delivery Plan

- Slice: `S7 媒体消息`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s7-media-messages.md`
- UI Design: `framework-agnostic-spec/interface-design/s7-media-messages.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S7-media-repo-handoff-brief.md`

## Goal

让 KMP 的 `Chat Detail` 支持图片消息展示与发送，在不破坏 `S3` 文本发送路径的前提下补齐 Telegram 类聊天的核心非文本体验。

## Delivery Order

1. `shared-domain`
   - 为 `Message` 增加媒体负载模型
   - 新增 media picker / send media use cases
2. `shared-data`
   - 扩展 fixture 数据，加入媒体消息与可发送媒体列表
   - 扩展 snapshot 持久化
3. `androidApp`
   - 渲染媒体消息 bubble
   - 新增底部 media picker sheet 与发送入口
4. `evaluation`
   - 共享测试、构建、设备验收
   - acceptance、AI log、parity

## Exit Criteria

1. `shared-domain` / `shared-data` 测试通过
2. `androidApp:assembleDebug` 通过
3. 设备侧走通 media render、picker open、media send、cold-start restore with media
4. `framework-agnostic-assets/evaluation/acceptance-reports/S7-media-messages.md` 回写为 `accepted`

## Outcome

1. `shared-domain` / `shared-data` 测试已通过
2. `androidApp:assembleDebug` 与 `:androidApp:installDebug` 已通过
3. 设备侧已走通 thread with media、picker open、media send、cold-start restore with media
4. `framework-agnostic-assets/evaluation/acceptance-reports/S7-media-messages.md` 已回写为 `accepted`
