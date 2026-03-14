---
name: telegram-compare-kmp-delivery
description: Use when implementing or planning the KMP side of this Telegram comparison project, especially Android-first KMP modules, Gradle setup, shared-domain/shared-data boundaries, or Compose-based UI work derived from the framework-agnostic spec.
---

# Telegram Compare KMP Delivery

用于把 framework-agnostic spec 转成 KMP 实现任务，优先支持 Android-first 落地。

## Use This Skill When

- 修改 `apps/kmp/`
- 需要把切片转成 KMP 模块设计
- 需要明确 Android / iOS 壳和 shared-domain / shared-data 边界
- 需要先判断 KMP 环境是否可执行

## Workflow

1. 先读取 `framework-agnostic-spec/` 中对应切片。
2. 读取 `framework-agnostic-assets/evaluation/parity-matrix.md`。
3. 跑 `bash ./.agents/setup/check-kmp-env.sh`。
4. 跑 `bash ./scripts/check-kmp-project.sh`，确认 wrapper、模块和 runbook 已就位。
5. 打开 `apps/kmp/docs/ai-workflow.md` 和 `apps/kmp/docs/module-map.md`。
6. 如有真实执行，先在 `framework-agnostic-assets/evaluation/ai-delivery-logs/` 建 run log。
7. 如果环境不齐，先记录阻塞。
8. 如果环境齐，按 Android-first 顺序推进:
   - shared-domain
   - shared-data
   - android shell
   - ios shell

## Rules

- 不要在环境缺失时伪造可构建工程
- KMP 共享层优先承载业务逻辑，不要过早共享所有 UI
- 每次推进都要回写 parity matrix
- Android app module 使用独立 `com.android.application` 壳，不把 app 壳和 KMP library 混在一起
- KMP library 优先使用 `com.android.kotlin.multiplatform.library`

## References

- `references/android-first.md`
