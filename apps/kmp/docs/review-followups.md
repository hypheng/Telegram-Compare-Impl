# KMP Review Follow-ups

Updated: 2026-03-15

## Addressed in the PR #1 follow-up update

### MainActivity decomposition

- [MainActivity.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivity.kt) now acts as the flow coordinator.
- Per-screen UI builders moved to [MainActivityScreenBuilders.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivityScreenBuilders.kt) for `Login`, `ChatList`, `ChatDetail`, `Search`, and `Settings`.
- Remaining note:
  - state and routing are still coordinated inside `MainActivity`; a true presenter / ViewModel layer is optional future cleanup, not a blocker for the demo comparison shell.

### Repository contract split

- [InMemoryChatRepository.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-data/src/commonMain/kotlin/com/telegram/compare/kmp/shareddata/InMemoryChatRepository.kt) no longer exposes one god repository implementing four contracts.
- The file now provides:
  - `InMemoryChatFixtureBundle`
  - `InMemoryChatDebugController`
  - per-contract repositories for chat list, chat detail, search, and sync

### UI presentation data in shared domain

- Avatar and media color styling were removed from [ChatModels.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-domain/src/commonMain/kotlin/com/telegram/compare/kmp/shareddomain/ChatModels.kt).
- Android presentation styling is now derived locally in [MainActivityScreenBuilders.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/androidApp/src/main/kotlin/com/telegram/compare/kmp/android/MainActivityScreenBuilders.kt).

### Release-safe auth wiring

- [DemoSessionRepository.kt](/Users/haifengsong/code-base/telegram/Telegram-Compare-Impl/apps/kmp/shared-data/src/commonMain/kotlin/com/telegram/compare/kmp/shareddata/DemoSessionRepository.kt) now takes `demoAuthEnabled`.
- `MainActivity` wires demo auth only when the Android app is debuggable.
- Remaining note:
  - if the project ever introduces a non-demo auth path, this should become a build-variant or dependency-injection decision instead of one repository flag.
