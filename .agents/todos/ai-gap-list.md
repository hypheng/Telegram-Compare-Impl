# AI / Tooling Gap List

检查时间: 2026-03-14

## 本机检查结果

| 项目 | 状态 | 证据 |
|---|---|---|
| `python3` | ready | `Python 3.9.6` |
| `java` | ready | `OpenJDK 17.0.14` |
| `xcodebuild` | ready | present |
| `node` | ready | present |
| `npm` | ready | present |
| `gradle` | ready | present |
| `adb` | ready | present |
| `cjc` | ready | present |
| `cjpm` | ready | present |
| `keels` | ready | present |
| `codex MCP servers` | ready | `openaiDeveloperDocs`, `context7` configured |

## 还缺什么

### 仓库级缺口

- KMP 根工程和 Gradle Wrapper 还没初始化进仓库
- CJMP 根工程和最小可运行模板还没初始化进仓库
- Android emulator / device profile 没纳入仓库级自检
- Telegram API / mock backend / test account 策略还没落文档
- Figma 设计文件、组件库、handoff 约定还没落地
- Figma MCP 还没按项目需求启用
- GitHub labels 还没在远端仓库里实际创建；issue templates 只能提供约定，不能替代仓库配置
- AI token 用量还没有自动采集通路；当前需要手工回填或从宿主工具的 usage 信息摘录
- 刚补齐的 acceptance report / AI delivery log / issue taxonomy 还没经过首个真实切片验证

### Agent 侧缺口

- 重启 Codex，让新装的全局 skills 明确可见
- 未来补真正的 Android UI 测试、截图和回归流水线
- 如采用 Figma desktop MCP，需要本机 Figma Desktop App 与 Dev Mode 权限
