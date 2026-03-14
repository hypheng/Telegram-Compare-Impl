---
name: telegram-compare-ui-design
description: Use when designing or refining the mobile UI and interaction layer for this Telegram comparison project, especially screen inventory, wireframes, stateful interaction design, visual system decisions, Figma handoff preparation, or before implementing a new screen in KMP or CJMP.
---

# Telegram Compare UI Design

用于在实现前把 mobile UI、交互状态和设计交付定义清楚。

## Use This Skill When

- 要新增或修改页面、组件、导航或动效
- 要把功能切片转成可实现的 screen inventory 和状态矩阵
- 要做 Figma handoff 或把设计转成开发任务
- 要审查某个页面的 loading / empty / error / success 设计是否完整

## Workflow

1. 先读取 `framework-agnostic-spec/requirements/telegram-mvp.md` 和 `ux/core-flows.md`。
2. 更新 `framework-agnostic-spec/interface-design/screen-inventory.md`。
3. 更新 `framework-agnostic-spec/interface-design/interaction-states.md`。
4. 如有新的视觉语言或组件原则，更新 `framework-agnostic-spec/interface-design/visual-system.md`。
5. 如果已有设计稿，把链接、截图和 review 结论记录到 `framework-agnostic-assets/design-evidence/`。
6. 设计 ready 后，再交给 KMP / CJMP 实现技能。

## Rules

- 先定义信息层级和状态，再定义视觉
- 不能只设计 happy path
- 设计必须考虑 Android / iOS 触控、系统返回、无障碍和 reduced motion
- AI overlay 必须作为独立层，不破坏聊天主路径

## References

- `references/mobile-ui-workflow.md`
- `references/figma-handoff.md`

