# Design Delivery Workflow

## Workflow

1. 明确功能切片
   - 来源: `requirements/telegram-mvp.md`
2. 画 screen inventory
   - 把涉及的页面、状态、边界条件列清楚
3. 先做 low-fidelity wireframe
   - 关注信息层级和操作路径
4. 再做 interaction states
   - 把 loading / empty / error / success 补齐
5. 再做 visual system 映射
   - 字体、色彩、间距、shape、motion
6. 做 prototype / review
   - 优先验证核心路径和失败路径
7. 做 developer handoff
   - 标记 ready for dev
   - 输出关键页面链接、节点链接、截图、注释
8. 回写到 parity matrix

## Figma Recommended Workflow

如果使用 Figma:

1. 用设计文件维护线框、高保真和 prototype
2. 用 Dev Mode 标记 ready for dev
3. 通过 MCP 把 frame / node URL 提供给 Agent
4. 如果 design system 稳定，再用 Code Connect 把设计组件映射到代码组件

## Required Handoff Payload

- screen / node link
- 状态说明
- 尺寸与间距约束
- 关键 token
- 动效说明
- 交互注释
- accessibility notes

