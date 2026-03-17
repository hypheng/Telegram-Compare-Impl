package ohos_app_cangjie_entry

import std.collection.*

class ChatMessage {
    let id: String
    let text: String
    let isOutgoing: Bool
    let timeLabel: String
    let status: String

    public init(id: String, text: String, isOutgoing: Bool, timeLabel: String, status: String) {
        this.id = id
        this.text = text
        this.isOutgoing = isOutgoing
        this.timeLabel = timeLabel
        this.status = status
    }

    func statusLabel(): String {
        if (!this.isOutgoing) {
            return this.timeLabel
        }
        if (this.status == "sending") {
            return "${this.timeLabel} · 发送中"
        }
        if (this.status == "failed") {
            return "${this.timeLabel} · 发送失败"
        }
        if (this.status == "retrying") {
            return "${this.timeLabel} · 重试中"
        }
        return "${this.timeLabel} · 已发送"
    }

    func withStatus(newStatus: String, newTimeLabel: String): ChatMessage {
        return ChatMessage(this.id, this.text, this.isOutgoing, newTimeLabel, newStatus)
    }
}

class ChatThread {
    let id: String
    let title: String
    let subtitle: String
    let messages: ArrayList<ChatMessage>

    public init(id: String, title: String, subtitle: String, messages: ArrayList<ChatMessage>) {
        this.id = id
        this.title = title
        this.subtitle = subtitle
        this.messages = messages
    }
}

class ChatDetailRepository {
    var messageSequence: Int64 = 0

    func loadThread(chatId: String, title: String, subtitle: String): ChatThread {
        let messages = this.buildDefaultMessages()
        this.messageSequence = messages.size
        return ChatThread(chatId, title, subtitle, messages)
    }

    func nextMessageId(): String {
        this.messageSequence += 1
        return "msg-${this.messageSequence}"
    }

    func buildDefaultMessages(): ArrayList<ChatMessage> {
        return ArrayList<ChatMessage>([
            ChatMessage("msg-1", "你好！这是 CJMP 的 S3 详情页基线。", false, "09:12", "sent"),
            ChatMessage("msg-2", "收到，我们在补齐 sending / failed / retrying 证据。", true, "09:12", "sent"),
            ChatMessage("msg-3", "消息区需要具备真实滚动长度。", false, "09:13", "sent"),
            ChatMessage("msg-4", "已准备好更长的消息来验证气泡截断与密度。", true, "09:14", "sent"),
            ChatMessage("msg-5", "我们需要确保 composer 固定且不随列表滚动。", false, "09:15", "sent"),
            ChatMessage("msg-6", "明白，顶部和底部固定，消息流独立滚动。", true, "09:16", "sent"),
            ChatMessage("msg-7", "下一条消息将进入发送中状态。", false, "09:17", "sent"),
            ChatMessage("msg-8", "发送中状态要显式标记。", true, "09:18", "sent"),
            ChatMessage("msg-9", "失败消息需要 retry。", false, "09:19", "sent"),
            ChatMessage("msg-10", "OK，失败与重试路径会用 debug 控件触发。", true, "09:20", "sent"),
            ChatMessage("msg-11", "再补两条，确保小屏也可滚动。", false, "09:21", "sent"),
            ChatMessage("msg-12", "这条是默认 fixture 的末尾。", true, "09:22", "sent")
        ])
    }
}
