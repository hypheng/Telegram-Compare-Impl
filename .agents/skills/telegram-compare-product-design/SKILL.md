---
name: telegram-compare-product-design
description: Use when working on framework-agnostic product design for this Telegram comparison project, especially requirements, UX flows, domain models, capability maps, acceptance criteria, or when the user asks for 先做框架无关设计 before KMP/CJMP implementation.
---

# Telegram Compare Product Design

用于把需求讨论收敛成框架无关的项目资产，确保所有后续实现都围绕同一份产品真相源展开。

## Use This Skill When

- 用户要求先做需求、产品、交互、领域设计
- 要改 `framework-agnostic-spec/`、`framework-agnostic-assets/contracts/`、`framework-agnostic-assets/fixtures/`
- 要给某个 Telegram 功能切片定义验收标准
- 需要决定哪些概念应该在 KMP / CJMP 之间共享

## Workflow

1. 先读取 `framework-agnostic-spec/README.md` 和相关目标文件。
2. 如果是新切片，先更新 `framework-agnostic-spec/requirements/telegram-mvp.md`。
3. 需要用户路径时，更新 `framework-agnostic-spec/ux/core-flows.md`。
4. 需要状态或实体时，更新 `framework-agnostic-spec/domain/domain-map.md`。
5. 最后把切片登记到 `framework-agnostic-assets/evaluation/parity-matrix.md`。

## Rules

- 不要先写 KMP / CJMP 代码
- 不要把框架专有状态容器写进领域定义
- 输出要能直接被双框架实现消费
- 如果需求还不清晰，先把假设写进文档，而不是跳到代码

## References

- 导航与落点: `references/navigation.md`
