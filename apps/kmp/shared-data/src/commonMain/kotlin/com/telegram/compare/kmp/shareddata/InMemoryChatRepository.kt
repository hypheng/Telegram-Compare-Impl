package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatDetailRepository
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListQuery
import com.telegram.compare.kmp.shareddomain.ChatListRepository
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.Message
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.SendMessageResult

enum class ChatListScenario {
    DEFAULT,
    EMPTY,
    ERROR,
}

class InMemoryChatRepository : ChatDetailRepository, ChatListRepository {
    private val chats = buildDefaultChats().toMutableList()
    private var chatListScenario: ChatListScenario = ChatListScenario.DEFAULT
    private var refreshCount = 0
    private var nextSendShouldFail = false
    private var messageCounter = DEFAULT_MESSAGE_COUNTER

    private val messagesByChat = buildDefaultMessages()
        .mapValues { (_, messages) -> messages.toMutableList() }
        .toMutableMap()

    override fun loadChatList(query: ChatListQuery): ChatListLoadResult {
        return when (chatListScenario) {
            ChatListScenario.ERROR -> ChatListLoadResult.Failed("会话列表加载失败，请下拉重试。")
            ChatListScenario.EMPTY -> ChatListLoadResult.Empty
            ChatListScenario.DEFAULT -> resolveResult(
                chats = filterChats(keyword = query.keyword),
            )
        }
    }

    override fun refreshChatList(query: ChatListQuery): ChatListLoadResult {
        return when (chatListScenario) {
            ChatListScenario.ERROR -> ChatListLoadResult.Failed("刷新失败，请稍后再试。")
            ChatListScenario.EMPTY -> ChatListLoadResult.Empty
            ChatListScenario.DEFAULT -> {
                refreshCount += 1
                updateChatSummary(
                    chatId = "chat-1",
                    lastMessagePreview = "Refresh #$refreshCount completed for the KMP chat list.",
                    lastMessageAtLabel = "刚刚",
                    unreadCount = chats.first { it.id == "chat-1" }.unreadCount + 1,
                )
                resolveResult(
                    chats = filterChats(keyword = query.keyword),
                )
            }
        }
    }

    override fun loadChatDetail(chatId: String): ChatDetailLoadResult {
        return resolveThread(chatId)?.let { ChatDetailLoadResult.Success(it) }
            ?: ChatDetailLoadResult.Failed("未找到该会话。")
    }

    override fun sendMessage(
        chatId: String,
        text: String,
    ): SendMessageResult {
        if (resolveThread(chatId) == null) {
            return SendMessageResult.Failed("未找到目标会话。")
        }

        val nextMessage = Message(
            id = nextMessageId(),
            chatId = chatId,
            text = text,
            sentAtLabel = "刚刚",
            isOutgoing = true,
            deliveryState = if (nextSendShouldFail) DeliveryState.FAILED else DeliveryState.SENT,
        )
        val bucket = messagesByChat.getOrPut(chatId) { mutableListOf() }
        bucket += nextMessage

        updateChatSummary(
            chatId = chatId,
            lastMessagePreview = text,
            lastMessageAtLabel = "刚刚",
            unreadCount = 0,
        )
        moveChatToTop(chatId)

        val thread = resolveThread(chatId)
        nextSendShouldFail = false
        return if (nextMessage.deliveryState == DeliveryState.FAILED) {
            SendMessageResult.Failed(
                message = "发送失败，请点击重试。",
                thread = thread,
                failedMessage = nextMessage,
            )
        } else {
            SendMessageResult.Success(
                thread = requireNotNull(thread),
                sentMessage = nextMessage,
            )
        }
    }

    override fun retryMessage(
        chatId: String,
        messageId: String,
    ): RetryMessageResult {
        val bucket = messagesByChat[chatId]
            ?: return RetryMessageResult.Failed("未找到目标会话。")
        val targetIndex = bucket.indexOfFirst { it.id == messageId }
        if (targetIndex == -1) {
            return RetryMessageResult.Failed(
                message = "未找到可重试消息。",
                thread = resolveThread(chatId),
            )
        }

        val targetMessage = bucket[targetIndex]
        if (targetMessage.deliveryState != DeliveryState.FAILED) {
            return RetryMessageResult.Failed(
                message = "当前消息不需要重试。",
                thread = resolveThread(chatId),
            )
        }

        val retriedMessage = targetMessage.copy(
            sentAtLabel = "刚刚",
            deliveryState = DeliveryState.SENT,
        )
        bucket[targetIndex] = retriedMessage

        updateChatSummary(
            chatId = chatId,
            lastMessagePreview = retriedMessage.text,
            lastMessageAtLabel = "刚刚",
            unreadCount = 0,
        )
        moveChatToTop(chatId)

        return RetryMessageResult.Success(
            thread = requireNotNull(resolveThread(chatId)),
            retriedMessage = retriedMessage,
        )
    }

    fun setChatListScenario(next: ChatListScenario) {
        chatListScenario = next
    }

    fun currentChatListScenario(): ChatListScenario = chatListScenario

