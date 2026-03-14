# KMP Debug Runbook

## 问题分类

- `build`: Gradle、plugin、source set、wrapper、dependency resolution
- `runtime`: app crash、Android lifecycle、platform wiring
- `test`: commonTest、android host test、fixture mismatch
- `workflow`: AI 跑偏、模块边界不清、重复返工

## 最小调试顺序

1. 先记下失败命令
2. 确认失败模块
3. 判断是 `shared-domain`、`shared-data` 还是 `androidApp`
4. 收集构建输出或 logcat
5. 写入 AI delivery log
6. 如果问题可复现且会持续出现，建 `ai/kmp` issue

## 常用命令

```bash
bash ./scripts/kmp-doctor.sh
./scripts/kmp-gradle-no-proxy.sh :shared-domain:allTests
cd apps/kmp
./gradlew help
./gradlew doctor
./gradlew :shared-domain:allTests --stacktrace
./gradlew :shared-data:allTests --stacktrace
./gradlew :androidApp:assembleDebug --stacktrace
adb logcat
```

首次冷启动注意:

- `AGP 9.1.0` 需要 `Gradle 9.3.1`
- 如果 `./gradlew` 卡在 distribution 下载，不要误判成 build script 错误
- 应先区分是 wrapper 下载问题，还是 plugin / DSL 配置问题
- 如果依赖解析报 SOCKS / 127.0.0.1 代理错误，优先用 `./scripts/kmp-gradle-no-proxy.sh` 重跑

## 需要记录到 AI log 的信息

- failing command
- module
- root cause guess
- 实际根因
- 修复尝试次数
- 是否需要人工介入
- 时间影响
- token 影响

## 必须升级成 issue 的情况

- 同类 Gradle / plugin 问题重复出现
- Android-KMP library plugin 配置导致多次返工
- built-in Kotlin 和 KMP plugin 的组合引发结构性问题
- 需要调整仓库级脚本、模板或模块边界
