package com.telegram.compare.kmp.shareddomain

data class ContactSummary(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    val avatarLabel: String,
    val statusLabel: String = "",
    val hasExistingChat: Boolean = false,
)

data class ContactsQuery(
    val keyword: String = "",
)

sealed interface ContactsLoadResult {
    data class Success(val contacts: List<ContactSummary>) : ContactsLoadResult

    object Empty : ContactsLoadResult

    data class Failed(val message: String) : ContactsLoadResult
}

sealed interface OpenContactChatResult {
    data class Success(
        val chat: ChatSummary,
        val isNewChat: Boolean,
    ) : OpenContactChatResult

    data class Failed(val message: String) : OpenContactChatResult
}

interface ContactsRepository {
    fun loadContacts(query: ContactsQuery = ContactsQuery()): ContactsLoadResult

    fun openContactChat(contactId: String): OpenContactChatResult
}
