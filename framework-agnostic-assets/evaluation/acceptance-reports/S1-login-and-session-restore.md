# S1 Acceptance Report

- Slice: `S1 登录与会话恢复`
- Status: `in-progress`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- UX Flow: `framework-agnostic-spec/ux/core-flows.md`
- UI Design: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Domain Notes: `framework-agnostic-spec/domain/domain-map.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md`

## Acceptance Checklist

| Criterion | Status | Evidence | Notes |
|---|---|---|---|
| AC-S1-1 启动反馈 | implemented | `apps/kmp/androidApp/.../MainActivity.kt` restoring state + `RestoreSessionUseCase` | 待模拟器或真机手动确认首屏反馈节奏 |
| AC-S1-2 无会话进入登录 | implemented | `SessionRestoreResult.NoSession` + login screen rendering | 需补截图证据 |
| AC-S1-3 失效会话回退 | implemented | `DemoSessionRepository.seedExpiredSession()` + `SessionRestoreResult.Failed` | 需在设备上走一遍冷启动失败路径 |
| AC-S1-4 登录成功进入主壳 | implemented | `LoginWithCodeUseCase` + `MainScreenState.Home` | 当前主壳是 `Chat List Entry Shell`，不计入 `S2` 完整验收 |
| AC-S1-5 会话可恢复 | implemented | `PreferencesSessionStorage` + `RestoreSessionUseCase` | 构建通过，待设备重启应用确认 |
| AC-S1-6 登出清理状态 | implemented | `LogoutUseCase` + `MainActivity.logout()` | 待手动回归验证 |
| S1 Figma backfill brief | implemented | `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md` | 已形成落图说明，但真实 frame/node links 仍待补 |

## Platform Status

| Track | Status | Evidence | Notes |
|---|---|---|---|
| KMP | in-progress | `./gradlew :shared-domain:allTests :shared-data:allTests`、`./gradlew :androidApp:assembleDebug` 通过 | 代码和构建基线已完成，待设备验收收尾 |
| CJMP | planned | `framework-agnostic-assets/evaluation/parity-matrix.md` | 本轮先聚焦 KMP 启动真实切片，CJMP 尚未开始实现 |
| Figma handoff | in-progress | `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md` | 已有 backfill brief，缺真实文件链接和 frame/node evidence |

## Tests And Verification

- Commands:
  - `bash ./.agents/setup/check-codex-ai-infra.sh`
  - `bash ./.agents/setup/check-kmp-env.sh`
  - `bash ./scripts/check-kmp-project.sh`
  - `bash ./scripts/kmp-doctor.sh`
  - `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
  - `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
- Result:
  - Codex repo-local infra ready
  - KMP env/project self-check ready
  - shared tests passed
  - `androidApp` debug 包构建成功
- Screenshots / videos:
  - 未采集
- Fixtures / mocks:
  - `DemoSessionRepository`
  - `PreferencesSessionStorage`
  - `InMemoryChatRepository`

## AI Delivery Summary

- Latest log: `framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-15-S1-kmp-login-session-restore.md`
- Total sessions: `1`
- Human interventions: `0`
- Open issues:
  - 还缺设备侧手动验收和截图证据
  - CJMP 同切片实现尚未启动
  - `S1` 还缺真实 Figma frame/node handoff evidence

## Risks

- 当前 `Chat List Entry Shell` 只用于承接 `S1` 成功落点，不能误判为 `S2` 已完成。
- 启动、恢复和失效分支尚未在真实设备上完成视觉验收。
- `S1` 的 Figma backfill 目前只有 brief，没有真实 frame/node links。

## Next Step

- 先根据 `2026-03-15-S1-figma-backfill-brief.md` 在 Figma 中补出 `S1` 的关键 frame 和 prototype，再进行设备侧全链路验收并补截图证据。
