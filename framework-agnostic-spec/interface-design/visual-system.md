# Visual System

## 目标

建立一个可以同时指导 KMP 与 CJMP 实现、并尽量贴近现有 Telegram 客户端的视觉语言，而不是重新发明一套聊天产品品牌。

## Telegram 风格锚点

- 主参考为用户于 2026-03-15 提供的 Telegram 聊天列表截图，见 `framework-agnostic-assets/design-evidence/2026-03-15-telegram-style-reference.md`
- 优先复用 Telegram 已验证的视觉层级:
  - 纯净浅色背景
  - 低对比度分隔线
  - 蓝色主强调色
  - 大圆角搜索框和操作按钮
  - 高密度但不拥挤的会话列表
  - 轻量悬浮感的底部导航
- 不额外引入新的品牌语言:
  - 不做渐变品牌背景
  - 不做卡片化聊天列表
  - 不做夸张阴影、玻璃拟态或装饰性插画
- 允许保留项目自己的数据与图标资源，但整体信息架构、间距节奏和组件轮廓要与 Telegram 同向

## Foundations

### Typography

- 使用平台系统字体，保持接近 Telegram 的系统原生观感
- 定义 `navTitle` / `rowTitle` / `body` / `meta` / `tabLabel` 五级文本体系
- `rowTitle` 用于会话名和主标题，应明显强于消息预览与时间
- `meta` 用于时间、未读、静音等弱信息，不与主标题竞争
- 明确 light / dark 模式下的层级对比
- 支持动态字体缩放

### Color

- 定义 semantic colors，并让默认 token 靠近 Telegram 的浅色界面:
  - `surface`
  - `surfaceVariant`
  - `primary`
  - `secondary`
  - `error`
  - `success`
  - `warning`
- `surface` 应接近纯白或极浅灰
- `surfaceVariant` 应用于搜索框、选中 tab 背板、次级胶囊按钮
- `primary` 默认使用 Telegram 风格的高可见蓝色
- 分隔线和弱边框应尽量轻，不抢正文
- 不直接在业务组件里写死颜色值

### Spacing

- 使用统一 spacing scale，例如 `4, 8, 12, 16, 24, 32`
- 页面、列表项、输入区使用同一套间距语义
- 会话列表行高、头像尺寸和标题区留白要接近 Telegram 的紧凑节奏，而不是 Material 卡片式稀疏布局

### Shape

- 定义消息气泡、按钮、搜索框、底部导航和底部面板的圆角层级
- 聊天列表默认是平面列表加细分隔线，而不是独立卡片
- 搜索框、顶部操作按钮和底部导航应使用明显的胶囊或大圆角语言
- 头像保持纯圆

### Motion

- 仅在关键切换使用动效:
  - screen transition
  - list refresh
  - message sending
  - AI overlay reveal
- 动效需要提供 reduced-motion fallback
- 默认动效应轻、短、低存在感，避免与 Telegram 的稳态交互风格冲突

## Component Primitives

- top navigation
- search field
- bottom navigation
- list row
- avatar
- message bubble
- input composer
- inline banner
- skeleton block
- bottom sheet / overlay

## 关键组件约束

### Chat List Header

- 标题区优先使用 Telegram 风格的信息层级:
  - 中心标题
  - 左侧文字操作
  - 右侧圆角图标按钮组
- 搜索框位于标题区下方，宽度接近全幅，使用浅灰背景和大圆角

### Chat Row

- 左侧圆形头像，右侧是两行信息区:
  - 第一行: 会话名、认证/置顶等徽标、时间
  - 第二行: 最近消息预览、草稿或系统状态
- 默认使用平面白底加极细分隔线
- 未读、静音、置顶、草稿要优先用 Telegram 类似的低噪音表达，不做重装饰标签

### Bottom Navigation

- 允许采用 Telegram iOS 客户端那种大圆角承载区和选中项底板
- 选中项强调应清晰，但不能比会话列表主体更抢眼

### Chat Detail

- 顶部导航、消息气泡、输入框都应延续 Telegram 的朴素和高密度风格
- 输入区优先使用圆角 composer，不做夸张阴影或厚重工具栏
- 顶部导航优先采用 `back + avatar + title/status` 结构，标题左对齐，不在主导航中展示切片或 debug 徽标
- 键盘弹出时页面必须 `resize`，保持顶部导航与 composer 可见
- outgoing / incoming 气泡的时间与发送状态要贴近气泡右下角，避免单独拉出厚 meta 行
- debug-only 能力应降噪为次级入口，不能成为聊天详情主视觉的一部分

### Media Attachment

- 图片消息优先表现为真实缩略图容器，而不是彩色信息卡片
- 图片底部可带弱化 caption 和时间，但不在缩略图中放置夸张标签
- media picker 使用 Telegram 风格的底部 sheet 和缩略图网格，不把选择器做成 settings 风格列表卡片
- composer 中的媒体入口优先是附件图标按钮，而不是高存在感文本按钮
