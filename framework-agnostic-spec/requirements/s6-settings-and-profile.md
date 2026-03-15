# S6 设置与个人资料

## 切片目标

把现有底部 `Settings` tab 从占位入口升级成可验收的设置页，使用户可以查看当前账号的基础资料，并管理少量会直接影响聊天体验的本地偏好项。

## 用户价值

- 用户能够确认当前登录的是哪个 demo 账号，而不是只能在聊天页隐式使用会话。
- 用户可以直接调整通知、媒体自动下载和动效等偏好项。
- 用户重启应用后，设置仍然保留，不需要每次重新配置。

## 本切片范围

- 在底部导航中把 `Settings` 变成真实可进入的 `UI-S6 Settings / Profile`。
- 页面需要展示当前 session 驱动的基础资料:
  - display name
  - phone number
  - 本地 username
  - about / bio
- 页面至少包含三项可切换的本地偏好:
  - `notifications enabled`
  - `auto-download media on Wi-Fi`
  - `reduced motion`
- 偏好项变更必须通过共享层语义完成，并持久化到本地存储。
- 设置页提供正式的 `退出登录` 入口，不再只依赖聊天页调试区。
- 覆盖 `loading`、`ready`、`failed` 三类主要状态。
- 设置页主壳必须撑满视口，顶部和底部导航固定，只有内容 viewport 滚动。

## 暂不纳入本切片

- 真实头像上传、裁剪和相册权限申请。
- 用户名、手机号或 bio 的完整编辑流程。
- 服务端设置同步、多账号和隐私权限矩阵。
- 复杂通知频道、铃声、聊天文件夹、代理设置。
- 主题切换、夜间模式和 AI overlay 配置页。

## 交付假设

- 当前阶段允许继续使用 fixture-backed / local-first 数据，但资料与偏好项的读写必须走共享层 repository。
- profile card 中的 `display name` 和 `phone number` 继续由 `S1` 的 session 恢复结果提供，本地设置只补充 `username` 和 `about`。
- logout 仍沿用 `S1` 的 session clear 语义，同时保留本地偏好项，除非显式说明为“与账号绑定”的数据。
- 由于真实 Figma 文件仍未闭环，本轮继续允许以 repo-side handoff brief 代理 `D2`，但必须在 acceptance 和 parity 中显式记录。

## 验收标准

| ID | 验收项 | 通过标准 |
|---|---|---|
| AC-S6-1 | 底部导航进入设置 | 用户可从底部 `Settings` tab 进入独立设置页 |
| AC-S6-2 | 资料卡完整可读 | 页面展示当前账号的 display name、phone、username 和 about |
| AC-S6-3 | 偏好项可切换 | 至少三项偏好能在页内切换，并立即反馈当前状态 |
| AC-S6-4 | 偏好项冷启动可恢复 | 重启应用后，用户刚刚切换的偏好仍然保留 |
| AC-S6-5 | 设置页可退出登录 | 用户可从设置页执行 logout，并回到登录页 |
| AC-S6-6 | 视口边界正确 | 设置页主壳撑满手机屏幕，只有内容 viewport 滚动 |

## 核心流程约束

### 从聊天主壳进入设置

1. 用户在 `Chat List` 或 `Search` 页点击底部 `Settings`。
2. 应用进入独立 `UI-S6 Settings / Profile`。
3. 设置页先显示 loading，再进入 ready 或 failed。

### 查看个人资料

1. 用户看到当前账号的 profile hero / profile card。
2. 必须能区分“session 提供的账号身份”和“本地资料补充信息”。
3. 资料区不应退化成纯 debug 文本。

### 切换偏好项

1. 用户点击偏好行或行内 toggle。
2. 当前行立即反馈开启 / 关闭。
3. 变更结果必须持久化，并在下次进入或冷启动后继续生效。

### 从设置页退出登录

1. 用户在设置页点击 `退出登录`。
2. 应用清空 session 并回到登录页。
3. 旧聊天上下文不应被错误恢复。

## 领域落点

- `UserProfileSummary`: 设置页展示用的资料摘要
- `PreferenceKey`: 偏好项标识
- `UserPreference`: 单个偏好项定义
- `SettingsSnapshot`: profile + preferences 的组合对象
- `SettingsLoadResult`: `Success`、`Failed`
- `UpdatePreferenceResult`: `Success`、`Failed`
- `SettingsRepository`: 资料与偏好项读取、写入边界

## 对 KMP 的要求

- `shared-domain` 需要定义 profile / preference 语义和 repository 边界。
- `shared-data` 需要提供可持久化的本地设置存储实现，而不是只在 Activity 内用布尔变量。
- `androidApp` 需要把 `Settings` tab 做成真实页面，并兼容系统返回与现有聊天主路径。
- 设置页中的 logout 必须复用既有 `S1` 会话清理语义，不能实现第二套不一致流程。
