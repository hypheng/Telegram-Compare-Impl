# S2 KMP Delivery Plan

- Slice: `S2 会话列表`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s2-chat-list.md`
- UI Design: `framework-agnostic-spec/interface-design/s2-chat-list.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S2-chat-list-repo-handoff-brief.md`

## Goal

把当前 `S1` 的 `Chat List Entry Shell` 升级成真实可验收的 chat list，覆盖 loading、default、search、refresh、empty、error 六类主要状态，并保留进入 `S3` 的承接提示。

## Delivery Order

1. `shared-domain`
   - 补 chat list query / result 语义
   - 补 load / refresh use cases
2. `shared-data`
   - 扩展 in-memory chat repository
   - 支持 search、refresh 和 demo scenario 切换
3. `androidApp`
   - 替换 `Chat List Entry Shell`
   - 实现 Telegram 风格顶部、搜索框、列表行、底部导航和状态页
4. `evaluation`
   - 设备截图、acceptance、AI log、parity

## Scope Notes

- 继续保持 Android-first。
- 真实网络和分页不进入 `S2`。
- `S2` 的原始出口要求只是承接到下一步；当前仓库内已由 `S3` 提供真实详情页。
- 由于真实 Figma 文件还没补齐，本轮以 repo-side handoff brief 作为实现代理，并在 evidence 中显式记录。

## Exit Criteria

1. `shared-domain` 和 `shared-data` 测试通过
2. `androidApp:assembleDebug` 通过
3. 设备侧至少验收 default、search、refresh、empty、error、entry-to-detail 六类状态
4. `framework-agnostic-assets/evaluation/acceptance-reports/S2-chat-list.md` 回写为 `accepted`

## Verification Summary

- `:shared-domain:allTests`
- `:shared-data:allTests`
- `:androidApp:assembleDebug`
- `:androidApp:installDebug`
- emulator evidence in `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/`
