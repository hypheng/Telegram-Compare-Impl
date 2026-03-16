- Slice: `S2 会话列表`
- Status: `in_progress`
- Last Updated: `2026-03-16`
- Product Spec: `framework-agnostic-spec/requirements/s2-chat-list.md`
- UI Design: `framework-agnostic-spec/interface-design/s2-chat-list.md`
- Design Evidence: `framework-agnostic-assets/design-evidence/2026-03-15-S2-chat-list-repo-handoff-brief.md`

## Goal

把当前 `S1` 登录后的 CJMP home 占位页升级成真实可比对的 Telegram 风格会话列表，至少先闭合固定 chrome、fixture-backed list、搜索、空态 / 错误态和 `S3` entry placeholder，并在 self-render 约束下保留可验证证据路径。

## Current Snapshot

- 当前 `CJMP S2` 已不再是 `deferred`：
  - 登录成功或会话恢复成功后，模拟器可以稳定落到真实 `S2` chat list safe shell
  - 默认态顶部与滚动后的前 5 条会话 fixture 已形成 repo 内截图证据
- 当前 safe shell 选择优先规避 blank-screen regression：
  - 暂时使用 `Column + Text + Button + 普通 TextInput` 的保守组合
  - 避开此前触发白屏的重排模式，例如更接近 Telegram 的复杂行布局、viewport 内独立滚动和 pull-to-refresh
- 当前 repo 证据：
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-safe-shell/s2-cjmp-default-top.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-safe-shell/s2-cjmp-default-list.png`
  - `framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-16-S2-cjmp-safe-shell.md`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-fixed-chrome/s2-cjmp-refreshing.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-fixed-chrome/s2-cjmp-entry-placeholder.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-fixed-chrome/s2-cjmp-empty.png`
  - `framework-agnostic-assets/evaluation/acceptance-evidence/s2-chat-list/cjmp-fixed-chrome/s2-cjmp-error.png`
  - `framework-agnostic-assets/evaluation/ai-delivery-logs/2026-03-16-S2-cjmp-fixed-chrome.md`

## Current Gaps To Acceptance

- 顶部 chrome、搜索框、列表 viewport 和底部导航尚未达到 `UI-S3` 设计要求的固定分区结构。
- 当前 debug-only 控件仍然占据首屏，尚未压缩到低干扰区域。
- 列表行尚未恢复 Telegram 风格的头像 / 时间 / 未读徽标 / 静音状态密度，只先保证 safe shell 可见。
- pull-to-refresh 还没有落成真实列表 viewport 内手势，只保留了 debug-only 手动刷新入口。
- `search / empty / error / refreshing / entry placeholder` 的设备侧归档已补齐；error 状态为手工验证并记录。
- 仍缺 debug 面板收起后的 empty/error 纯净截图。

## Delivery Order

1. `apps/cjmp`
   - 在 `telegram_compare_app/lib/` 内新增 `S2` fixture、状态机和列表渲染
   - 保持 session restore/login/logout 继续沿用 `session.cj`
2. `device validation`
   - 先做 `keels build apk` / `keels run`
   - 再用截图、`run-as` 快照和 root-only XML 记录可见状态
3. `evaluation`
   - 回写 acceptance report、parity matrix、AI log

## Scope Notes

- 继续保持 app-only 结构，不新增 `business/` 或 `logic/` 模块。
- 默认仍以 Android-first 验证。
- fixture 至少覆盖 default、search、empty、error、refreshing placeholder、entry placeholder。
- 由于 `CJMP` 为自渲染页面，`uiautomator dump` 仍只作为 root-only 辅助证据，不把 XML 子树当主验收路径。
- 当前仍缺真实 Figma file / frame / node links，因此先沿用 repo-side handoff brief。

## Exit Criteria

1. `apps/cjmp/telegram_compare_app` 可以构建并运行
2. 登录或会话恢复后能进入真实 chat list，而不是 `S1` 的调试占位
3. `S2` 的 default / search / empty / error / refresh indicator / entry placeholder 至少形成首轮可见证据
4. `framework-agnostic-assets/evaluation/parity-matrix.md` 不再把 `CJMP S2` 标成 `deferred`

## Verification Snapshot

- `bash ./.agents/setup/check-cjmp-env.sh`
- `cd apps/cjmp/telegram_compare_app && keels build apk --platform android-arm64 --debug`
- `cd apps/cjmp/telegram_compare_app && keels run --debug -d emulator-5554`
- `adb shell am start -W -n com.example.telegram_compare_cjmp/.EntryEntryAbilityActivity`
- `adb exec-out screencap -p`
- `adb logcat -d | rg "Bounds\\[-inf|RSRenderNodeDrawableAdapter|InitRenderParams failed"`

## Known Risks

- `CJMP` 当前未确认是否存在低成本异步 loading / pull-to-refresh 标准范式；如果首轮只能做等价状态表达，需要在 acceptance 中显式记录。
- 自渲染引擎仍会限制 UI tree 观测，设备侧交互可能继续依赖截图和坐标探针。
- 当前 safe shell 虽然已经可见，但渲染日志仍会出现 `RS_NODE Bounds[-inf ...]`，因此 acceptance 仍需以设备截图为主、log 为辅。
