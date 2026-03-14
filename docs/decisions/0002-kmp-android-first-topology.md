# ADR 0002: KMP 采用 Android App Shell 加 KMP Shared Libraries

## Status

accepted

## Context

本仓库需要为 KMP 路线提供可被 AI 稳定消费的工程骨架。

Android 官方在 2026-03 的 KMP 文档里已经明确:

- KMP library module 应使用 `com.android.kotlin.multiplatform.library`
- Android application 应拆成独立模块
- `com.android.application` 和 KMP library 不应再沿用旧式混合结构

如果仓库没有显式模块边界，AI 很容易:

- 把 Android-specific 代码误放到共享层
- 把 build 问题和业务问题混在一起
- 重复返工

## Decision

`apps/kmp/` 采用如下拓扑:

- `shared-domain`
- `shared-data`
- `androidApp`
- `iosApp` 计划位

并补充:

- Gradle wrapper
- KMP doctor scripts
- AI delivery runbook
- KMP-specific prompts

## Consequences

好处:

- AI 的模块边界更清晰
- Android-first 调试路径更短
- 更符合 2026 年官方 KMP / Android plugin 约束

代价:

- iOS 壳不会在第一步就存在
- 仍需后续补真实业务切片
