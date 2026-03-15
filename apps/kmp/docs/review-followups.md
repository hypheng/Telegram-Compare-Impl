# KMP Review Follow-ups

Updated: 2026-03-15

## Deferred from PR #1

### MainActivity decomposition

- Split [MainActivity.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivity.kt) into:
  - a state holder / presenter or ViewModel-like coordinator
  - per-screen UI builders for `Login`, `ChatList`, `ChatDetail`, `Search`, and `Settings`
  - a slimmer navigation/router layer
- Reason deferred:
  - valid structural concern, but too large for the current PR update without turning a targeted slice-delivery branch into a broad refactor branch

### Repository contract split

- Split [InMemoryChatRepository.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-data/src/commonMain/kotlin/com/telegram/compare/kmp/shareddata/InMemoryChatRepository.kt) by responsibility instead of keeping one demo store behind multiple repository interfaces
- Reason deferred:
  - the current branch still uses one in-memory fixture store to compare slices quickly; untangling the contracts is worthwhile but larger than a review follow-up patch

### UI presentation data in shared domain

- Move avatar color styling out of [ChatModels.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-domain/src/commonMain/kotlin/com/telegram/compare/kmp/shareddomain/ChatModels.kt) into a presentation wrapper or dedicated avatar-style model
- Reason deferred:
  - would cascade through shared-domain, shared-data fixtures, snapshot persistence, and Android shell rendering

### Release-safe auth wiring

- Replace the current demo-only [DemoSessionRepository.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-data/src/commonMain/kotlin/com/telegram/compare/kmp/shareddata/DemoSessionRepository.kt) with an explicitly debug/dev-only wiring strategy before any non-demo distribution path
- Reason deferred:
  - current comparison app is still demo-slice delivery infrastructure; this needs product-level build-variant and repository wiring rather than a small patch
