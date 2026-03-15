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
    private val chats = mutableListOf(
        ChatSummary(
            id = "chat-1",
            title = "Telegram Compare",
            lastMessagePreview = "Bootstrap the KMP track",
            unreadCount = 3,
            lastMessageAtLabel = "09:24",
            avatarLabel = "TC",
        ),
        ChatSummary(
            id = "chat-2",
            title = "AI Infra",
            lastMessagePreview = "Need a reliable debug loop",
            unreadCount = 1,
            lastMessageAtLabel = "08:11",
            avatarLabel = "AI",
            isMuted = true,
        ),
        ChatSummary(
            id = "chat-3",
            title = "Product Design",
            lastMessagePreview = "S2 needs a Telegram-style search field.",
            unreadCount = 0,
            lastMessageAtLabel = "昨天",
            avatarLabel = "PD",
        ),
        ChatSummary(
            id = "chat-4",
            title = "KMP Delivery",
            lastMessagePreview = "Wire the shared-domain use cases first.",
            unreadCount = 6,
            lastMessageAtLabel = "周四",
            avatarLabel = "KD",
        ),
    )
    private var chatListScenario: ChatListScenario = ChatListScenario.DEFAULT
    private var refreshCount = 0
    private var nextSendShouldFail = false
    private var messageCounter = 8

    private val messagesByChat = mutableMapOf(
        "chat-1" to mutableListOf(
            Message(
                id = "message-1",
                chatId = "chat-1",
                text = "Spec is ready. Build the KMP shell next.",
                sentAtLabel = "09:08",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
            ),
            Message(
                id = "message-2",
                chatId = "chat-1",
                text = "Understood. I will keep the acceptance evidence synced.",
                sentAtLabel = "09:12",
                isOutgoing = true,
                deliveryState = DeliveryState.SENT,
            ),
            Message(
                id = "message-3",
                chatId = "chat-1",
                text = "Remember to log AI friction and blockers.",
                sentAtLabel = "09:24",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
            ),
        ),
        "chat-2" to mutableListOf(
            Message(
                id = "message-4",
                chatId = "chat-2",
                text = "Issue labels should distinguish common and KMP-specific friction.",
                sentAtLabel = "08:11",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
            ),
        ),
        "chat-3" to mutableListOf(
            Message(
                id = "message-5",
                chatId = "chat-3",
                text = "Default state and failure state should both be ready-for-dev.",
                sentAtLabel = "昨天",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
            ),
            Message(
                id = "message-6",
                chatId = "chat-3",
                text = "I'll keep the visual density close to Telegram.",
                sentAtLabel = "昨天",
                isOutgoing = true,
                deliveryState = DeliveryState.SENT,
            ),
        ),
        "chat-4" to mutableListOf(
            Message(
                id = "message-7",
                chatId = "chat-4",
                text = "Refresh needs a visible indicator and updated fixture data.",
                sentAtLabel = "周四",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
            ),
            Message(
                id = "message-8",
                chatId = "chat-4",
                text = "Agreed. I will wire the Android shell after the shared layer lands.",
                sentAtLabel = "周四",
                isOutgoing = true,
                deliveryState = DeliveryState.SENT,
            ),
        ),
    )

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
}
