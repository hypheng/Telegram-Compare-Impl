# AI / Tooling Gap List

检查时间: 2026-03-15

## 本机检查结果

| 项目 | 状态 | 证据 |
|---|---|---|
| `repo-local codex self-check` | ready | `bash ./.agents/setup/check-codex-ai-infra.sh` |
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

- CJMP 根工程和最小可运行模板还没初始化进仓库
- Android emulator / device profile 没纳入仓库级自检
- Telegram API / mock backend / test account 策略还没落文档
- Figma 设计文件、组件库、handoff 约定还没落地
- Figma MCP 还没按项目需求启用
- `figma` 与 `figma-implement-design` 全局 skills 还未安装；当前不阻塞 KMP 开发，但进入设计稿联动前应执行 `bash ./.agents/setup/install-curated-skills.sh --apply`
- GitHub labels 还没在远端仓库里实际创建；issue templates 只能提供约定，不能替代仓库配置
- AI token 用量还没有自动采集通路；当前需要手工回填或从宿主工具的 usage 信息摘录
- 刚补齐的 acceptance report / AI delivery log / issue taxonomy 还没经过首个真实切片验证
- KMP bootstrap 基线（`./gradlew doctor`、共享层测试、`androidApp:assembleDebug`、`androidApp:assembleRelease`）已验证通过，但这套基础设施还没经过首个真实业务切片验证

### Agent 侧缺口

- 未来补真正的 Android UI 测试、截图和回归流水线
- 如采用 Figma desktop MCP，需要本机 Figma Desktop App 与 Dev Mode 权限
- KMP 还没有首个真实切片的 shared-domain / shared-data / androidApp 业务实现，当前仍是 bootstrap 骨架
- KMP iOS host shell 还没有真实 Xcode 工程
