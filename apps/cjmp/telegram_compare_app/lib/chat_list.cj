package ohos_app_cangjie_entry

import std.collection.*
import std.unicode.*

class ChatPreview {
    let id: String
    let title: String
    let lastMessagePreview: String
    let unreadCount: Int64
    let lastMessageAtLabel: String
    let avatarLabel: String
    let statusLabel: String
    let isMuted: Bool
    let avatarBackgroundColor: Int64
    let avatarTextColor: Int64

    public init(
        id: String,
        title: String,
        lastMessagePreview: String,
        unreadCount: Int64,
        lastMessageAtLabel: String,
        avatarLabel: String,
        statusLabel: String,
        isMuted: Bool,
        avatarBackgroundColor: Int64,
        avatarTextColor: Int64
    ) {
        this.id = id
        this.title = title
        this.lastMessagePreview = lastMessagePreview
        this.unreadCount = unreadCount
        this.lastMessageAtLabel = lastMessageAtLabel
        this.avatarLabel = avatarLabel
        this.statusLabel = statusLabel
        this.isMuted = isMuted
        this.avatarBackgroundColor = avatarBackgroundColor
        this.avatarTextColor = avatarTextColor
    }
}

class ChatListRepository {
    let baseChats: ArrayList<ChatPreview> = buildDefaultChats()
    var refreshCount: Int64 = 0

    func visibleChats(keyword: String, scenario: String): ArrayList<ChatPreview> {
        if (scenario == "empty") {
            return ArrayList<ChatPreview>()
        }
        if (scenario == "error") {
            return ArrayList<ChatPreview>()
        }

        let normalizedKeyword = normalizeKeyword(keyword)
        let keywordUpper = normalizedKeyword.toUpper()
        let items = ArrayList<ChatPreview>()

        for (chat in baseChats) {
            let current = applyRefresh(chat)
            if (keywordUpper == "") {
                items.add(current)
                continue
            }

            let titleUpper = current.title.toUpper()
            let previewUpper = current.lastMessagePreview.toUpper()
            if (titleUpper.contains(keywordUpper) || previewUpper.contains(keywordUpper)) {
                items.add(current)
            }
        }

        return items
    }

    func emptyMessage(keyword: String, scenario: String): String {
        if (scenario == "empty") {
            return "当前没有会话，试试恢复默认数据。"
        }
        let normalizedKeyword = normalizeKeyword(keyword)
        if (normalizedKeyword != "") {
            return "未找到匹配的会话。试试搜索会话名或最近消息中的关键词。"
        }
        return "当前没有可显示的会话。"
    }

    func errorMessage(): String {
        return "Fixture 加载失败。请重试或切回默认数据。"
    }

    func refresh(keyword: String, scenario: String): String {
        if (scenario == "error") {
            return "当前是错误 fixture，请先切回默认数据后再刷新。"
        }

        refreshCount += 1
        let normalizedKeyword = normalizeKeyword(keyword)
        if (scenario == "empty") {
            return "Refresh #${refreshCount} 完成，但空态 fixture 仍然没有会话。"
        }
        if (normalizedKeyword != "") {
            return "Refresh #${refreshCount} 完成，已重新过滤关键词 “${normalizedKeyword}”。"
        }
        return "Refresh #${refreshCount} completed for the CJMP chat list."
    }

    func refreshHint(): String {
        if (refreshCount == 0) {
            return "列表回到顶部后会刷新 fixture。"
        }
        return "已刷新 ${refreshCount} 次。继续回到顶部可再次刷新。"
    }

    func applyRefresh(chat: ChatPreview): ChatPreview {
        if (refreshCount == 0) {
            return chat
        }

        if (chat.id == "chat-1") {
            let nextUnread = Int64((refreshCount % 5) + 1)
            return ChatPreview(
                chat.id,
                chat.title,
                "Refresh #${refreshCount} completed for the CJMP chat list.",
                nextUnread,
                "刚刚",
                chat.avatarLabel,
                "refresh ${refreshCount}",
                chat.isMuted,
                chat.avatarBackgroundColor,
                chat.avatarTextColor
            )
        }

        if (chat.id == "chat-9" && refreshCount % 2 == 1) {
            return ChatPreview(
                chat.id,
                chat.title,
                "Preview text changed so search and refresh can be observed together.",
                chat.unreadCount,
                "刚刚",
                chat.avatarLabel,
                "awaiting input",
                chat.isMuted,
                chat.avatarBackgroundColor,
                chat.avatarTextColor
            )
        }

        return chat
    }

    func normalizeKeyword(value: String): String {
        return value.trimStart(" ").trimEnd(" ")
    }
}

func buildDefaultChats(): ArrayList<ChatPreview> {
    return ArrayList<ChatPreview>([
        ChatPreview("chat-1", "Telegram Compare", "Refine the KMP shell before CJMP parity.", 3, "09:24", "TC", "last seen recently", false, 0xDDF0FF, 0x0E5D91),
        ChatPreview("chat-2", "AI Infra", "Figma MCP is ready, waiting for editable frames.", 1, "08:11", "AI", "syncing logs", true, 0xE8EAFD, 0x4E5ABD),
        ChatPreview("chat-3", "Product Design", "S2 still needs a Telegram-style search field.", 0, "昨天", "PD", "reviewing handoff", false, 0xFFEBCD, 0xA15E12),
        ChatPreview("chat-4", "KMP Delivery", "Wire the shared-domain use cases before polishing UI.", 6, "周四", "KD", "typing...", false, 0xE5F7EC, 0x1D7A47),
        ChatPreview("chat-5", "QA Evidence", "Need another screenshot after the scroll-boundary fix.", 2, "周三", "QA", "online", false, 0xFFE4EC, 0xA03B63),
        ChatPreview("chat-6", "Growth Ops", "Longer preview copy helps validate truncation in the row layout.", 0, "周二", "GO", "last seen 2h ago", false, 0xEAF7FF, 0x2B6B98),
        ChatPreview("chat-7", "Design Systems", "Avatar colors and muted metadata should feel less generic.", 12, "周一", "DS", "reviewing", false, 0xF0E8FF, 0x6541A8),
        ChatPreview("chat-8", "Release Notes", "S4 planning starts after the S1-S3 UI stabilizes.", 0, "3月12日", "RN", "drafting summary", false, 0xFDEDDC, 0x9E6316),
        ChatPreview("chat-9", "Bot Sandbox", "Search should still find keywords inside preview text.", 4, "3月10日", "BS", "awaiting input", false, 0xE7F8F0, 0x25704A),
        ChatPreview("chat-10", "Mock Data Lab", "Need enough rows to force a real scroll region on small screens.", 0, "3月08日", "MD", "fixture owner", false, 0xECEFF3, 0x5D6875)
    ])
}
