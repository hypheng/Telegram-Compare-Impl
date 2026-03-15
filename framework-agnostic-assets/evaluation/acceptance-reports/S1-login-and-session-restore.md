# S1 Acceptance Report

- Slice: `S1 登录与会话恢复`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- UX Flow: `framework-agnostic-spec/ux/core-flows.md`
- UI Design: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Domain Notes: `framework-agnostic-spec/domain/domain-map.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md`
- Acceptance Evidence: `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md`

## Acceptance Checklist

| Criterion | Status | Evidence | Notes |
|---|---|---|---|
| AC-S1-1 启动反馈 | accepted | `apps/kmp/androidApp/.../MainActivity.kt` restoring state + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md` | 在模拟器冷启动和 `重试恢复` 路径上都观察到 restoring 反馈；由于该状态仅持续 `650ms`，未能稳定截到单帧静态图 |
| AC-S1-2 无会话进入登录 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-no-session.png` | 冷启动无会话直接进入登录页 |
| AC-S1-3 失效会话回退 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-debug-seeded.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-restore-failed.png` | 先写入失效会话，再冷启动得到明确错误提示并回到登录页 |
| AC-S1-4 登录成功进入主壳 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png` | 历史证据截图采集于 `S2` 落地前；当前实现已直接衔接真实 chat list，但 `S1` 只要求能成功进入主壳 |
| AC-S1-5 会话可恢复 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-restored.png` | 登录成功后冷启动应用，恢复到同一会话 |
| AC-S1-6 登出清理状态 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-logged-out.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-after-logout-cold-start.png` | 登出后回到登录页，随后冷启动也不再自动恢复 |
| S1 Figma backfill brief | implemented | `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md` | 已形成落图说明，但真实 frame/node links 仍待补 |

## Platform Status

| Track | Status | Evidence | Notes |
|---|---|---|---|
| KMP | accepted | `./gradlew :shared-domain:allTests :shared-data:allTests`、`./gradlew :androidApp:assembleDebug`、`./gradlew :androidApp:installDebug`、`framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md` | 构建和模拟器手动验收均完成 |
| CJMP | deferred | `apps/cjmp/docs/s1-login-session-restore-plan.md`、`bash ./.agents/setup/check-cjmp-env.sh` | 当前阶段明确不要求闭环 CJMP；保留切片级 bootstrap 计划供后续恢复对齐时使用 |
| Figma handoff | blocked | `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md`、`framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-15-S1-design-backfill.md` | 当前只有 repo-side brief；缺少可编辑 Figma 文件和真实 frame/node links |

## Tests And Verification

- Commands:
  - `bash ./.agents/setup/check-codex-ai-infra.sh`
  - `bash ./.agents/setup/check-kmp-env.sh`
  - `bash ./scripts/check-kmp-project.sh`
  - `bash ./scripts/kmp-doctor.sh`
  - `bash ./.agents/setup/check-cjmp-env.sh`
  - `cd apps/kmp && ./gradlew :shared-domain:allTests :shared-data:allTests`
  - `cd apps/kmp && ./gradlew :androidApp:assembleDebug`
  - `cd apps/kmp && ./gradlew :androidApp:installDebug`
  - `adb devices -l`
  - `adb shell am start -n com.telegram.compare.kmp.android/.MainActivity`
  - `adb shell uiautomator dump ...`
  - `adb shell screencap -p ...`
- Result:
  - Codex repo-local infra ready
  - KMP env/project self-check ready
  - CJMP env self-check ready
  - shared tests passed
  - `androidApp` debug 包构建并安装成功
  - 模拟器上完成了无会话登录、登录成功、冷启动恢复、失效会话回退、登出、登出后冷启动六条路径验收
- Screenshots / videos:
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-no-session.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-restored.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-debug-seeded.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-restore-failed.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-logged-out.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-after-logout-cold-start.png`
- Fixtures / mocks:
  - `DemoSessionRepository`
  - `PreferencesSessionStorage`
  - `InMemoryChatRepository`

## AI Delivery Summary

- Latest log: `framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-15-S1-cjmp-scope-deferral.md`
- Total sessions: `4`
- Human interventions: `2`
- Open issues:
  - `S1` 还缺真实 Figma frame/node handoff evidence

## Risks

- `S1` 的历史 acceptance evidence 采集于 `S2/S3` 落地前，和当前 UI 形态并不完全一致，但不会影响 `S1` 的验收结论。
- `S1` 的 Figma backfill 目前只有 brief，没有真实 frame/node links。
- 如果后续重新要求双实现对比，CJMP 仍需从切片计划推进到 repo-local 工程和运行证据。

## Next Step

- 提供一个可编辑的 Figma 文件 / page，把 `2026-03-15-S1-figma-backfill-brief.md` 回填成真实 frame/node links。
