package ohos_app_cangjie_entry

internal import ohos.base.LengthProp
internal import ohos.component.*
internal import ohos.state_manage.SubscriberManager
internal import ohos.state_manage.ObservedProperty
internal import ohos.state_manage.LocalStorage
import ohos.state_macro_manage.Entry
import ohos.state_macro_manage.Component
import ohos.state_macro_manage.State

@Entry
@Component
class EntryView {
    let runtimeRoot: String = "/data/user/0/com.example.telegram_compare_cjmp/files/runtime"
    @State var screen: String = "restoring"
    @State var phone: String = ""
    @State var code: String = ""
    @State var banner: String = ""
    @State var sessionSummary: String = ""
    @State var homeStatus: String = ""
    @State var debugMessage: String = ""
    let repo: SessionRepository = SessionRepository(runtimeRoot)

    public override func aboutToAppear(): Unit {
        this.attemptRestore()
    }

    func build() {
        Scroll() {
            Column() {
                Text("Telegram Compare")
                    .fontSize(28)
                    .fontWeight(FontWeight.Medium)
                    .margin(top: 32, bottom: 8)

                Text("CJMP Android-first bootstrap for S1 login / restore")
                    .fontSize(14)
                    .fontColor(0x6B7280)
                    .margin(bottom: 24)

                if (this.screen == "restoring") {
                    Column() {
                        Text("正在恢复上次会话...")
                            .fontSize(22)
                            .fontWeight(FontWeight.Medium)
                        Text("如果没有保存的会话，将进入登录")
                            .fontSize(15)
                            .fontColor(0x6B7280)
                            .margin(top: 8)
                        Text("snapshot: ${this.repo.snapshotPathString()}")
                            .fontSize(12)
                            .fontColor(0x6B7280)
                            .margin(top: 16)
                    }
                    .width(100.percent)
                    .padding(24)
                    .backgroundColor(0xFFFFFF)
                    .borderRadius(24)
                } else if (this.screen == "login") {
                    Column() {
                        Text("登录 Telegram Compare")
                            .fontSize(22)
                            .fontWeight(FontWeight.Medium)

                        if (this.banner != "") {
                            Text(this.banner)
                                .fontSize(14)
                                .fontColor(0xC62828)
                                .margin(top: 12)
                        }

                        Text("手机号")
                            .fontSize(13)
                            .fontColor(0x6B7280)
                            .margin(top: 18, bottom: 6)
                            .width(100.percent)

                        TextInput(placeholder: "请输入手机号")
                            .height(52)
                            .padding(left: 16, right: 16)
                            .backgroundColor(0xEDF2F7)
                            .borderRadius(18)
                            .maxLength(20)
                            .onChange({ value: String =>
                                this.phone = value
                            })

                        Text("验证码")
                            .fontSize(13)
                            .fontColor(0x6B7280)
                            .margin(top: 12, bottom: 6)
                            .width(100.percent)

                        TextInput(placeholder: "请输入验证码")
                            .height(52)
                            .padding(left: 16, right: 16)
                            .backgroundColor(0xEDF2F7)
                            .borderRadius(18)
                            .maxLength(6)
                            .onChange({ value: String =>
                                this.code = value
                            })

                        Button("继续")
                            .width(100.percent)
                            .height(52)
                            .margin(top: 16)
                            .backgroundColor(0x2A8AF6)
                            .onClick({ _ =>
                                this.submitLogin()
                            })

                        Button("使用 Demo 凭据继续")
                            .width(100.percent)
                            .height(44)
                            .margin(top: 12)
                            .backgroundColor(0xE7F6EA)
                            .fontColor(0x2E7D32)
                            .onClick({ _ =>
                                this.submitDemoLogin()
                            })

                        Button("重试恢复")
                            .width(100.percent)
                            .height(44)
                            .margin(top: 12)
                            .backgroundColor(0xDCEBFF)
                            .fontColor(0x2A5CAA)
                            .onClick({ _ =>
                                this.attemptRestore()
                            })

                        Text("Demo 环境固定验证码: 2046")
                            .fontSize(13)
                            .fontColor(0x6B7280)
                            .margin(top: 16)

                        if (this.debugMessage != "") {
                            Text(this.debugMessage)
                                .fontSize(12)
                                .fontColor(0x6B7280)
                                .margin(top: 10)
                        }
                    }
                    .width(100.percent)
                    .padding(24)
                    .backgroundColor(0xFFFFFF)
                    .borderRadius(24)
                } else {
                    Column() {
                        Text("Chats")
                            .fontSize(24)
                            .fontWeight(FontWeight.Medium)
                        Text(this.sessionSummary)
                            .fontSize(16)
                            .margin(top: 8)
                        Text(this.homeStatus)
                            .fontSize(14)
                            .fontColor(0x6B7280)
                            .margin(top: 4, bottom: 18)

                        Column() {
                            Text("Telegram Compare   Bootstrap...")
                                .fontSize(17)
                                .margin(bottom: 12)
                            Text("AI Infra           Need debug")
                                .fontSize(17)
                        }
                        .width(100.percent)
                        .padding(20)
                        .backgroundColor(0xEDF2F7)
                        .borderRadius(18)

                        Button("退出登录")
                            .width(100.percent)
                            .height(48)
                            .margin(top: 18)
                            .backgroundColor(0xE3F2FD)
                            .fontColor(0x245A88)
                            .onClick({ _ =>
                                this.performLogout()
                            })

                        Button("写入失效会话用于下次冷启动")
                            .width(100.percent)
                            .height(48)
                            .margin(top: 12)
                            .backgroundColor(0xFFF2D9)
                            .fontColor(0x9A5A00)
                            .onClick({ _ =>
                                this.seedExpiredSession()
                            })

                        if (this.debugMessage != "") {
                            Text(this.debugMessage)
                                .fontSize(12)
                                .fontColor(0x6B7280)
                                .margin(top: 12)
                        }
                    }
                    .width(100.percent)
                    .padding(24)
                    .backgroundColor(0xFFFFFF)
                    .borderRadius(24)
                }
            }
            .width(100.percent)
            .padding(left: 20, right: 20, top: 8, bottom: 32)
        }
        .width(100.percent)
        .height(100.percent)
        .backgroundColor(0xF4F7FB)
    }

