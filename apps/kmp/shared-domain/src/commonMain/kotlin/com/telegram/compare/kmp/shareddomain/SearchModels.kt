package com.telegram.compare.kmp.shareddomain

data class SearchQuery(
    val keyword: String = "",
)

data class MessageSearchHit(
    val chat: ChatSummary,
    val message: Message,
)

sealed interface SearchLoadResult {
    data class Success(
        val chatResults: List<ChatSummary>,
        val messageResults: List<MessageSearchHit>,
    ) : SearchLoadResult

    object Empty : SearchLoadResult

    data class Failed(val message: String) : SearchLoadResult
}

interface SearchRepository {
    fun search(query: SearchQuery): SearchLoadResult
}
