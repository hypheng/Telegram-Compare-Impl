package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class ChatListUseCasesTest {
    @Test
    fun trimsSearchQueryBeforePassingItToRepository() {
        val repository = FakeChatListRepository()

        LoadChatListUseCase(repository).execute("  infra  ")

        assertEquals("infra", repository.lastLoadQuery)
    }

    @Test
    fun trimsRefreshQueryBeforePassingItToRepository() {
        val repository = FakeChatListRepository()

        RefreshChatListUseCase(repository).execute("  telegram  ")

        assertEquals("telegram", repository.lastRefreshQuery)
    }
}

private class FakeChatListRepository : ChatListRepository {
    var lastLoadQuery: String? = null
    var lastRefreshQuery: String? = null

    override fun loadChatList(query: ChatListQuery): ChatListLoadResult {
        lastLoadQuery = query.keyword
        return ChatListLoadResult.Empty
    }

    override fun refreshChatList(query: ChatListQuery): ChatListLoadResult {
        lastRefreshQuery = query.keyword
        return ChatListLoadResult.Empty
    }
}
