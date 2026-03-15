package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.MediaPickerLoadResult
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.SearchLoadResult
import com.telegram.compare.kmp.shareddomain.SearchQuery
import com.telegram.compare.kmp.shareddomain.SendMediaResult
import com.telegram.compare.kmp.shareddomain.SendMessageResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRequest
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRestoreResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRoute
import com.telegram.compare.kmp.shareddomain.SyncSnapshotSaveResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InMemoryChatRepositoryTest {
    @Test
    fun appendsSentMessageToChatHistory() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        val repository = fixtureBundle.chatDetailRepository

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
        val repository = InMemoryChatFixtureBundle().chatListRepository

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
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setChatListScenario(ChatListScenario.EMPTY)
        val repository = fixtureBundle.chatListRepository

        val result = repository.loadChatList()

        assertEquals(ChatListLoadResult.Empty, result)
    }

    @Test
    fun returnsFailedWhenScenarioIsError() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setChatListScenario(ChatListScenario.ERROR)
        val repository = fixtureBundle.chatListRepository

        val result = repository.refreshChatList()

        assertEquals(
            ChatListLoadResult.Failed("刷新失败，请稍后再试。"),
            result,
        )
    }

    @Test
    fun refreshUpdatesTopChatPreview() {
        val repository = InMemoryChatFixtureBundle().chatListRepository

        repository.refreshChatList()
        val result = repository.loadChatList()

        assertIs<ChatListLoadResult.Success>(result)
        assertTrue(result.chats.first().lastMessagePreview.contains("Refresh #1"))
        assertEquals("刚刚", result.chats.first().lastMessageAtLabel)
    }

    @Test
    fun searchesChatsAndMessagesAcrossFixtures() {
        val repository = InMemoryChatFixtureBundle().searchRepository

        val result = repository.search(
            query = SearchQuery(keyword = "settings"),
        )

        assertIs<SearchLoadResult.Success>(result)
        assertEquals(listOf("Telegram Compare"), result.chatResults.map { it.title })
        assertTrue(result.messageResults.any { hit ->
            hit.chat.id == "chat-1" && hit.message.text.contains("settings", ignoreCase = true)
        })
    }

    @Test
    fun returnsSearchFailureWhenListScenarioIsError() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setChatListScenario(ChatListScenario.ERROR)
        val repository = fixtureBundle.searchRepository

        val result = repository.search(
            query = SearchQuery(keyword = "viewport"),
        )

        assertEquals(
            SearchLoadResult.Failed("搜索暂不可用，请稍后重试。"),
            result,
        )
    }

    @Test
    fun loadsFixtureMediaPickerOptions() {
        val repository = InMemoryChatFixtureBundle().chatDetailRepository

        val result = repository.loadAvailableMedia()

        assertIs<MediaPickerLoadResult.Success>(result)
        assertEquals(listOf("media-1", "media-2", "media-3"), result.attachments.map { it.id })
    }

    @Test
    fun sendsMediaAndUpdatesPreview() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        val detailRepository = fixtureBundle.chatDetailRepository
        val listRepository = fixtureBundle.chatListRepository

        val result = detailRepository.sendMedia(
            chatId = "chat-2",
            mediaId = "media-3",
        )
        val detail = detailRepository.loadChatDetail("chat-2")
        val list = listRepository.loadChatList()

        assertIs<SendMediaResult.Success>(result)
        assertIs<ChatDetailLoadResult.Success>(detail)
        assertIs<ChatListLoadResult.Success>(list)
        assertEquals("media-3", result.sentMessage.mediaAttachment?.id)
        assertEquals("Photo · Media picker board for the S7 acceptance path.", detail.thread.chat.lastMessagePreview)
        assertEquals("chat-2", list.chats.first().id)
    }

    @Test
    fun returnsFailedMessageWhenNextSendIsForcedToFail() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setNextSendShouldFail(true)
        val repository = fixtureBundle.chatDetailRepository

        val result = repository.sendMessage(
            chatId = "chat-2",
            text = "This send should fail",
        )

        assertIs<SendMessageResult.Failed>(result)
        assertEquals(DeliveryState.FAILED, result.failedMessage?.deliveryState)
        assertEquals(false, fixtureBundle.debugController.nextSendWillFail())
    }

    @Test
    fun retryTurnsFailedMessageIntoSent() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setNextSendShouldFail(true)
        val repository = fixtureBundle.chatDetailRepository
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

    @Test
    fun restoresSavedDetailSnapshotIntoFreshRepository() {
        val storage = InMemorySyncSnapshotStorage()
        val firstBundle = InMemoryChatFixtureBundle(snapshotStorage = storage)
        firstBundle.chatListRepository.refreshChatList()
        firstBundle.chatDetailRepository.sendMedia(chatId = "chat-1", mediaId = "media-1")

        val saved = firstBundle.syncRepository.saveSnapshot(
            SyncSnapshotRequest(
                route = SyncSnapshotRoute.CHAT_DETAIL,
                searchKeyword = "telegram",
                selectedChatId = "chat-1",
            ),
        )

        assertIs<SyncSnapshotSaveResult.Success>(saved)

        val restoredBundle = InMemoryChatFixtureBundle(snapshotStorage = storage)
        val restored = restoredBundle.syncRepository.restoreSnapshot()
        val detail = restoredBundle.chatDetailRepository.loadChatDetail("chat-1")

        assertIs<SyncSnapshotRestoreResult.Restored>(restored)
        assertEquals(SyncSnapshotRoute.CHAT_DETAIL, restored.snapshot.route)
        assertEquals("telegram", restored.snapshot.searchKeyword)
        assertIs<ChatDetailLoadResult.Success>(detail)
        assertTrue(detail.thread.messages.any { it.mediaAttachment?.id == "media-1" })
        assertTrue(detail.thread.chat.lastMessagePreview.contains("Photo"))
    }
}
