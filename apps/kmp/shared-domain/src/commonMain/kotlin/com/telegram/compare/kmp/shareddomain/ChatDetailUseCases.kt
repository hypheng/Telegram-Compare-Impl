package com.telegram.compare.kmp.shareddomain

class LoadChatDetailUseCase(
    private val repository: ChatDetailRepository,
) {
    fun execute(chatId: String): ChatDetailLoadResult {
        val normalizedChatId = chatId.trim()
        if (normalizedChatId.isBlank()) {
            return ChatDetailLoadResult.Failed("未找到目标会话。")
        }

        return repository.loadChatDetail(chatId = normalizedChatId)
    }
}

class SendChatMessageUseCase(
    private val repository: ChatDetailRepository,
) {
    fun execute(
        chatId: String,
        text: String,
    ): SendMessageResult {
        val normalizedChatId = chatId.trim()
        if (normalizedChatId.isBlank()) {
            return SendMessageResult.InvalidInput("未找到目标会话。")
        }

        val normalizedText = text.trim()
        if (normalizedText.isBlank()) {
            return SendMessageResult.InvalidInput("请输入消息内容。")
        }

        return repository.sendMessage(
            chatId = normalizedChatId,
            text = normalizedText,
        )
    }
}

class RetryChatMessageUseCase(
    private val repository: ChatDetailRepository,
) {
    fun execute(
        chatId: String,
        messageId: String,
    ): RetryMessageResult {
        val normalizedChatId = chatId.trim()
        if (normalizedChatId.isBlank()) {
            return RetryMessageResult.Failed("未找到目标会话。")
        }

        val normalizedMessageId = messageId.trim()
        if (normalizedMessageId.isBlank()) {
            return RetryMessageResult.Failed("未找到可重试消息。")
        }

        return repository.retryMessage(
            chatId = normalizedChatId,
            messageId = normalizedMessageId,
        )
    }
}
