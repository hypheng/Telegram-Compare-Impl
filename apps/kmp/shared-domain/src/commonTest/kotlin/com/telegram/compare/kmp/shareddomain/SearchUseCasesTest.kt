package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class SearchUseCasesTest {
    @Test
    fun trimsSearchQueryBeforePassingItToRepository() {
        val repository = FakeSearchRepository()

        SearchChatsAndMessagesUseCase(repository).execute("  telegram  ")

        assertEquals("telegram", repository.lastQuery)
    }

    @Test
    fun returnsEmptyWhenQueryIsBlankAfterTrim() {
        val repository = FakeSearchRepository()

        val result = SearchChatsAndMessagesUseCase(repository).execute("   ")

        assertEquals(SearchLoadResult.Empty, result)
        assertEquals(null, repository.lastQuery)
    }
}

private class FakeSearchRepository : SearchRepository {
    var lastQuery: String? = null

    override fun search(query: SearchQuery): SearchLoadResult {
        lastQuery = query.keyword
        return SearchLoadResult.Empty
    }
}
