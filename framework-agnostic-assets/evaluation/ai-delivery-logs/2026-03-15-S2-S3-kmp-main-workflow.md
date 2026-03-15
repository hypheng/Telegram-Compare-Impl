# 2026-03-15 S2-S3 KMP Main Workflow

- Scope: `S2 会话列表` + `S3 单聊详情与文本发送`
- Track: `KMP`
- Date: `2026-03-15`
- Driver: `Codex`

## Delivered

1. 补齐了 `S2` 和 `S3` 的 framework-agnostic requirement、UI design、repo-side handoff 和 acceptance scaffold。
2. 共享层新增了 chat list / chat detail / send / retry 的领域语义与 use cases。
3. `InMemoryChatRepository` 同时支撑 chat list、chat detail、send failure 和 retry。
4. Android 壳从 `S1` 的 entry shell 升级成真实的 `S1 -> S2 -> S3` 主流程。
5. 模拟器上补齐了 `S2` 与 `S3` 的设备侧 acceptance evidence。

## Verification

- `bash ./.agents/setup/check-kmp-env.sh`
- `bash ./scripts/check-kmp-project.sh`
- `bash ./scripts/kmp-doctor.sh`
- `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
- `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- `cd apps/kmp && ./gradlew :androidApp:installDebug`
- `adb` emulator manual validation for `S2` and `S3`

## Evidence

- `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/`
- `framework-agnostic-assets/evaluation/acceptance-evidence/s3-chat-detail-and-send/`

## AI Friction

- 真实 Figma MCP 已 ready，但项目仍缺可编辑 Figma 文件，所以 `D2` 仍只能用 repo-side handoff brief 代理。
- 原先 `MainActivity` 已经积累了较多 View DSL 代码，本轮直接继续在原生 View 壳里扩展会比重建 UI 栈更稳。
- 设备自动化里，发送按钮点击坐标需要基于 UI dump 反复收敛，不能靠肉眼估计。

## Outcome

- `S2` on KMP: `accepted`
- `S3` on KMP: `accepted`
- `S1-S3` 的 KMP 主用户工作流已闭环
- `CJMP` 仍按当前项目范围保持 `deferred`