    fun restoreDefaultFixtures() {
        chats.clear()
        chats += buildDefaultChats()
        messagesByChat.clear()
        messagesByChat.putAll(
            buildDefaultMessages()
                .mapValues { (_, messages) -> messages.toMutableList() },
        )
        chatListScenario = ChatListScenario.DEFAULT
        refreshCount = 0
        nextSendShouldFail = false
        messageCounter = DEFAULT_MESSAGE_COUNTER
    }

    fun setNextSendShouldFail(enabled: Boolean) {
        nextSendShouldFail = enabled
    }

    fun nextSendWillFail(): Boolean = nextSendShouldFail

    private fun filterChats(keyword: String): List<ChatSummary> {
        if (keyword.isBlank()) {
            return chats.toList()
        }

        val normalized = keyword.trim()
        return chats.filter { chat ->
            chat.title.contains(normalized, ignoreCase = true) ||
                chat.lastMessagePreview.contains(normalized, ignoreCase = true)
        }
    }

    private fun resolveResult(chats: List<ChatSummary>): ChatListLoadResult {
        return if (chats.isEmpty()) {
            ChatListLoadResult.Empty
        } else {
            ChatListLoadResult.Success(chats)
        }
    }

    private fun resolveThread(chatId: String): ChatThread? {
        val chat = chats.find { it.id == chatId } ?: return null
        return ChatThread(
            chat = chat,
            messages = messagesByChat[chatId].orEmpty().toList(),
        )
    }

    private fun updateChatSummary(
        chatId: String,
        lastMessagePreview: String,
        lastMessageAtLabel: String,
        unreadCount: Int,
    ) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex == -1) {
            return
        }

        chats[chatIndex] = chats[chatIndex].copy(
            lastMessagePreview = lastMessagePreview,
            lastMessageAtLabel = lastMessageAtLabel,
            unreadCount = unreadCount,
        )
    }

    private fun moveChatToTop(chatId: String) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex <= 0) {
            return
        }

        val chat = chats.removeAt(chatIndex)
        chats.add(0, chat)
    }

    private fun nextMessageId(): String {
        messageCounter += 1
        return "message-$messageCounter"
    }

    private companion object {
        const val DEFAULT_MESSAGE_COUNTER = 24
    }
}

private fun buildDefaultChats(): List<ChatSummary> {
    return listOf(
        ChatSummary(
            id = "chat-1",
            title = "Telegram Compare",
            lastMessagePreview = "Refine the KMP shell before CJMP parity.",
            unreadCount = 3,
            lastMessageAtLabel = "09:24",
            avatarLabel = "TC",
            statusLabel = "last seen recently",
            avatarBackgroundColorHex = "#DDF0FF",
            avatarTextColorHex = "#0E5D91",
        ),
        ChatSummary(
            id = "chat-2",
            title = "AI Infra",
            lastMessagePreview = "Figma MCP is ready, waiting for editable frames.",
            unreadCount = 1,
            lastMessageAtLabel = "08:11",
            avatarLabel = "AI",
            isMuted = true,
            statusLabel = "syncing logs",
            avatarBackgroundColorHex = "#E8EAFD",
            avatarTextColorHex = "#4E5ABD",
        ),
        ChatSummary(
            id = "chat-3",
            title = "Product Design",
            lastMessagePreview = "S2 still needs a Telegram-style search field.",
            unreadCount = 0,
            lastMessageAtLabel = "昨天",
            avatarLabel = "PD",
            statusLabel = "reviewing handoff",
            avatarBackgroundColorHex = "#FFEBCD",
            avatarTextColorHex = "#A15E12",
        ),
        ChatSummary(
            id = "chat-4",
            title = "KMP Delivery",
            lastMessagePreview = "Wire the shared-domain use cases before polishing UI.",
            unreadCount = 6,
            lastMessageAtLabel = "周四",
            avatarLabel = "KD",
            statusLabel = "typing...",
            avatarBackgroundColorHex = "#E5F7EC",
            avatarTextColorHex = "#1D7A47",
        ),
        ChatSummary(
            id = "chat-5",
            title = "QA Evidence",
            lastMessagePreview = "Need another screenshot after the scroll-boundary fix.",
            unreadCount = 2,
            lastMessageAtLabel = "周三",
            avatarLabel = "QA",
            statusLabel = "online",
            avatarBackgroundColorHex = "#FFE4EC",
            avatarTextColorHex = "#A03B63",
        ),
        ChatSummary(
            id = "chat-6",
            title = "Growth Ops",
            lastMessagePreview = "Longer preview copy helps validate truncation in the row layout.",
            unreadCount = 0,
            lastMessageAtLabel = "周二",
            avatarLabel = "GO",
            statusLabel = "last seen 2h ago",
            avatarBackgroundColorHex = "#EAF7FF",
            avatarTextColorHex = "#2B6B98",
        ),
        ChatSummary(
            id = "chat-7",
            title = "Design Systems",
            lastMessagePreview = "Avatar colors and muted metadata should feel less generic.",
            unreadCount = 12,
            lastMessageAtLabel = "周一",
            avatarLabel = "DS",
            statusLabel = "reviewing",
            avatarBackgroundColorHex = "#F0E8FF",
            avatarTextColorHex = "#6541A8",
        ),
        ChatSummary(
            id = "chat-8",
            title = "Release Notes",
            lastMessagePreview = "S4 planning starts after the S1-S3 UI stabilizes.",
            unreadCount = 0,
            lastMessageAtLabel = "3月12日",
            avatarLabel = "RN",
            statusLabel = "drafting summary",
            avatarBackgroundColorHex = "#FDEDDC",
            avatarTextColorHex = "#9E6316",
        ),
        ChatSummary(
            id = "chat-9",
            title = "Bot Sandbox",
            lastMessagePreview = "Search should still find keywords inside preview text.",
            unreadCount = 4,
            lastMessageAtLabel = "3月10日",
            avatarLabel = "BS",
            statusLabel = "awaiting input",
            avatarBackgroundColorHex = "#E7F8F0",
            avatarTextColorHex = "#25704A",
        ),
        ChatSummary(
            id = "chat-10",
            title = "Mock Data Lab",
            lastMessagePreview = "Need enough rows to force a real scroll region on small screens.",
            unreadCount = 0,
            lastMessageAtLabel = "3月08日",
            avatarLabel = "MD",
            statusLabel = "fixture owner",
            avatarBackgroundColorHex = "#ECEFF3",
            avatarTextColorHex = "#5D6875",
        ),
    )
}

