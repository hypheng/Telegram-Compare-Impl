# S4 KMP Delivery Plan

- Slice: `S4 本地缓存与离线恢复`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s4-local-cache-and-offline-restore.md`
- UI Design: `framework-agnostic-spec/interface-design/s4-local-cache-and-offline-restore.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S4-cache-offline-repo-handoff-brief.md`

## Goal

让 KMP 在 `S1-S3` 主路径之上获得本地 snapshot 缓存能力，使 session 恢复后可以直接回到上次的 chat list 或 chat detail，并保留最近 refresh / send / retry 结果。

## Delivery Order

1. `shared-domain`
   - 新增 snapshot route、snapshot model、restore/save/clear use cases
   - 明确 `SyncRepository` 边界
2. `shared-data`
   - 为 `InMemoryChatRepository` 增加 snapshot export / import
   - 新增 snapshot storage 接口和 Android SharedPreferences 适配
3. `androidApp`
   - restoring 后优先尝试恢复 snapshot
   - 在 list / detail 页面展示缓存恢复 banner
   - 增加 debug-only 清空缓存入口
4. `evaluation`
   - 共享测试、构建、设备侧冷启动验证
   - acceptance、AI log、parity

## Outcome

1. `shared-domain` / `shared-data` 测试已通过
2. `androidApp:assembleDebug` 已通过
3. 设备侧已走通 list restore、detail restore、latest message persisted、cache cleared fallback、logout clears cache
4. `framework-agnostic-assets/evaluation/acceptance-reports/S4-local-cache-and-offline-restore.md` 已回写为 `accepted`

## Exit Criteria

1. `shared-domain` / `shared-data` 测试通过
2. `androidApp:assembleDebug` 通过
3. 设备侧走通 list restore、detail restore、cache cleared fallback、logout clears cache
4. `framework-agnostic-assets/evaluation/acceptance-reports/S4-local-cache-and-offline-restore.md` 回写为 `accepted`
