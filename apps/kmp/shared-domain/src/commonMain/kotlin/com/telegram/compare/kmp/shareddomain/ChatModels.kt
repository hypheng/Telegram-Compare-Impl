package com.telegram.compare.kmp.shareddomain

data class ChatSummary(
    val id: String,
    val title: String,
    val lastMessagePreview: String,
    val unreadCount: Int,
    val lastMessageAtLabel: String,
    val avatarLabel: String,
    val isMuted: Boolean = false,
    val statusLabel: String = "",
    val avatarBackgroundColorHex: String = "#EAF3FB",
    val avatarTextColorHex: String = "#1F5F8B",
)

data class ChatListQuery(
    val keyword: String = "",
)

sealed interface ChatListLoadResult {
    data class Success(val chats: List<ChatSummary>) : ChatListLoadResult

    object Empty : ChatListLoadResult

    data class Failed(val message: String) : ChatListLoadResult
}

data class Message(
    val id: String,
    val chatId: String,
    val text: String,
    val sentAtLabel: String,
    val isOutgoing: Boolean,
    val deliveryState: DeliveryState,
    val mediaAttachment: MediaAttachment? = null,
)

data class ChatThread(
    val chat: ChatSummary,
    val messages: List<Message>,
)

sealed interface ChatDetailLoadResult {
    data class Success(val thread: ChatThread) : ChatDetailLoadResult

    data class Failed(val message: String) : ChatDetailLoadResult
}

sealed interface SendMessageResult {
    data class Success(
        val thread: ChatThread,
        val sentMessage: Message,
    ) : SendMessageResult

    data class InvalidInput(val message: String) : SendMessageResult

    data class Failed(
        val message: String,
        val thread: ChatThread? = null,
        val failedMessage: Message? = null,
    ) : SendMessageResult
}

sealed interface RetryMessageResult {
    data class Success(
        val thread: ChatThread,
        val retriedMessage: Message,
    ) : RetryMessageResult

    data class Failed(
        val message: String,
        val thread: ChatThread? = null,
    ) : RetryMessageResult
}

data class MediaAttachment(
    val id: String,
    val title: String,
    val defaultCaption: String,
    val accentColorHex: String,
)

sealed interface MediaPickerLoadResult {
    data class Success(val attachments: List<MediaAttachment>) : MediaPickerLoadResult

    data class Failed(val message: String) : MediaPickerLoadResult
}

sealed interface SendMediaResult {
    data class Success(
        val thread: ChatThread,
        val sentMessage: Message,
    ) : SendMediaResult

    data class Failed(
        val message: String,
        val thread: ChatThread? = null,
    ) : SendMediaResult
}

enum class DeliveryState {
    DRAFT,
    SENDING,
    SENT,
    FAILED,
}

interface ChatListRepository {
    fun loadChatList(query: ChatListQuery = ChatListQuery()): ChatListLoadResult

    fun refreshChatList(query: ChatListQuery = ChatListQuery()): ChatListLoadResult
}

interface ChatDetailRepository {
    fun loadChatDetail(chatId: String): ChatDetailLoadResult

    fun sendMessage(chatId: String, text: String): SendMessageResult

    fun retryMessage(chatId: String, messageId: String): RetryMessageResult

    fun loadAvailableMedia(): MediaPickerLoadResult

    fun sendMedia(chatId: String, mediaId: String): SendMediaResult
}
