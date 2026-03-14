# Framework-Agnostic Assets

这里不是共享源码，而是共享“契约、夹具和对比证据”。

## 主要内容

- `contracts/`: API / 状态 / 数据契约
- `fixtures/`: 测试夹具与样例
- `evaluation/`: 双框架对比矩阵、切片验收、AI 执行日志、issue 分类规范
- `design-evidence/`: 设计评审与 handoff 证据

这样做的目的:

- 避免为了“共享代码”过早耦合 KMP 和 CJMP
- 保持对比口径统一
