package com.telegram.compare.kmp.shareddomain

class LoadContactsUseCase(
    private val repository: ContactsRepository,
) {
    fun execute(query: String = ""): ContactsLoadResult {
        return repository.loadContacts(
            query = ContactsQuery(keyword = query.trim()),
        )
    }
}

class OpenContactChatUseCase(
    private val repository: ContactsRepository,
) {
    fun execute(contactId: String): OpenContactChatResult {
        val normalizedContactId = contactId.trim()
        if (normalizedContactId.isBlank()) {
            return OpenContactChatResult.Failed("未找到目标联系人。")
        }

        return repository.openContactChat(contactId = normalizedContactId)
    }
}
