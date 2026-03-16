# 2026-03-16 S8 KMP Contacts Acceptance

- Scope: `S8 Contacts 联系人`
- Track: `KMP`
- Date: `2026-03-16`
- Driver: `Codex`

## Delivered

1. 为 framework-agnostic spec 新增 `S8` 的 requirement 与 UI design，并把 `S8` 登记到 MVP、UX flow、screen inventory、interaction states、domain map、roadmap 和 parity。
2. 为 `shared-domain` 新增联系人模型、contacts load/open chat use cases 和 repository 边界。
3. 为 `shared-data` 的 fixture store 增加联系人列表、搜索、已有聊天进入和新聊天创建逻辑，并补了联系人场景恢复与 snapshot 一致性。
4. 为 Android 壳新增真实 `Contacts` 页，把底部 `Contacts` tab 和顶部 `新聊天` 入口接到联系人主路径。
5. 完成 `S8` 的设备验收，补齐联系人 root、搜索命中、搜索空态、已有聊天进入、新聊天创建、返回刷新、chat list 回写和错误恢复证据。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb -s emulator-5554 shell cmd activity start-activity -n com.telegram.compare.kmp.android/.MainActivity`
- `adb -s emulator-5554 shell input tap ...`
- `adb -s emulator-5554 shell input text ...`
- `adb -s emulator-5554 exec-out screencap -p ...`
- `adb -s emulator-5554 shell uiautomator dump ...`

## Evidence Produced

- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-root.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-existing-chat.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-return.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-search-sam.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-new-chat.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-return-new-chat.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-chat-list-with-contact-chat.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-search-empty.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-error.png`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s8-contacts/s8-contacts-recovered.png`

## AI Friction

- `S8` 加进来后暴露了 `S4` snapshot 模型的空缺: chats 和 threads 会恢复，但 contacts 到 chat 的映射不会，导致 Contacts 和 Chats 状态不一致；这轮补了 snapshot contacts payload 和 backward-compatible fallback。
- 联系人返回路径如果直接复用旧的 `contentState`，会在“新建聊天返回联系人”时回显过期结果；这轮把返回逻辑改成重新加载 contacts 结果，而不是回显旧列表快照。
- 模拟器在 app 重装后偶尔会掉回桌面，需要显式重新拉起 `MainActivity` 才能继续取证。

## Outcome

- `S8` on KMP 已 accepted
- 当前 `S1-S8` 的 KMP 主路径已覆盖联系人创建聊天这条基础 Telegram 能力
- 剩余 backlog 只剩 `S9 AI 助手增强能力`
