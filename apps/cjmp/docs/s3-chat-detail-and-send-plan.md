- Slice: `S3 单聊详情与文本发送`
- Status: `in_progress`
- Last Updated: `2026-03-16`
- Product Spec: `framework-agnostic-spec/requirements/s3-chat-detail-and-send.md`
- UI Design: `framework-agnostic-spec/interface-design/s3-chat-detail-and-send.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S3-chat-detail-repo-handoff-brief.md`

## Goal

把 `S2` 的 entry placeholder 升级成真实 `S3` 单聊详情页，覆盖消息历史、发送中、失败重试与返回列表承接，并生成 CJMP 设备侧证据。

## Current Snapshot

- CJMP 已接入 `S3` 详情页基础壳：
  - 固定顶部栏 + 状态行 + 消息流滚动区 + composer 固定区
  - 支持 debug-only 触发 loading / error / failed / retrying
- 当前 review follow-ups 已登记到 `apps/cjmp/docs/review-followups.md`，优先解决发送态与返回列表承接的一致性问题
- 设备侧 evidence 需要补齐：
  - detail loading / ready
  - send success / failed / retrying
  - return-to-list preview 回写

## Delivery Order

1. `framework-agnostic-spec` 更新交付假设与 UI 设计中的 debug 收起规则
2. `apps/cjmp` 实现 detail repo、状态机与 UI
3. device validation：截图与状态证据
4. `evaluation`：AI log + parity matrix

## Scope Notes

- 继续保持 app-only 结构，不新增 `business/` 或 `logic/` 模块。
- demo 允许 debug-only 控件触发发送失败与重试，不计入最终产品交互。

## Exit Criteria

1. `apps/cjmp/telegram_compare_app` 可以构建并运行
2. 从 `S2` 列表进入真实详情页，非 placeholder
3. `S3` 的 loading / ready / sending / failed / retrying 形成设备证据
4. 返回列表后会话预览与排序更新

## Open TODO

- `apps/cjmp/docs/review-followups.md`
  - 先收敛 review 中暴露的 `preview overwrite`、`pending send`、`message rendering`、`thread reopen` 四个问题
  - 完成后再补设备证据与 AI delivery log

## Verification Snapshot

- `cd apps/cjmp/telegram_compare_app && keels build apk --platform android-arm64 --debug`
- `adb -s emulator-5554 install -r android/app/build/outputs/apk/debug/app-debug.apk`
- `adb -s emulator-5554 shell am start -n com.example.telegram_compare_cjmp/.EntryEntryAbilityActivity`
- `adb -s emulator-5554 exec-out screencap -p`
