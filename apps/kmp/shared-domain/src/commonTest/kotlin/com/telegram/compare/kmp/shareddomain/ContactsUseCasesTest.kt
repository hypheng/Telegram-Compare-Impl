package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class ContactsUseCasesTest {
    @Test
    fun trimsContactsQueryBeforePassingItToRepository() {
        val repository = FakeContactsRepository()

        LoadContactsUseCase(repository).execute("  nora  ")

        assertEquals("nora", repository.lastQuery)
    }

    @Test
    fun failsWhenContactIdIsBlank() {
        val repository = FakeContactsRepository()

        val result = OpenContactChatUseCase(repository).execute("   ")

        assertEquals(OpenContactChatResult.Failed("未找到目标联系人。"), result)
        assertEquals(null, repository.lastContactId)
    }
}

private class FakeContactsRepository : ContactsRepository {
    var lastQuery: String? = null
    var lastContactId: String? = null

    override fun loadContacts(query: ContactsQuery): ContactsLoadResult {
        lastQuery = query.keyword
        return ContactsLoadResult.Empty
    }

    override fun openContactChat(contactId: String): OpenContactChatResult {
        lastContactId = contactId
        return OpenContactChatResult.Failed("unused")
    }
}
