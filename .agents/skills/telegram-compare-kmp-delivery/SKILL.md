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
4. 如果环境不齐，先记录阻塞。
5. 如果环境齐，按 Android-first 顺序推进:
   - shared-domain
   - shared-data
   - android shell
   - ios shell

## Rules

- 不要在环境缺失时伪造可构建工程
- KMP 共享层优先承载业务逻辑，不要过早共享所有 UI
- 每次推进都要回写 parity matrix

## References

- `references/android-first.md`
