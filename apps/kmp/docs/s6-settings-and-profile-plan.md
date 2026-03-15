# S6 KMP Delivery Plan

- Slice: `S6 设置与个人资料`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s6-settings-and-profile.md`
- UI Design: `framework-agnostic-spec/interface-design/s6-settings-and-profile.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S6-settings-repo-handoff-brief.md`

## Goal

让 KMP 的 `Settings` tab 成为真实可验收页面，覆盖资料摘要、偏好项切换与冷启动恢复，并把 logout 从聊天页调试区迁移到正式设置入口。

## Delivery Order

1. `shared-domain`
   - 新增 profile / preference 模型
   - 新增 load / toggle settings use cases
2. `shared-data`
   - 新增本地设置 repository 和 Android SharedPreferences 适配
   - 修正 session clear 与 settings/snapshot 的 key 边界
3. `androidApp`
   - 新增 `Settings` screen state 与 bottom navigation 跳转
   - 新增 profile hero、preference rows、logout action
4. `evaluation`
   - 共享测试、构建、设备验收
   - acceptance、AI log、parity

## Exit Criteria

1. `shared-domain` / `shared-data` 测试通过
2. `androidApp:assembleDebug` 通过
3. 设备侧走通 settings open、preference toggle、cold-start persistence、logout from settings
4. `framework-agnostic-assets/evaluation/acceptance-reports/S6-settings-and-profile.md` 回写为 `accepted`

## Outcome

1. `shared-domain` / `shared-data` 测试已通过
2. `androidApp:assembleDebug` 与 `:androidApp:installDebug` 已通过
3. 设备侧已走通 settings open、notifications toggle、cold-start persistence、logout from settings
4. `framework-agnostic-assets/evaluation/acceptance-reports/S6-settings-and-profile.md` 已回写为 `accepted`
