package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatRepository
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.Message

class InMemoryChatRepository : ChatRepository {
    private val chats = listOf(
        ChatSummary(
            id = "chat-1",
            title = "Telegram Compare",
            unreadCount = 3,
            lastMessagePreview = "Bootstrap the KMP track",
        ),
        ChatSummary(
            id = "chat-2",
            title = "AI Infra",
            unreadCount = 1,
            lastMessagePreview = "Need a reliable debug loop",
        ),
    )

    private val messagesByChat = mutableMapOf(
        "chat-1" to mutableListOf(
            Message(
                id = "message-1",
                chatId = "chat-1",
                text = "Spec is ready. Build the KMP shell next.",
                deliveryState = DeliveryState.SENT,
            ),
            Message(
                id = "message-2",
                chatId = "chat-1",
                text = "Remember to log AI friction and blockers.",
                deliveryState = DeliveryState.SENT,
            ),
        ),
        "chat-2" to mutableListOf(
            Message(
                id = "message-3",
                chatId = "chat-2",
                text = "Issue labels should distinguish common and KMP-specific friction.",
                deliveryState = DeliveryState.SENT,
            ),
        ),
    )

    override fun listChats(): List<ChatSummary> = chats

    override fun listMessages(chatId: String): List<Message> {
        return messagesByChat[chatId].orEmpty()
    }

    override fun sendMessage(chatId: String, text: String): Message {
        val nextMessage = Message(
            id = "message-${messagesByChat.values.sumOf { it.size } + 1}",
            chatId = chatId,
            text = text,
            deliveryState = DeliveryState.SENT,
        )
        val bucket = messagesByChat.getOrPut(chatId) { mutableListOf() }
        bucket += nextMessage
        return nextMessage
    }
}
