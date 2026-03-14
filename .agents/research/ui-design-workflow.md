# Mobile UI Design Workflow Research

日期: 2026-03-14

## 官方依据

### Apple HIG

- Apple Human Interface Guidelines 强调 clear visual hierarchy、consistency 和 platform conventions。
  - 来源: https://developer.apple.com/design/human-interface-guidelines/
- `Designing for iOS` 强调手机场景下的 ergonomics 和 iPhone 使用模式。
  - 来源: https://developer.apple.com/design/human-interface-guidelines/designing-for-ios

### Android / Compose / Material 3

- Android 官方说明 Compose 设计系统应通过 theming、components 和 design system 统一应用观感。
  - 来源: https://developer.android.com/develop/ui/compose/designsystems
- Material 3 in Compose 明确把 color, typography, shapes 作为主题系统的核心子系统，并强调 motion、navigation、accessibility。
  - 来源: https://developer.android.com/develop/ui/compose/designsystems/material3

### Figma Dev Mode / MCP / Code Connect

- Figma MCP 官方说明 remote server 可用于所有 seats and plans；desktop server 需要 Dev 或 Full seat 且需要 paid plan。
  - 来源: https://help.figma.com/hc/en-us/articles/32132100833559-Guide-to-the-Figma-MCP-server
- 同一文档说明 Figma MCP 适合:
  - 获取 design context
  - 从 frame 生成代码
  - 通过 Code Connect 保持设计系统组件一致
- Figma 也明确 Dev Mode 侧重 handoff、inspection、annotation 和 codegen，且 Dev Mode seat 属于 paid plans。
  - 来源: https://help.figma.com/hc/en-us/articles/35498519152663-Figma-MCP-collection-Dev-Mode-fundamentals-old-UI

## 对本仓库的推导

### 1. UI design 必须是独立层

原因:

- Apple 强调平台一致性和层级
- Android/Compose 强调 design system foundations
- 这两类要求都不能只靠需求文档和代码实现阶段临时补

所以仓库里需要单独保存:

- screen inventory
- interaction states
- visual system
- design handoff evidence

### 2. 设计交付应分两层

- 仓库内的 markdown 真相源
  - 便于 diff、review、和实现关联
- Figma / prototype / Dev Mode
  - 便于视觉设计、原型、handoff 和 inspect

两层不能互相替代。

### 3. Agent 不应直接从功能描述生成 UI

更稳的顺序是:

1. scope / flow
2. screen inventory
3. low-fi wireframe
4. interaction states
5. visual system
6. prototype / review
7. handoff
8. implementation
