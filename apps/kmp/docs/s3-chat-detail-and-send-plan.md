# S3 KMP Delivery Plan

- Slice: `S3 单聊详情与文本发送`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s3-chat-detail-and-send.md`
- UI Design: `framework-agnostic-spec/interface-design/s3-chat-detail-and-send.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S3-chat-detail-repo-handoff-brief.md`

## Goal

把 `S2` 的聊天进入承接从 placeholder 升级成真实聊天详情，覆盖 detail loading、message history、text send、failed send、retry 和返回列表后的预览回写。

## Delivery Order

1. `shared-domain`
   - 补 chat detail、send、retry 语义
   - 补 detail / send / retry use cases
2. `shared-data`
   - 扩展 in-memory chat repository
   - 支持 detail load、send failure 和 retry
3. `androidApp`
   - 用真实聊天详情替换 placeholder
   - 实现 Telegram 风格顶部、消息流、composer 和失败重试反馈
4. `evaluation`
   - 设备截图、acceptance、AI log、parity

## Scope Notes

- 继续保持 Android-first。
- 真实网络、消息分页和媒体不进入 `S3`。
- 失败路径通过 debug-only 控件强制覆盖。
- 由于真实 Figma 文件还没补齐，本轮继续以 repo-side handoff brief 作为实现代理，并在 evidence 中显式记录。

## Exit Criteria

1. `shared-domain` 和 `shared-data` 测试通过
2. `androidApp:assembleDebug` 通过
3. 设备侧至少验收 detail load、send success、send failed、retry、return to list 五类状态
4. `framework-agnostic-assets/evaluation/acceptance-reports/S3-chat-detail-and-send.md` 回写为 `accepted`

## Verification Summary

- `:shared-domain:allTests`
- `:shared-data:allTests`
- `:androidApp:assembleDebug`
- `:androidApp:installDebug`
- emulator evidence in `framework-agnostic-assets/evaluation/acceptance-evidence/s3-chat-detail-and-send/`
