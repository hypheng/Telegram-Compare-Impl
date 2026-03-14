# KMP Android-First Notes

优先观察:

- Gradle / 构建稳定性
- shared-domain / shared-data 是否清晰
- Android UI 壳是否能快速承接切片
- Compose 共享 UI 是否真的必要

最低环境:

- `java`
- `gradle` 或 wrapper
- `adb`
- Android SDK

当前仓库约定:

- `apps/kmp/shared-domain`: 领域对象、用例、仓储接口
- `apps/kmp/shared-data`: mock / data 实现、存储和网络适配
- `apps/kmp/androidApp`: Android 壳
- `apps/kmp/iosApp`: iOS 壳计划位

AI 交付顺序:

1. 先读 `framework-agnostic-spec/`
2. 再读 `apps/kmp/docs/module-map.md`
3. 先改 `shared-domain`
4. 再改 `shared-data`
5. 最后改 `androidApp`
