package com.telegram.compare.kmp.shareddata

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InMemoryChatRepositoryTest {
    @Test
    fun appendsSentMessageToChatHistory() {
        val repository = InMemoryChatRepository()

        val before = repository.listMessages("chat-1").size
        val message = repository.sendMessage(chatId = "chat-1", text = "AI log updated")
        val after = repository.listMessages("chat-1")

        assertEquals(before + 1, after.size)
        assertEquals("AI log updated", message.text)
        assertTrue(after.any { it.id == message.id })
    }
}
