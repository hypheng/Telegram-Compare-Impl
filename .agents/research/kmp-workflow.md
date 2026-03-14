# KMP AI Workflow Research

日期: 2026-03-14

## 官方依据

- Android 官方 KMP 环境文档建议用 Kotlin Multiplatform wizard 创建项目，并选择 `Do not share UI` 保持原生 UI。
  - 来源: https://developer.android.com/kotlin/multiplatform/setup
- Android 官方说明 `com.android.kotlin.multiplatform.library` 是 KMP library module 的官方 Android target plugin，并建议把 Android application 拆成单独模块。
  - 来源: https://developer.android.com/kotlin/multiplatform/plugin
- 同一文档说明，从 AGP `8.12.0` 起应优先使用 `kotlin { android { ... } }`，而不是旧的 `androidLibrary {}`。
  - 来源: https://developer.android.com/kotlin/multiplatform/plugin
- Android 官方 built-in Kotlin 文档说明，AGP `9.0` 起 Android app module 默认启用 built-in Kotlin，不再需要 `org.jetbrains.kotlin.android`。
  - 来源: https://developer.android.com/build/migrate-to-built-in-kotlin

## 对本仓库的推导

### 1. KMP 轨道应拆成 app shell + shared libraries

因为官方已经不建议把 KMP library 和 Android application 混在同一个 Android plugin 结构里，所以本仓库应采用:

- `androidApp`
- `shared-domain`
- `shared-data`

### 2. UI 先保留 Android 原生壳

对比目标是工程能力和 AI 开发效率，不是强行共享 UI。所以当前先把 UI 放在 Android 壳层，等产品切片稳定后再评估 UI 共享。

### 3. AI 需要稳定的模块边界和命令入口

因此仓库里应显式提供:

- wrapper
- module map
- debug runbook
- AI prompts
- project doctor scripts
