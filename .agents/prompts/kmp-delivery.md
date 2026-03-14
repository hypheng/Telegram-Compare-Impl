# KMP Delivery Prompt

把某个 framework-agnostic 功能切片转换成 KMP Android-first 实现任务。

要求:

1. 先读取 `framework-agnostic-spec/`、acceptance report 和 parity matrix
2. 跑 `bash ./.agents/setup/check-kmp-env.sh`
3. 跑 `bash ./scripts/check-kmp-project.sh`
4. 读取 `apps/kmp/docs/ai-workflow.md` 和 `apps/kmp/docs/module-map.md`
5. 实现顺序固定为:
   - `shared-domain`
   - `shared-data`
   - `androidApp`
   - `iosApp`
6. 每次真实执行都要在 `framework-agnostic-assets/evaluation/ai-delivery-logs/` 留记录
7. 修改完成后回写 acceptance report 和 parity matrix
