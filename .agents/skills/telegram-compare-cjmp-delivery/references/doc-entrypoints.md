# CJMP Doc Entrypoints

首选文档源:

- Context7 library: `/hypheng/cjmp-ai-docs`

Agent 查询顺序建议:

1. `cjmp/zh-cn/app-dev/README.md`
   - CJMP 应用开发总导航，先确定是 quick-start、tools 还是 UI 参考
2. `cjmp/zh-cn/app-dev/quick-start/README.md`
   - quick-start 的总入口
3. 按问题类型继续下钻:
   - 环境准备: `cjmp/zh-cn/app-dev/quick-start/start-overview.md`
   - CLI 创建 / 运行应用: `cjmp/zh-cn/app-dev/quick-start/start-tools.md`
   - VS Code 插件流: `cjmp/zh-cn/app-dev/quick-start/start-plugins.md`
   - 第一个应用 / build / debug: `cjmp/zh-cn/app-dev/quick-start/get-start.md`
   - 工程类型和目录结构: `cjmp/zh-cn/app-dev/quick-start/app-info.md`
   - `keels` 命令、设备、签名、打包: `cjmp/zh-cn/app-dev/tools/tools-cmd.md`
   - UI 组件参考: `cjmp/zh-cn/framework-dev/cj-ui/README.md`
4. 只有当问题已经明确落在“纯仓颉子模块 / 语言 / 包管理”时，再查:
   - `cangjie_tools`
   - `cangjie_lang_manual`
   - `std` / `stdx`

Agent 最先建立的 CJMP 心智模型:

- CJMP 应用脚手架:
  - `keels create --app`
  - `keels create --module`
  - `keels create --logic-module`
- 工程类型选择:
  - `app` = 独立 App
  - `module` = 嵌入已有原生工程
  - `logic-module` = 可复用逻辑模块
- 应用生命周期命令:
  - `keels doctor -v`
  - `keels devices`
  - `keels build`
  - `keels run`
- 仓颉子模块命令:
  - `cjpm.toml`
  - `cjpm build`
  - `cjpm test`

当问题涉及:

- 应用工程模板 / 平台壳 / 目录结构: 优先查 `app-info.md`
- 应用创建 / 首次 build / run / debug: 优先查 `start-tools.md`、`start-plugins.md`、`get-start.md`
- UI 组件 / 状态管理 / 页面导航: 优先查 `framework-dev/cj-ui/README.md` 与 `get-start.md`
- 设备 / 签名 / 平台包类型: 优先查 `tools-cmd.md`
- 纯语言语法 / 包依赖 / `cjpm` 细节: 再查 `cangjie_tools`