    func attemptRestore(): Unit {
        this.screen = "restoring"
        this.debugMessage = ""
        match(this.repo.restore()) {
            case Restored(current) => this.showHome(current, "已恢复会话")
            case NoSession => this.showLogin("")
            case Failed(message) => this.showLogin(message)
        }
    }

    func submitLogin(): Unit {
        this.debugMessage = ""
        match(this.repo.login(this.phone, this.code)) {
            case Success(current) => this.showHome(current, "刚刚登录")
            case InvalidInput(message) => this.showLogin(message)
            case Failed(message) => this.showLogin(message)
        }
    }

    func submitDemoLogin(): Unit {
        this.phone = "13800138000"
        this.code = "2046"
        this.submitLogin()
    }

    func performLogout(): Unit {
        this.repo.logout()
        this.phone = ""
        this.code = ""
        this.showLogin("")
        let warning = this.repo.takeLastWarning()
        if (warning != "") {
            this.debugMessage = warning
        } else {
            this.debugMessage = "已清理本地会话，当前回到登录页。"
        }
    }

    func seedExpiredSession(): Unit {
        this.repo.seedExpiredSession()
        let warning = this.repo.takeLastWarning()
        if (warning != "") {
            this.debugMessage = warning
        } else {
            this.debugMessage = "已写入失效会话。请重启应用验证恢复失败路径。"
        }
    }

    func showLogin(message: String): Unit {
        this.screen = "login"
        this.banner = message
        this.sessionSummary = ""
        this.homeStatus = ""
    }

    func showHome(current: UserSession, status: String): Unit {
        this.screen = "home"
        this.banner = ""
        this.sessionSummary = current.summary()
        this.homeStatus = status
        let warning = this.repo.takeLastWarning()
        this.debugMessage = warning
    }
}
