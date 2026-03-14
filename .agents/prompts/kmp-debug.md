# KMP Debug Prompt

调试 KMP 方案时，目标不是只让命令通过，还要把问题沉淀成可比较的证据。

要求:

1. 先记录 failing command 和 failing module
2. 问题归类为 `build`、`runtime`、`test` 或 `workflow`
3. 优先缩小到 `shared-domain`、`shared-data` 或 `androidApp`
4. 把证据写进 AI delivery log
5. 如问题可复现或会重复出现，创建 `ai/kmp` issue
6. 修复后回写 acceptance report 和 parity matrix