private fun buildDefaultMessages(): Map<String, List<Message>> {
    return mapOf(
        "chat-1" to listOf(
            Message("message-1", "chat-1", "Spec is ready. Build the KMP shell next.", "09:02", false, DeliveryState.SENT),
            Message("message-2", "chat-1", "Understood. I will keep the acceptance evidence synced.", "09:05", true, DeliveryState.SENT),
            Message("message-3", "chat-1", "The whole page still scrolls. Fix the viewport boundaries first.", "09:09", false, DeliveryState.SENT),
            Message("message-4", "chat-1", "I am moving top chrome and composer out of the scroll container.", "09:11", true, DeliveryState.SENT),
            Message("message-5", "chat-1", "Good. The list should feel denser and closer to Telegram.", "09:13", false, DeliveryState.SENT),
            Message("message-6", "chat-1", "I will tighten row spacing and flatten the visual treatment.", "09:15", true, DeliveryState.SENT),
            Message("message-7", "chat-1", "Do not forget richer fixture data for long titles and previews.", "09:17", false, DeliveryState.SENT),
            Message("message-8", "chat-1", "Adding more chats and longer copy now.", "09:18", true, DeliveryState.SENT),
            Message("message-9", "chat-1", "Composer must stay pinned while the thread keeps scrolling.", "09:20", false, DeliveryState.SENT),
            Message("message-10", "chat-1", "That will also make send and retry states much easier to read.", "09:22", true, DeliveryState.SENT),
            Message("message-11", "chat-1", "Remember to log AI friction and blockers.", "09:23", false, DeliveryState.SENT),
            Message("message-12", "chat-1", "Refine the KMP shell before CJMP parity.", "09:24", true, DeliveryState.SENT),
        ),
        "chat-2" to listOf(
            Message("message-13", "chat-2", "Issue labels should distinguish common and KMP-specific friction.", "08:01", false, DeliveryState.SENT),
            Message("message-14", "chat-2", "Agreed. The design blocker is no longer MCP access, it is the missing editable file.", "08:06", true, DeliveryState.SENT),
            Message("message-15", "chat-2", "Keep the infra summary concise in each acceptance note.", "08:11", false, DeliveryState.SENT),
        ),
        "chat-3" to listOf(
            Message("message-16", "chat-3", "Default state and failure state should both be ready-for-dev.", "昨天", false, DeliveryState.SENT),
            Message("message-17", "chat-3", "I will keep the visual density close to Telegram.", "昨天", true, DeliveryState.SENT),
            Message("message-18", "chat-3", "The search field still needs better native spacing.", "昨天", false, DeliveryState.SENT),
        ),
        "chat-4" to listOf(
            Message("message-19", "chat-4", "Refresh needs a visible indicator and updated fixture data.", "周四", false, DeliveryState.SENT),
            Message("message-20", "chat-4", "Agreed. I will wire the Android shell after the shared layer lands.", "周四", true, DeliveryState.SENT),
            Message("message-21", "chat-4", "Do not let debug controls dominate the viewport.", "周四", false, DeliveryState.SENT),
        ),
        "chat-5" to listOf(
            Message("message-22", "chat-5", "Capture another screenshot once the fixed footer is in place.", "周三", false, DeliveryState.SENT),
            Message("message-23", "chat-5", "Will do after the layout rebuild passes assembleDebug.", "周三", true, DeliveryState.SENT),
        ),
        "chat-10" to listOf(
            Message("message-24", "chat-10", "Need enough rows to force a real scroll region.", "3月08日", false, DeliveryState.SENT),
        ),
    )
}
