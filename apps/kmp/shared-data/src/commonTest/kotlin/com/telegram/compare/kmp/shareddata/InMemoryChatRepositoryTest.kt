package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.SendMessageResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InMemoryChatRepositoryTest {
    @Test
    fun appendsSentMessageToChatHistory() {
        val repository = InMemoryChatRepository()

        val before = repository.loadChatDetail("chat-1")
        val message = repository.sendMessage(chatId = "chat-1", text = "AI log updated")
        val after = repository.loadChatDetail("chat-1")

        assertIs<ChatDetailLoadResult.Success>(before)
        assertIs<SendMessageResult.Success>(message)
        assertIs<ChatDetailLoadResult.Success>(after)
        assertEquals(before.thread.messages.size + 1, after.thread.messages.size)
        assertEquals("AI log updated", message.sentMessage.text)
        assertTrue(after.thread.messages.any { it.id == message.sentMessage.id })
    }

    @Test
    fun filtersChatsByTitleAndPreview() {
        val repository = InMemoryChatRepository()

        val byTitle = repository.loadChatList(
            query = com.telegram.compare.kmp.shareddomain.ChatListQuery(keyword = "infra"),
        )
        val byPreview = repository.loadChatList(
            query = com.telegram.compare.kmp.shareddomain.ChatListQuery(keyword = "search field"),
        )

        assertIs<ChatListLoadResult.Success>(byTitle)
        assertEquals(listOf("AI Infra"), byTitle.chats.map { it.title })
        assertIs<ChatListLoadResult.Success>(byPreview)
        assertEquals(listOf("Product Design"), byPreview.chats.map { it.title })
    }

    @Test
    fun returnsEmptyWhenScenarioIsEmpty() {
        val repository = InMemoryChatRepository()
        repository.setChatListScenario(ChatListScenario.EMPTY)

        val result = repository.loadChatList()

        assertEquals(ChatListLoadResult.Empty, result)
    }

    @Test
    fun returnsFailedWhenScenarioIsError() {
        val repository = InMemoryChatRepository()
        repository.setChatListScenario(ChatListScenario.ERROR)

        val result = repository.refreshChatList()

        assertEquals(
            ChatListLoadResult.Failed("刷新失败，请稍后再试。"),
            result,
        )
    }

    @Test
    fun refreshUpdatesTopChatPreview() {
        val repository = InMemoryChatRepository()

        repository.refreshChatList()
        val result = repository.loadChatList()

        assertIs<ChatListLoadResult.Success>(result)
        assertTrue(result.chats.first().lastMessagePreview.contains("Refresh #1"))
        assertEquals("刚刚", result.chats.first().lastMessageAtLabel)
    }

    @Test
    fun returnsFailedMessageWhenNextSendIsForcedToFail() {
        val repository = InMemoryChatRepository()
        repository.setNextSendShouldFail(true)

        val result = repository.sendMessage(
            chatId = "chat-2",
            text = "This send should fail",
        )

        assertIs<SendMessageResult.Failed>(result)
        assertEquals(DeliveryState.FAILED, result.failedMessage?.deliveryState)
        assertEquals(false, repository.nextSendWillFail())
    }

    @Test
    fun retryTurnsFailedMessageIntoSent() {
        val repository = InMemoryChatRepository()
        repository.setNextSendShouldFail(true)
        val failed = repository.sendMessage(
            chatId = "chat-2",
            text = "Retry me",
        )

        assertIs<SendMessageResult.Failed>(failed)
        val retry = repository.retryMessage(
            chatId = "chat-2",
            messageId = requireNotNull(failed.failedMessage).id,
        )

        assertIs<RetryMessageResult.Success>(retry)
        assertEquals(DeliveryState.SENT, retry.retriedMessage.deliveryState)
        assertEquals("Retry me", retry.thread.chat.lastMessagePreview)
    }
}
