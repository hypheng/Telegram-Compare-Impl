# 2026-03-15 S4 Repo Handoff Brief

- Slice: `S4 本地缓存与离线恢复`
- Status: `repo-side handoff`
- Driver: `Codex`

## Frame 建议

1. `S4 / Launch / Restoring With Snapshot`
2. `S4 / Chat List / Restored From Cache`
3. `S4 / Chat Detail / Restored From Cache`
4. `S4 / Cache Cleared / Normal List Load`

## Ready-for-dev 说明

- `S4` 不新增独立主屏，重点是 `UI-S1/UI-S3/UI-S4` 的恢复状态。
- 恢复成功时必须有轻量 info banner，文案表达“已从本地缓存恢复最近上下文，可能不是最新内容”。
- 恢复详情时要直接落到上次 chat detail，而不是先闪回列表。
- “清空本地缓存”保持在 debug-only 区域，不进入正式用户交互。

## 与实现映射

- `shared-domain`: `SyncSnapshotRoute`、`SyncSnapshot`、restore/save/clear use cases
- `shared-data`: snapshot storage + repository export/import
- `androidApp`: restoring 后优先尝试 snapshot route，并把缓存来源映射到 banner

## 当前缺口

- 真实 Figma file / frame / node links 仍缺失
- 本 brief 继续作为 `D2` 的 repo-side 代理
