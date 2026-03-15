package com.telegram.compare.kmp.shareddomain

class LoadAvailableMediaUseCase(
    private val repository: ChatDetailRepository,
) {
    fun execute(): MediaPickerLoadResult {
        return repository.loadAvailableMedia()
    }
}

class SendChatMediaUseCase(
    private val repository: ChatDetailRepository,
) {
    fun execute(
        chatId: String,
        mediaId: String,
    ): SendMediaResult {
        val normalizedChatId = chatId.trim()
        if (normalizedChatId.isBlank()) {
            return SendMediaResult.Failed("未找到目标会话。")
        }

        val normalizedMediaId = mediaId.trim()
        if (normalizedMediaId.isBlank()) {
            return SendMediaResult.Failed("未找到可发送的图片。")
        }

        return repository.sendMedia(
            chatId = normalizedChatId,
            mediaId = normalizedMediaId,
        )
    }
}
