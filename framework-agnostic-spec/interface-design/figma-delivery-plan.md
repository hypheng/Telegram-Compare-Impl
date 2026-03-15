# Figma Delivery Plan

## 目标

把 Figma 从“可选设计工具”提升为项目里的显式交付阶段，用于承接视觉设计、原型、ready-for-dev handoff，以及通过 MCP 把 frame/node 上下文带给 Agent。

这个文档不替代仓库内的 `interface-design/` 文档。两者分工如下:

- 仓库文档: 第一真相源，定义页面、状态、信息层级、低保真结构和约束
- Figma: 第二真相源，定义视觉稿、原型、交互动效、inspect 和 handoff

## 项目级规则

### 必经阶段

每个核心切片都应该经过:

1. `D1` framework-agnostic design
2. `D2` Figma design and handoff
3. `I1/I2` implementation
4. `E1` acceptance and parity

### 特例规则

- `S1` 已经先启动了 KMP 实现，因此需要补做 Figma handoff，属于 backfill 特例。
- 从 `S2` 开始，不再允许把“先写代码后补 Figma”当成默认路径。

## Figma 文件建议结构

建议在一个主文件中按页面或切片组织：

1. `00 Cover`
   - 文件说明
   - 当前里程碑
   - 负责人和日期
2. `01 Foundations`
   - color
   - typography
   - spacing
   - shape
   - motion notes
3. `10 S1 Login / Restore`
   - launch
   - login
   - restore failed
   - chat list entry shell
4. `20 S2 Chat List`
   - default list
   - loading
   - empty
   - refresh
   - error
5. `30 S3 Chat Detail`
   - detail default
   - sending
   - failed
   - retry
6. `90 Prototype`
   - 核心路径原型
7. `99 Ready For Dev`
   - 最终 handoff frame
   - 注释
   - node links

## 每个切片的 Figma 交付清单

### 必需内容

- 关键页面 frame
- loading / empty / error / success 状态 frame
- 尺寸、间距、排版和颜色说明
- 平台差异说明
- accessibility notes
- ready-for-dev 标记

### 推荐内容

- prototype
- 动效说明
- 组件复用说明
- 与实现偏差的备注

## Ready-for-dev 门槛

以下条件同时满足，才能认为某个切片完成 Figma handoff:

1. 关键页面和关键状态都已有 frame
2. 关键交互有 prototype 或文字注释
3. 设计说明可支撑 Android / iOS / KMP / CJMP 对齐
4. frame / node links 已写入 `framework-agnostic-assets/design-evidence/`
5. 已标记哪些部分是必须对齐，哪些部分允许实现期微调

## 当前切片计划

| Slice | 当前设计状态 | Figma 目标 | 下一步 |
|---|---|---|---|
| `S1` 登录与会话恢复 | 仓库内 UI 定义已完成，Figma 未完成 | 补 launch、login、restore failed、entry shell 四组 frame 和主路径 prototype | 先做 backfill handoff，作为已实现代码的视觉对照 |
| `S2` 会话列表 | 仅有 screen inventory 和视觉原则，还没有切片级 handoff | 完成 Telegram 风格会话列表的 low-fi、high-fi、状态页和 prototype | 必须在实现前完成 |
| `S3` 单聊详情与文本发送 | 只有高层约束，还未拆到切片级视觉稿 | 完成 detail/composer/send-failed 的设计稿 | 依赖 `S2` 主壳确认后推进 |

## 与 MCP 的关系

- Figma MCP 的作用是把现有 Figma frame/node 上下文带给 Agent。
- Figma MCP 不是设计产出本身，也不能替代 low-fi、high-fi 和 handoff 审查。
- 当前仓库默认使用 `figma_remote` 模式；只有需要桌面选择上下文时，才考虑 `figma_desktop`。

## 与 design evidence 的关系

完成 handoff 后，至少要在 `framework-agnostic-assets/design-evidence/` 留下:

- Figma 文件链接
- 关键 frame/node 链接
- design review 结论
- ready-for-dev 清单
- 如有实现偏差，补偏差说明
