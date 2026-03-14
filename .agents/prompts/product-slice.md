# Product Slice Prompt

把一个 Telegram 功能点拆成 framework-agnostic 规格，并输出到本仓库约定位置。

要求:

1. 先更新 `framework-agnostic-spec/requirements/telegram-mvp.md`
2. 再更新 `framework-agnostic-spec/ux/core-flows.md`
3. 如有新领域概念，再更新 `framework-agnostic-spec/domain/domain-map.md`
4. 如该切片进入可执行状态，在 `framework-agnostic-assets/evaluation/acceptance-reports/` 建立验收报告
5. 把本次 AI 执行过程记入 `framework-agnostic-assets/evaluation/ai-delivery-logs/`
6. 最后在 `framework-agnostic-assets/evaluation/parity-matrix.md` 增加或更新该切片
7. 不要直接写 KMP / CJMP 代码，除非规格已经完整
