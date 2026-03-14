# Visual System

## 目标

建立一个可以同时指导 KMP 与 CJMP 实现的视觉语言，而不是先绑死某个平台控件库。

## Foundations

### Typography

- 定义 display / title / body / label 四级文本体系
- 明确 light / dark 模式下的层级对比
- 支持动态字体缩放

### Color

- 定义 semantic colors:
  - `surface`
  - `surfaceVariant`
  - `primary`
  - `secondary`
  - `error`
  - `success`
  - `warning`
- 不直接在业务组件里写死颜色值

### Spacing

- 使用统一 spacing scale，例如 `4, 8, 12, 16, 24, 32`
- 页面、列表项、输入区使用同一套间距语义

### Shape

- 定义消息气泡、卡片、按钮、底部面板的圆角层级

### Motion

- 仅在关键切换使用动效:
  - screen transition
  - list refresh
  - message sending
  - AI overlay reveal
- 动效需要提供 reduced-motion fallback

## Component Primitives

- top app bar
- bottom navigation
- list row
- avatar
- message bubble
- input composer
- inline banner
- skeleton block
- bottom sheet / overlay

