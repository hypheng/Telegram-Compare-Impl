package com.telegram.compare.kmp.shareddomain

class SearchChatsAndMessagesUseCase(
    private val repository: SearchRepository,
) {
    fun execute(query: String): SearchLoadResult {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) {
            return SearchLoadResult.Empty
        }

        return repository.search(
            query = SearchQuery(keyword = normalizedQuery),
        )
    }
}
