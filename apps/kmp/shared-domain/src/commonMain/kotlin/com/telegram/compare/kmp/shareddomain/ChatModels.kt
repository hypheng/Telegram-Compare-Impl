package com.telegram.compare.kmp.shareddomain

data class ChatSummary(
    val id: String,
    val title: String,
    val unreadCount: Int,
    val lastMessagePreview: String,
)

data class Message(
    val id: String,
    val chatId: String,
    val text: String,
    val deliveryState: DeliveryState,
)

enum class DeliveryState {
    DRAFT,
    SENDING,
    SENT,
    FAILED,
}

interface ChatRepository {
    fun listChats(): List<ChatSummary>
    fun listMessages(chatId: String): List<Message>
    fun sendMessage(chatId: String, text: String): Message
}
