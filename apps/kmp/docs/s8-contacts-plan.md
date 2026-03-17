# S8 KMP Delivery Plan

- Slice: `S8 Contacts 联系人`
- Status: `accepted`
- Last Updated: `2026-03-16`
- Product Spec: `framework-agnostic-spec/requirements/s8-contacts.md`
- UI Design: `framework-agnostic-spec/interface-design/s8-contacts.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-16-S8-contacts-repo-handoff-brief.md`

## Goal

让 KMP 主壳具备真实 `Contacts` 路径，支持浏览联系人、搜索联系人、打开已有聊天、创建新聊天，并在返回与冷启动恢复后保持联系人和聊天列表的一致性。

## Delivery Order

1. `shared-domain`
   - 定义 `ContactSummary`、contacts load/open chat use cases、repository 边界
2. `shared-data`
   - 为 fixture store 增加联系人数据、打开联系人聊天逻辑和联系人场景切换
   - 让 snapshot 恢复联系人到聊天的映射状态
3. `androidApp`
   - 把底部 `Contacts` tab 接成真实页面
   - 接通联系人搜索、已有聊天进入、新聊天创建和返回刷新
4. `evaluation`
   - 共享测试、构建、设备验收
   - acceptance、AI log、parity

## Exit Criteria

1. `shared-domain` / `shared-data` 测试通过
2. `androidApp:assembleDebug` 与 `:androidApp:installDebug` 通过
3. 设备侧走通联系人 root、搜索、空态、错误恢复、已有聊天进入、新聊天创建、返回刷新和 chats 回写
4. `framework-agnostic-assets/evaluation/acceptance-reports/S8-contacts.md` 回写为 `accepted`

## Outcome

1. `shared-domain` / `shared-data` 测试已通过
2. `androidApp:assembleDebug` 与 `:androidApp:installDebug` 已通过
3. 设备侧已走通联系人 root、existing chat、新聊天、聊天列表回写、搜索命中、搜索空态、错误恢复和冷启动后的联系人一致性
4. `framework-agnostic-assets/evaluation/acceptance-reports/S8-contacts.md` 已回写为 `accepted`
