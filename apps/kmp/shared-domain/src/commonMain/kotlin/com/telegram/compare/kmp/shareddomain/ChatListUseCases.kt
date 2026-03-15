package com.telegram.compare.kmp.shareddomain

class LoadChatListUseCase(
    private val repository: ChatListRepository,
) {
    fun execute(query: String = ""): ChatListLoadResult {
        return repository.loadChatList(
            query = ChatListQuery(keyword = query.trim()),
        )
    }
}

class RefreshChatListUseCase(
    private val repository: ChatListRepository,
) {
    fun execute(query: String = ""): ChatListLoadResult {
        return repository.refreshChatList(
            query = ChatListQuery(keyword = query.trim()),
        )
    }
}
