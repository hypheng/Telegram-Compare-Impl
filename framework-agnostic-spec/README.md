# Framework-Agnostic Spec

这里放框架无关的应用规格。

它不是“产品部门文档”，而是实现前的 single source of truth。

## 顺序

1. `requirements/telegram-mvp.md`
2. `ux/core-flows.md`
3. `interface-design/`
4. `domain/domain-map.md`
5. `comparison/evaluation-rubric.md`
6. `comparison/ai-delivery-observability.md`
7. `comparison/debugging-and-issue-workflow.md`

## 基本原则

- 先定义用户价值，再定义技术实现
- 先定义验收，再写框架代码
- 所有切片都要能映射到 KMP 和 CJMP 两边
- AI 的执行成本、阻塞与调试过程也属于规格的一部分，不能只留在对话里
