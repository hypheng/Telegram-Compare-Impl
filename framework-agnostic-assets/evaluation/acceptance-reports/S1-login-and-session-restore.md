# S1 Acceptance Report

- Slice: `S1 登录与会话恢复`
- Status: `accepted`
- Last Updated: `2026-03-15`
- Product Spec: `framework-agnostic-spec/requirements/s1-login-session-restore.md`
- UX Flow: `framework-agnostic-spec/ux/core-flows.md`
- UI Design: `framework-agnostic-spec/interface-design/s1-login-session-restore.md`
- Domain Notes: `framework-agnostic-spec/domain/domain-map.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md`
- Acceptance Evidence: `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md`, `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/README.md`

## Acceptance Checklist

| Criterion | Status | Evidence | Notes |
|---|---|---|---|
| AC-S1-1 启动反馈 | accepted | `apps/kmp/androidApp/.../MainActivity.kt` restoring state + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/startup-feedback.png` | KMP 在 restoring 分支上完成验收；CJMP 因自渲染 restoring 文案过短，使用 splash / loading 反馈和冷启动实测作为等价证据 |
| AC-S1-2 无会话进入登录 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-no-session.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-no-session.png` | KMP 与 CJMP 均在无会话冷启动时直接进入登录页 |
| AC-S1-3 失效会话回退 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-debug-seeded.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-restore-failed.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-expired-seeded.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-restore-failed.png` | KMP 与 CJMP 均验证了“写入失效快照 -> 冷启动 -> 明确错误 -> 回到登录页” |
| AC-S1-4 登录成功进入主壳 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-after-login.png` | KMP 与 CJMP 均已在设备上进入 `Chat List Entry Shell` |
| AC-S1-5 会话可恢复 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-restored.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-restored.png` | KMP 与 CJMP 均验证了登录成功后冷启动恢复同一用户会话 |
| AC-S1-6 登出清理状态 | accepted | `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-logged-out.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-after-logout-cold-start.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-after-logout.png` + `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-after-logout-cold-start.png` | KMP 与 CJMP 均验证了登出后回到登录页，且后续冷启动不再自动恢复 |
| S1 Figma backfill brief | implemented | `framework-agnostic-assets/design-evidence/2026-03-15-S1-figma-backfill-brief.md` | 已形成落图说明，但真实 frame/node links 仍待补 |

## Platform Status

| Track | Status | Evidence | Notes |
|---|---|---|---|
| KMP | accepted | `./gradlew :shared-domain:allTests :shared-data:allTests`、`./gradlew :androidApp:assembleDebug`、`./gradlew :androidApp:installDebug`、`framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/README.md` | 构建和模拟器手动验收均完成 |
| CJMP | accepted | `apps/cjmp/docs/s1-login-session-restore-plan.md`、`framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/README.md`、`apps/cjmp/telegram_compare_app/lib/session.cj`、`keels build apk --platform android-arm64 --debug`、`keels run --debug -d emulator-5554` | `CJMP` 为自渲染页面，`uiautomator dump` 只暴露单个根 `android.view.View`；当前使用 app 内会话实现、截图、`run-as` 快照检查、构建日志和 root-only XML 作为等价证据完成验收 |
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
  - `cd apps/cjmp/telegram_compare_app && keels run --debug -d emulator-5554`
  - `adb devices -l`
  - `adb shell am start -n com.telegram.compare.kmp.android/.MainActivity`
  - `adb shell am start -n com.example.telegram_compare_cjmp/.EntryEntryAbilityActivity`
  - `adb shell input touchscreen tap ...`
  - `adb shell run-as com.example.telegram_compare_cjmp sh -c 'find . -maxdepth 4 -name "session.snapshot"'`
  - `adb shell uiautomator dump ...`
  - `adb shell screencap -p ...`
- Result:
  - Codex repo-local infra ready
  - KMP env/project self-check ready
  - CJMP env self-check ready
  - shared tests passed
  - KMP `androidApp` debug 包构建并安装成功
  - CJMP `keels` app 构建、安装和启动成功
  - 模拟器上完成了 KMP 和 CJMP 两侧的无会话登录、登录成功、冷启动恢复、失效会话回退、登出、登出后冷启动六条路径验收
- Screenshots / videos:
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-no-session.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-after-login.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-restored.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/home-debug-seeded.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-restore-failed.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-logged-out.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/login-after-logout-cold-start.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/startup-feedback.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-after-login.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/home-restored.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-restore-failed.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s1-login-session-restore/cjmp-bootstrap/login-after-logout-cold-start.png`
- Fixtures / mocks:
  - `DemoSessionRepository`
  - `PreferencesSessionStorage`
  - `InMemoryChatRepository`
  - `apps/cjmp/telegram_compare_app/lib/session.cj`

## AI Delivery Summary

- Latest log: `framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-15-S1-cjmp-bootstrap-login-shell.md`
- Total sessions: `8`
- Human interventions: `2`
- Open issues:
  - `S1` 还缺真实 Figma frame/node handoff evidence

## Risks

- `S1` 的 Figma backfill 目前只有 brief，没有真实 frame/node links。
- `CJMP` 当前通过 self-render 等价证据完成验收，后续若要做稳定 UI 自动化仍需额外测试 hook 或图像驱动方案。
- `CJMP` app 的 snapshot 目录目前使用包名私有目录常量，后续若调整 applicationId 需要同步维护。

## Next Step

- 提供一个可编辑的 Figma 文件 / page，把 `2026-03-15-S1-figma-backfill-brief.md` 回填成真实 frame/node links，或继续推进 `CJMP` 的 `S2` 对齐实现。
