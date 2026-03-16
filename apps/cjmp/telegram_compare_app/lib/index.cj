package ohos_app_cangjie_entry

import std.collection.*
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
    let sessionRepo: SessionRepository = SessionRepository(runtimeRoot)
    let chatRepo: ChatListRepository = ChatListRepository()

    @State var screen: String = "restoring"
    @State var phone: String = ""
    @State var code: String = ""
    @State var banner: String = ""
    @State var sessionSummary: String = ""
    @State var listStatus: String = ""
    @State var debugMessage: String = ""
    @State var searchDraft: String = ""
    @State var listScenario: String = "default"
    @State var entryPlaceholder: String = ""
    @State var hasReachedListTopOnce: Bool = false
    @State var debugExpanded: Bool = false
    @State var isRefreshing: Bool = false

    public override func aboutToAppear(): Unit {
        this.attemptRestore()
    }

    func build() {
        if (this.screen == "chat-list") {
            Column() {
                Column() {
                    Row() {
                        Text("编辑")
                            .width(80)
                            .fontSize(15)
                            .fontColor(0x2A8AF6)

                        Text("Chats")
                            .width(120)
                            .fontSize(26)
                            .fontWeight(FontWeight.Medium)
                            .textAlign(TextAlign.Center)

                        Text("写消息")
                            .width(80)
                            .fontSize(15)
                            .fontColor(0x2A8AF6)
                            .textAlign(TextAlign.End)
                    }
                    .width(100.percent)
                    .alignItems(VerticalAlign.Center)

                    Text(this.listStatusLine())
                        .fontSize(12)
                        .fontColor(0x6B7280)
                        .width(100.percent)
                        .margin(top: 10)

                    Button(if (this.debugExpanded) { "收起调试面板" } else { "展开调试面板" })
                        .width(100.percent)
                        .height(28)
                        .margin(top: 8)
                        .backgroundColor(0xEFF3F7)
                        .fontColor(0x4B5563)
                        .fontSize(11)
                        .onClick({ _ =>
                            this.debugExpanded = !this.debugExpanded
                        })

                    TextInput(placeholder: "搜索会话或最近消息")
                        .height(46)
                        .margin(top: 12)
                        .padding(left: 16, right: 16)
                        .backgroundColor(0xEDF2F7)
                        .borderRadius(23)
                        .onChange({ value: String =>
                            this.searchDraft = value
                            this.entryPlaceholder = ""
                            this.isRefreshing = false
                            if (value != "" && this.listScenario != "default") {
                                this.listScenario = "default"
                            }
                        })

                    if (this.entryPlaceholder != "") {
                        Text(this.entryPlaceholder)
                            .fontSize(12)
                            .fontColor(0x2A5CAA)
                            .width(100.percent)
                            .padding(left: 12, right: 12, top: 10, bottom: 10)
                            .margin(top: 12)
                            .backgroundColor(0xE7F1FF)
                            .borderRadius(16)
                    }

                    if (this.debugExpanded) {
                        Column() {
                            if (this.sessionSummary != "") {
                                Text(this.sessionSummary)
                                    .fontSize(12)
                                    .fontColor(0x51606F)
                                    .width(100.percent)
                            }

                            if (this.debugMessage != "") {
                                Text(this.debugMessage)
                                    .fontSize(12)
                                    .fontColor(0x6B7280)
                                    .width(100.percent)
                                    .margin(top: 6)
                            }

                            Row() {
                                Button("搜 AI")
                                    .layoutWeight(1)
                                    .height(32)
                                    .backgroundColor(0xF4F7FB)
                                    .fontColor(0x2A5CAA)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.applySearch("infra", "已填入搜索词 infra，用于验证 `AI Infra` 过滤结果。")
                                    })

                                Button("搜 zzz")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xF4F7FB)
                                    .fontColor(0x2A5CAA)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.applySearch("zzz", "已填入搜索词 zzz，用于验证搜索空态。")
                                    })

                                Button("清除")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xF4F7FB)
                                    .fontColor(0x2A5CAA)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.resetSearch()
                                    })
                            }
                            .width(100.percent)
                            .margin(top: 12)

                            Row() {
                                Button("默认")
                                    .layoutWeight(1)
                                    .height(32)
                                    .backgroundColor(0xDCEBFF)
                                    .fontColor(0x2A5CAA)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.setListScenario("default")
                                    })

                                Button("loading")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xEDE7FF)
                                    .fontColor(0x6541A8)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.setListScenario("loading")
                                    })

                                Button("empty")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xFFF3DD)
                                    .fontColor(0x9A5A00)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.setListScenario("empty")
                                    })

                                Button("error")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xFFE4E4)
                                    .fontColor(0xA12A2A)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.setListScenario("error")
                                    })
                            }
                            .width(100.percent)
                            .margin(top: 10)

                            Row() {
                                Button("刷新")
                                    .layoutWeight(1)
                                    .height(32)
                                    .backgroundColor(0xE7F6EA)
                                    .fontColor(0x2E7D32)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.performRefresh("manual")
                                    })

                                Button("失效会话")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xFFF2D9)
                                    .fontColor(0x9A5A00)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.seedExpiredSession()
                                    })

                                Button("退出")
                                    .layoutWeight(1)
                                    .height(32)
                                    .margin(left: 8)
                                    .backgroundColor(0xE3F2FD)
                                    .fontColor(0x245A88)
                                    .fontSize(12)
                                    .onClick({ _ =>
                                        this.performLogout()
                                    })
                            }
                            .width(100.percent)
                            .margin(top: 10)
                        }
                        .width(100.percent)
                        .padding(left: 12, right: 12, top: 10, bottom: 12)
                        .margin(top: 12)
                        .backgroundColor(0xF7F9FC)
                        .borderRadius(16)
                    }
                }
                .width(100.percent)
                .padding(20)
                .backgroundColor(0xFFFFFF)
                .borderRadius(24)

                Column() {
                    Scroll() {
                        Column() {
                        if (this.listScenario != "loading" && this.listScenario != "error") {
                            Row() {
                                Text(if (this.isRefreshing) { "正在刷新..." } else { "下拉可刷新" })
                                    .layoutWeight(1)
                                    .fontSize(12)
                                    .fontColor(0x6B7280)

                                Button(if (this.isRefreshing) { "完成" } else { "刷新" })
                                    .height(28)
                                    .backgroundColor(0xE7F1FF)
                                    .fontColor(0x2A5CAA)
                                    .fontSize(11)
                                    .onClick({ _ =>
                                        if (this.isRefreshing) {
                                            this.finishRefresh()
                                        } else {
                                            this.performRefresh("pull")
                                        }
                                    })
                            }
                            .width(100.percent)
                            .padding(left: 18, right: 18, top: 12, bottom: 12)
                            .backgroundColor(0xF7F9FC)
                            .borderRadius(16)
                            .margin(bottom: 12)
                        }

                        if (this.listScenario == "loading") {
                            Column() {
                                Row() {
                                    Blank()
                                        .width(48)
                                        .height(48)
                                        .backgroundColor(0xE6ECF4)
                                        .borderRadius(24)

                                    Column() {
                                        Blank()
                                            .height(14)
                                            .width(70.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(12)
                                            .width(52.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)
                                            .margin(top: 8)
                                    }
                                    .layoutWeight(1)
                                    .margin(left: 12)

                                    Column() {
                                        Blank()
                                            .height(10)
                                            .width(36)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(18)
                                            .width(26)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(9)
                                            .margin(top: 10)
                                    }
                                    .width(60)
                                    .alignItems(HorizontalAlign.End)
                                }
                                .width(100.percent)
                                .padding(left: 18, right: 18, top: 16, bottom: 16)

                                Row() {
                                    Blank()
                                        .width(48)
                                        .height(48)
                                        .backgroundColor(0xE6ECF4)
                                        .borderRadius(24)

                                    Column() {
                                        Blank()
                                            .height(14)
                                            .width(66.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(12)
                                            .width(48.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)
                                            .margin(top: 8)
                                    }
                                    .layoutWeight(1)
                                    .margin(left: 12)

                                    Column() {
                                        Blank()
                                            .height(10)
                                            .width(36)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(18)
                                            .width(26)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(9)
                                            .margin(top: 10)
                                    }
                                    .width(60)
                                    .alignItems(HorizontalAlign.End)
                                }
                                .width(100.percent)
                                .padding(left: 18, right: 18, top: 16, bottom: 16)

                                Row() {
                                    Blank()
                                        .width(48)
                                        .height(48)
                                        .backgroundColor(0xE6ECF4)
                                        .borderRadius(24)

                                    Column() {
                                        Blank()
                                            .height(14)
                                            .width(62.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(12)
                                            .width(44.percent)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)
                                            .margin(top: 8)
                                    }
                                    .layoutWeight(1)
                                    .margin(left: 12)

                                    Column() {
                                        Blank()
                                            .height(10)
                                            .width(36)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(6)

                                        Blank()
                                            .height(18)
                                            .width(26)
                                            .backgroundColor(0xE6ECF4)
                                            .borderRadius(9)
                                            .margin(top: 10)
                                    }
                                    .width(60)
                                    .alignItems(HorizontalAlign.End)
                                }
                                .width(100.percent)
                                .padding(left: 18, right: 18, top: 16, bottom: 16)
                            }
                            .width(100.percent)
                            .padding(18)
                        } else if (this.listScenario == "error") {
                            Column() {
                                Text("加载失败")
                                    .fontSize(20)
                                    .fontWeight(FontWeight.Medium)
                                    .width(100.percent)

                                Text(this.chatRepo.errorMessage())
                                    .fontSize(14)
                                    .fontColor(0x6B7280)
                                    .width(100.percent)
                                    .margin(top: 8)

                                Button("重试")
                                    .width(100.percent)
                                    .height(40)
                                    .margin(top: 14)
                                    .backgroundColor(0xDCEBFF)
                                    .fontColor(0x2A5CAA)
                                    .onClick({ _ =>
                                        this.setListScenario("default")
                                        this.performRefresh("retry")
                                    })
                            }
                            .width(100.percent)
                            .padding(18)
                        } else if (this.visibleChats().size == 0) {
                            Column() {
                                Text(if (this.searchDraft == "") { "暂无会话" } else { "未找到匹配的会话" })
                                    .fontSize(20)
                                    .fontWeight(FontWeight.Medium)
                                    .width(100.percent)

                                Text(this.chatRepo.emptyMessage(this.searchDraft, this.listScenario))
                                    .fontSize(14)
                                    .fontColor(0x6B7280)
                                    .width(100.percent)
                                    .margin(top: 8)

                                Button(if (this.searchDraft == "") { "恢复默认数据" } else { "清除搜索" })
                                    .width(100.percent)
                                    .height(40)
                                    .margin(top: 14)
                                    .backgroundColor(0xDCEBFF)
                                    .fontColor(0x2A5CAA)
                                    .onClick({ _ =>
                                        if (this.searchDraft == "") {
                                            this.setListScenario("default")
                                        } else {
                                            this.resetSearch()
                                        }
                                    })
                            }
                            .width(100.percent)
                            .padding(18)
                        } else {
                            Column() {
                                if (this.hasVisibleChat(0)) {
                                    Row() {
                                        Text(this.visibleChat(0).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(0).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(0).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(0).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(0).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(0).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(0).unreadCount > 0) {
                                                Text("${this.visibleChat(0).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(0).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(0))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(1)) {
                                    Row() {
                                        Text(this.visibleChat(1).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(1).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(1).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(1).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(1).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(1).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(1).unreadCount > 0) {
                                                Text("${this.visibleChat(1).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(1).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(1))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(2)) {
                                    Row() {
                                        Text(this.visibleChat(2).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(2).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(2).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(2).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(2).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(2).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(2).unreadCount > 0) {
                                                Text("${this.visibleChat(2).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(2).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(2))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(3)) {
                                    Row() {
                                        Text(this.visibleChat(3).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(3).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(3).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(3).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(3).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(3).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(3).unreadCount > 0) {
                                                Text("${this.visibleChat(3).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(3).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(3))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(4)) {
                                    Row() {
                                        Text(this.visibleChat(4).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(4).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(4).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(4).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(4).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(4).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(4).unreadCount > 0) {
                                                Text("${this.visibleChat(4).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(4).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(4))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(5)) {
                                    Row() {
                                        Text(this.visibleChat(5).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(5).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(5).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(5).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(5).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(5).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(5).unreadCount > 0) {
                                                Text("${this.visibleChat(5).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(5).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(5))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(6)) {
                                    Row() {
                                        Text(this.visibleChat(6).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(6).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(6).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(6).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(6).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(6).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(6).unreadCount > 0) {
                                                Text("${this.visibleChat(6).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(6).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(6))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(7)) {
                                    Row() {
                                        Text(this.visibleChat(7).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(7).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(7).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(7).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(7).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(7).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(7).unreadCount > 0) {
                                                Text("${this.visibleChat(7).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(7).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(7))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(8)) {
                                    Row() {
                                        Text(this.visibleChat(8).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(8).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(8).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(8).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(8).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(8).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(8).unreadCount > 0) {
                                                Text("${this.visibleChat(8).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(8).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(8))
                                    })

                                    Blank()
                                        .height(1)
                                        .margin(left: 78, right: 18)
                                        .backgroundColor(0xE9EEF4)
                                }

                                if (this.hasVisibleChat(9)) {
                                    Row() {
                                        Text(this.visibleChat(9).avatarLabel)
                                            .width(48)
                                            .height(48)
                                            .lineHeight(48)
                                            .textAlign(TextAlign.Center)
                                            .fontSize(14)
                                            .fontWeight(FontWeight.Medium)
                                            .backgroundColor(this.visibleChat(9).avatarBackgroundColor)
                                            .fontColor(this.visibleChat(9).avatarTextColor)
                                            .borderRadius(24)

                                        Column() {
                                            Text(this.visibleChat(9).title)
                                                .fontSize(16)
                                                .fontWeight(FontWeight.Medium)
                                                .width(100.percent)

                                            Text(this.visibleChat(9).lastMessagePreview)
                                                .fontSize(13)
                                                .fontColor(0x6B7280)
                                                .width(100.percent)
                                                .margin(top: 4)
                                        }
                                        .layoutWeight(1)
                                        .margin(left: 12)

                                        Column() {
                                            Text(this.visibleChat(9).lastMessageAtLabel)
                                                .fontSize(12)
                                                .fontColor(0x9AA5B1)
                                                .width(100.percent)
                                                .textAlign(TextAlign.End)

                                            if (this.visibleChat(9).unreadCount > 0) {
                                                Text("${this.visibleChat(9).unreadCount}")
                                                    .fontSize(12)
                                                    .fontColor(0xFFFFFF)
                                                    .padding(left: 8, right: 8, top: 2, bottom: 2)
                                                    .backgroundColor(0x2A8AF6)
                                                    .borderRadius(10)
                                                    .margin(top: 8)
                                                    .textAlign(TextAlign.Center)
                                            } else {
                                                Blank()
                                                    .height(18)
                                                    .margin(top: 8)
                                            }

                                            if (this.visibleChat(9).isMuted) {
                                                Text("静音")
                                                    .fontSize(10)
                                                    .fontColor(0x9AA5B1)
                                                    .width(100.percent)
                                                    .textAlign(TextAlign.End)
                                                    .margin(top: 6)
                                            }
                                        }
                                        .width(70)
                                        .margin(left: 8)
                                    }
                                    .width(100.percent)
                                    .padding(left: 18, right: 18, top: 14, bottom: 14)
                                    .onClick({ _ =>
                                        this.openChat(this.visibleChat(9))
                                    })
                                }
                            }
                            .width(100.percent)
                        }
                        }
                        .width(100.percent)
                    }
                    .height(100.percent)
                }
                .layoutWeight(1)
                .width(100.percent)
                .margin(top: 12)
                .backgroundColor(0xFFFFFF)
                .borderRadius(24)

                Row() {
                    Text("Chats")
                        .layoutWeight(1)
                        .fontSize(15)
                        .fontWeight(FontWeight.Medium)
                        .fontColor(0x2A8AF6)
                        .textAlign(TextAlign.Center)

                    Text("Calls")
                        .layoutWeight(1)
                        .fontSize(15)
                        .fontColor(0x6B7280)
                        .textAlign(TextAlign.Center)

                    Text("Settings")
                        .layoutWeight(1)
                        .fontSize(15)
                        .fontColor(0x6B7280)
                        .textAlign(TextAlign.Center)
                }
                .width(100.percent)
                .padding(left: 10, right: 10, top: 14, bottom: 14)
                .margin(top: 12)
                .backgroundColor(0xFFFFFF)
                .borderRadius(28)
            }
            .width(100.percent)
            .height(100.percent)
            .padding(left: 16, right: 16, top: 16, bottom: 16)
            .backgroundColor(0xF4F7FB)
        } else {
            Scroll() {
                Column() {
                    if (this.screen == "restoring") {
                        Column() {
                            Text("Telegram Compare")
                                .fontSize(28)
                                .fontWeight(FontWeight.Medium)

                            Text("正在恢复上次会话...")
                                .fontSize(22)
                                .fontWeight(FontWeight.Medium)
                                .margin(top: 20)

                            Text("如果没有保存的会话，将进入登录。")
                                .fontSize(15)
                                .fontColor(0x6B7280)
                                .margin(top: 8)

                            Text("snapshot: ${this.sessionRepo.snapshotPathString()}")
                                .fontSize(12)
                                .fontColor(0x6B7280)
                                .margin(top: 16)
                        }
                        .width(100.percent)
                        .padding(28)
                        .backgroundColor(0xFFFFFF)
                        .borderRadius(28)
                    } else {
                        Column() {
                            Text("登录 Telegram Compare")
                                .fontSize(22)
                                .fontWeight(FontWeight.Medium)

                            Text("CJMP Android-first bootstrap for S1 login and S2 chat list.")
                                .fontSize(14)
                                .fontColor(0x6B7280)
                                .margin(top: 8)

                            if (this.banner != "") {
                                Text(this.banner)
                                    .fontSize(14)
                                    .fontColor(0xC62828)
                                    .margin(top: 14)
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
                    }
                }
                .width(100.percent)
                .padding(left: 20, right: 20, top: 16, bottom: 20)
            }
            .width(100.percent)
            .height(100.percent)
            .backgroundColor(0xF4F7FB)
        }
    }

    func attemptRestore(): Unit {
        this.screen = "restoring"
        this.debugMessage = ""
        match(this.sessionRepo.restore()) {
            case Restored(current) => this.showChatList(current, "已恢复会话")
            case NoSession => this.showLogin("")
            case Failed(message) => this.showLogin(message)
        }
    }

    func submitLogin(): Unit {
        this.debugMessage = ""
        match(this.sessionRepo.login(this.phone, this.code)) {
            case Success(current) => this.showChatList(current, "刚刚登录")
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
        this.sessionRepo.logout()
        this.phone = ""
        this.code = ""
        this.searchDraft = ""
        this.entryPlaceholder = ""
        this.isRefreshing = false
        this.debugExpanded = false
        this.showLogin("")
        let warning = this.sessionRepo.takeLastWarning()
        if (warning != "") {
            this.debugMessage = warning
        } else {
            this.debugMessage = "已清理本地会话，当前回到登录页。"
        }
    }

    func seedExpiredSession(): Unit {
        this.sessionRepo.seedExpiredSession()
        let warning = this.sessionRepo.takeLastWarning()
        if (warning != "") {
            this.debugMessage = warning
        } else {
            this.debugMessage = "已写入失效会话。请重启应用验证恢复失败路径。"
        }
    }

    func setListScenario(value: String): Unit {
        this.listScenario = value
        this.entryPlaceholder = ""
        this.hasReachedListTopOnce = false
        this.isRefreshing = false
        if (value != "default") {
            this.searchDraft = ""
        }

        if (value == "default") {
            this.listStatus = "已切回默认 chat list fixture。"
            this.debugMessage = "当前使用 fixture-backed `S2` 会话列表。"
        } else if (value == "loading") {
            this.listStatus = "当前展示 loading skeleton，用于补齐 `S2` 状态覆盖。"
            this.debugMessage = "当前 loading 仍是 demo state，真实异步进入列表待继续收敛。"
        } else if (value == "empty") {
            this.listStatus = "当前展示 empty fixture。"
            this.debugMessage = "空态保留顶部 chrome、搜索框和底部导航。"
        } else {
            this.listStatus = "当前展示 error fixture。"
            this.debugMessage = "错误态保留页面结构，并提供重试入口。"
        }
    }

    func applySearch(value: String, note: String): Unit {
        this.searchDraft = value
        this.listScenario = "default"
        this.entryPlaceholder = ""
        this.isRefreshing = false
        this.debugMessage = note
    }

    func resetSearch(): Unit {
        this.searchDraft = ""
        this.listScenario = "default"
        this.entryPlaceholder = ""
        this.isRefreshing = false
        this.debugMessage = "已清除搜索词，列表回到默认过滤。"
    }

    func performRefresh(source: String): Unit {
        if (this.listScenario == "loading") {
            this.debugMessage = "当前是 loading demo，请先切回默认列表后再刷新。"
            return
        }

        if (this.listScenario == "error" || this.listScenario == "empty") {
            this.listScenario = "default"
        }
        this.isRefreshing = true
        this.listStatus = this.chatRepo.refresh(this.searchDraft, this.listScenario)
        if (source == "manual") {
            this.debugMessage = "已通过 debug-only 刷新入口触发 fixture 刷新。"
        } else if (source == "retry") {
            this.debugMessage = "错误态已切回默认数据，并重新触发刷新。"
        } else {
            this.debugMessage = "列表下拉触发了 fixture 刷新。"
        }
    }

    func finishRefresh(): Unit {
        if (!this.isRefreshing) {
            return
        }
        this.isRefreshing = false
        this.listStatus = "已完成刷新，fixture 已更新。"
        this.debugMessage = "刷新完成，列表已更新 fixture。"
    }

    func handleListReachStart(): Unit {
        if (!this.hasReachedListTopOnce) {
            this.hasReachedListTopOnce = true
            this.debugMessage = "已到达列表顶部；再次回到顶部时会触发 fixture 刷新。"
            return
        }
        this.performRefresh("top")
    }

    func openChat(chat: ChatPreview): Unit {
        this.entryPlaceholder = "已选中 ${chat.title}。`S3` 真实聊天详情仍待在 CJMP 侧继续交付。"
        this.debugMessage = "当前只闭合 `S2` entry placeholder，不把点击误判为 `S3` 已完成。"
    }

    func showLogin(message: String): Unit {
        this.screen = "login"
        this.banner = message
        this.sessionSummary = ""
        this.listStatus = ""
        this.entryPlaceholder = ""
        this.isRefreshing = false
        this.debugExpanded = false
    }

    func showChatList(current: UserSession, status: String): Unit {
        this.screen = "chat-list"
        this.banner = ""
        this.sessionSummary = current.summary()
        this.listStatus = "${status}，当前进入 `S2` chat list shell。"
        this.searchDraft = ""
        this.listScenario = "default"
        this.entryPlaceholder = ""
        this.hasReachedListTopOnce = false
        this.isRefreshing = false
        this.debugExpanded = false
        let warning = this.sessionRepo.takeLastWarning()
        this.debugMessage = warning
    }

    func listStatusLine(): String {
        if (this.listScenario == "loading") {
            return "S2 loading demo · 顶部 / 搜索 / 底部固定，列表骨架用于等价状态表达。"
        }
        if (this.listScenario == "error") {
            return "S2 error fixture · ${this.chatRepo.errorMessage()}"
        }
        if (this.isRefreshing) {
            return "S2 refreshing · 列表保持可滚动，刷新结果将更新 fixture。"
        }

        let chats = this.chatRepo.visibleChats(this.searchDraft, this.listScenario)
        if (chats.size == 0) {
            return "S2 empty state · ${this.chatRepo.emptyMessage(this.searchDraft, this.listScenario)}"
        }

        if (this.searchDraft == "") {
            return "${this.listStatus} · ${this.chatRepo.refreshHint()}"
        }
        return "正在搜索 “${this.searchDraft}” · 共 ${chats.size} 条结果。"
    }

    func visibleChats(): ArrayList<ChatPreview> {
        return this.chatRepo.visibleChats(this.searchDraft, this.listScenario)
    }

    func hasVisibleChat(index: Int64): Bool {
        return this.visibleChats().size > index
    }

    func visibleChat(index: Int64): ChatPreview {
        return this.visibleChats()[index]
    }
}
