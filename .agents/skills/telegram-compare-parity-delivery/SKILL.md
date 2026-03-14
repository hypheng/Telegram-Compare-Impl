---
name: telegram-compare-parity-delivery
description: Use when planning, implementing, or reviewing KMP and CJMP work for the same Telegram feature slice, especially when updating apps/kmp, apps/cjmp, or the shared parity matrix and the user wants strict 双实现对齐.
---

# Telegram Compare Parity Delivery

用于让 KMP 和 CJMP 围绕同一个切片同步推进，并持续留下可比较的工程与 AI 证据。

## Use This Skill When

- 修改 `apps/kmp/` 或 `apps/cjmp/`
- 需要规划某个功能切片的双实现顺序
- 需要做 parity review
- 需要判断双实现是否已经偏离

## Workflow

1. 先读取 `framework-agnostic-spec/` 下该切片的需求、流程和领域定义。
2. 查看 `framework-agnostic-assets/evaluation/parity-matrix.md` 当前状态。
3. 对 KMP 与 CJMP 分别给出:
   - 实现入口
   - 阻塞项
   - 风险
   - 下一步
4. 更新 parity matrix 和说明。

## Rules

- 不能把一边实现当成另一边的真实完成度
- 如果某一侧工具链缺失，必须显式记录
- 先保切片闭环，再追求抽象复用
- 所有“AI 生成很顺 / 很卡”的观察都要留痕

## References

- 证据要求: `references/evidence.md`
