package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class MediaUseCasesTest {
    @Test
    fun trimsIdentifiersBeforeSendingMedia() {
        val repository = FakeMediaRepository()

        SendChatMediaUseCase(repository).execute(
            chatId = "  chat-1  ",
            mediaId = "  media-1  ",
        )

        assertEquals("chat-1", repository.lastChatId)
        assertEquals("media-1", repository.lastMediaId)
    }

    @Test
    fun failsWhenMediaIdIsBlank() {
        val repository = FakeMediaRepository()

        val result = SendChatMediaUseCase(repository).execute(
            chatId = "chat-1",
            mediaId = "   ",
        )

        assertEquals(SendMediaResult.Failed("未找到可发送的图片。"), result)
        assertEquals(null, repository.lastMediaId)
    }
}

private class FakeMediaRepository : ChatDetailRepository {
    var lastChatId: String? = null
    var lastMediaId: String? = null

    override fun loadChatDetail(chatId: String): ChatDetailLoadResult = ChatDetailLoadResult.Failed("unused")

    override fun sendMessage(chatId: String, text: String): SendMessageResult = SendMessageResult.Failed("unused")

    override fun retryMessage(chatId: String, messageId: String): RetryMessageResult = RetryMessageResult.Failed("unused")

    override fun loadAvailableMedia(): MediaPickerLoadResult = MediaPickerLoadResult.Failed("unused")

    override fun sendMedia(chatId: String, mediaId: String): SendMediaResult {
        lastChatId = chatId
        lastMediaId = mediaId
        return SendMediaResult.Failed("unused")
    }
}
