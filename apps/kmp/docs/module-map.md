# KMP Module Map

## 模块职责

| Module | 责任 | AI 应该放什么 | AI 不该放什么 |
|---|---|---|---|
| `shared-domain` | 领域模型、用例、仓储接口、状态机 | `ChatSummary`、`Message`、use case、业务规则 | Android UI、网络 SDK、数据库驱动 |
| `shared-data` | 数据实现、mock、fixture、存储 / 网络适配 | repository implementation、mapper、data source | Activity、ViewModel、平台壳导航 |
| `androidApp` | Android 壳、启动入口、页面状态映射 | Activity、Compose / View UI、Android wiring | 通用业务规则、跨平台仓储接口定义 |
| `iosApp` | iOS host shell | Xcode 宿主、Swift bridge | Android-only 代码 |

## 推荐目录

```text
apps/kmp/
├── shared-domain/src/commonMain/
├── shared-data/src/commonMain/
├── androidApp/src/main/
└── iosApp/
```

## 切片推进时的落点规则

- 新领域对象先去 `shared-domain`
- 新 mock / fixture 驱动的数据实现先去 `shared-data`
- Android 页面、导航、交互反馈去 `androidApp`
- 如果某段代码只为 Android UI 服务，不要放进共享层

## AI 常见误区

- 把 Android-specific state holder 塞进共享层
- 为了“多平台”过早共享 UI
- 在 `androidApp` 里重复定义领域对象
- 没有先从 acceptance report 找验收项就直接写代码
