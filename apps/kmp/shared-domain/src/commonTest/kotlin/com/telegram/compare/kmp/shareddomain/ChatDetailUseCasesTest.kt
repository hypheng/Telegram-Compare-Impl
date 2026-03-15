package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ChatDetailUseCasesTest {
    @Test
    fun trimsChatIdBeforeLoadingDetail() {
        val repository = FakeChatDetailRepository()

        LoadChatDetailUseCase(repository).execute("  chat-42  ")

        assertEquals("chat-42", repository.lastLoadedChatId)
    }

    @Test
    fun trimsMessageTextBeforeSending() {
        val repository = FakeChatDetailRepository()

        SendChatMessageUseCase(repository).execute(
            chatId = " chat-42 ",
            text = "  hello  ",
        )

        assertEquals("chat-42", repository.lastSentChatId)
        assertEquals("hello", repository.lastSentText)
    }

    @Test
    fun rejectsBlankMessageBeforeCallingRepository() {
        val repository = FakeChatDetailRepository()

        val result = SendChatMessageUseCase(repository).execute(
            chatId = "chat-42",
            text = "   ",
        )

        assertIs<SendMessageResult.InvalidInput>(result)
        assertEquals(null, repository.lastSentText)
    }

    @Test
    fun trimsMessageIdBeforeRetrying() {
        val repository = FakeChatDetailRepository()

        RetryChatMessageUseCase(repository).execute(
            chatId = " chat-42 ",
            messageId = " message-9 ",
        )

        assertEquals("chat-42", repository.lastRetriedChatId)
        assertEquals("message-9", repository.lastRetriedMessageId)
    }
}

private class FakeChatDetailRepository : ChatDetailRepository {
    var lastLoadedChatId: String? = null
    var lastSentChatId: String? = null
    var lastSentText: String? = null
    var lastRetriedChatId: String? = null
    var lastRetriedMessageId: String? = null

    override fun loadChatDetail(chatId: String): ChatDetailLoadResult {
        lastLoadedChatId = chatId
        return ChatDetailLoadResult.Failed("stub")
    }

    override fun sendMessage(
        chatId: String,
        text: String,
    ): SendMessageResult {
        lastSentChatId = chatId
        lastSentText = text
        return SendMessageResult.InvalidInput("stub")
    }

    override fun retryMessage(
        chatId: String,
        messageId: String,
    ): RetryMessageResult {
        lastRetriedChatId = chatId
        lastRetriedMessageId = messageId
        return RetryMessageResult.Failed("stub")
    }

    override fun loadAvailableMedia(): MediaPickerLoadResult {
        return MediaPickerLoadResult.Failed("stub")
    }

    override fun sendMedia(
        chatId: String,
        mediaId: String,
    ): SendMediaResult {
        return SendMediaResult.Failed("stub")
    }
}
