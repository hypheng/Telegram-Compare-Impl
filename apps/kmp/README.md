# KMP App Track

## 目标

实现 Telegram 对比项目的 KMP 版本。

## Agent 入口

- 规格先看 `framework-agnostic-spec/`
- 共享契约和 parity 先看 `framework-agnostic-assets/`
- 实现时优先使用 `$telegram-compare-kmp-delivery`
- 环境检查先跑 `bash ./.agents/setup/check-kmp-env.sh`

## 当前策略

按照 Kotlin 官方建议，优先共享业务逻辑、数据契约和状态机，UI 是否共享在产品切片稳定后再决定。

## 建议结构

```text
apps/kmp/
├── README.md
├── shared-domain/   # 领域与用例
├── shared-data/     # 网络 / 存储契约与实现
├── androidApp/      # Android 壳
└── iosApp/          # iOS 壳
```

## 当前状态

- 只完成仓库入口和设计约束
- 未初始化 Gradle Wrapper
- 未创建真实模块

## 下一步

1. 安装 Gradle 或生成 Wrapper
2. 初始化 KMP 根工程
3. 先实现 S1/S2/S3 的 shared-domain 和 shared-data
4. 再补 Android / iOS 壳
