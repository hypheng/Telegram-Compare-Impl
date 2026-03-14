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

data class KmpBootstrapSummary(
    val firstSlice: String,
    val chatCount: Int,
    val messageCount: Int,
    val moduleOrder: List<String>,
    val nextSteps: List<String>,
)

class BuildBootstrapSummaryUseCase(
    private val repository: ChatRepository,
) {
    fun execute(chatId: String): KmpBootstrapSummary {
        val chats = repository.listChats()
        val messages = repository.listMessages(chatId)

        return KmpBootstrapSummary(
            firstSlice = "S3 单聊详情与文本发送",
            chatCount = chats.size,
            messageCount = messages.size,
            moduleOrder = listOf("shared-domain", "shared-data", "androidApp", "iosApp"),
            nextSteps = listOf(
                "Implement S1/S2/S3 domain contracts in shared-domain",
                "Add storage and network adapters in shared-data",
                "Replace the Android placeholder screen with slice-specific UI",
                "Add the iOS host shell after the first slice is stable",
            ),
        )
    }
}
