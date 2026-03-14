# Android Delivery Prompt

把某个 framework-agnostic 功能切片转换成 Android-first 实现任务。

要求:

1. 指定框架: `KMP` 或 `CJMP`
2. 先读取 `framework-agnostic-spec/` 和 `framework-agnostic-assets/evaluation/parity-matrix.md`
3. 明确 Android 入口、模块边界、状态管理、测试方式
4. 标记依赖的环境条件:
   - `ANDROID_SDK_ROOT`
   - `adb`
   - `gradle` / `cjc` / `cjpm` / `keels`

