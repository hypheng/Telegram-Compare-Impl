# S5 KMP Delivery Plan

- Slice: `S5 搜索会话 / 消息`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s5-search-chats-and-messages.md`
- UI Design: `framework-agnostic-spec/interface-design/s5-search-chats-and-messages.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S5-search-repo-handoff-brief.md`

## Goal

在 KMP Android 壳中补齐独立 `UI-S5 Search`，让用户能够从 chat list 的当前关键词进入全局搜索结果页，查看 chat hit 和 message hit，并从结果跳到详情页或消息定位状态。

## Delivery Order

1. `shared-domain`
   - 新增 `SearchQuery`、`MessageSearchHit`、`SearchLoadResult`
   - 新增 `SearchRepository` 与 `SearchChatsAndMessagesUseCase`
2. `shared-data`
   - 为 `InMemoryChatRepository` 增加 chat/message 搜索实现
   - 复用现有 `ChatListScenario.ERROR` 作为搜索失败验证入口
3. `androidApp`
   - 新增 `MainScreenState.Search`
   - 在 chat list 中增加“全局搜索”入口
   - 新增 search result screen、chat result jump、message result jump、search return
   - 在详情页增加命中消息预览卡和命中 bubble 高亮
4. `evaluation`
   - 共享测试、构建、设备侧验收、acceptance report、AI log、parity

## Outcome

1. `shared-domain` / `shared-data` 测试已通过
2. `androidApp:assembleDebug` 与 `androidApp:installDebug` 已通过
3. 设备侧已走通 grouped results、chat result jump、message result jump、empty、error
4. `framework-agnostic-assets/evaluation/acceptance-reports/S5-search-chats-and-messages.md` 已回写为 `accepted`
