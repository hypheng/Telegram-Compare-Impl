package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.ContactsLoadResult
import com.telegram.compare.kmp.shareddomain.ContactsQuery
import com.telegram.compare.kmp.shareddomain.OpenContactChatResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InMemoryContactsRepositoryTest {
    @Test
    fun filtersContactsByNameAndPhone() {
        val repository = InMemoryChatFixtureBundle().contactsRepository

        val byName = repository.loadContacts(ContactsQuery(keyword = "nora"))
        val byPhone = repository.loadContacts(ContactsQuery(keyword = "0108"))

        assertIs<ContactsLoadResult.Success>(byName)
        assertEquals(listOf("Nora Chen"), byName.contacts.map { it.displayName })
        assertIs<ContactsLoadResult.Success>(byPhone)
        assertEquals(listOf("Mock Data Lab"), byPhone.contacts.map { it.displayName })
    }

    @Test
    fun returnsFailedWhenContactsScenarioIsError() {
        val fixtureBundle = InMemoryChatFixtureBundle()
        fixtureBundle.debugController.setContactListScenario(ContactListScenario.ERROR)

        val result = fixtureBundle.contactsRepository.loadContacts()

        assertEquals(
            ContactsLoadResult.Failed("联系人加载失败，请稍后重试。"),
            result,
        )
    }

    @Test
    fun opensExistingContactChatWithoutCreatingNewThread() {
        val fixtureBundle = InMemoryChatFixtureBundle()

        val result = fixtureBundle.contactsRepository.openContactChat("contact-2")
        val detail = fixtureBundle.chatDetailRepository.loadChatDetail("chat-2")

        assertIs<OpenContactChatResult.Success>(result)
        assertEquals(false, result.isNewChat)
        assertEquals("chat-2", result.chat.id)
        assertIs<ChatDetailLoadResult.Success>(detail)
    }

    @Test
    fun createsNewThreadWhenOpeningContactWithoutExistingChat() {
        val fixtureBundle = InMemoryChatFixtureBundle()

        val result = fixtureBundle.contactsRepository.openContactChat("contact-5")
        val contacts = fixtureBundle.contactsRepository.loadContacts()
        val chats = fixtureBundle.chatListRepository.loadChatList()

        assertIs<OpenContactChatResult.Success>(result)
        assertEquals(true, result.isNewChat)
        assertEquals("Nora Chen", result.chat.title)
        assertIs<ContactsLoadResult.Success>(contacts)
        assertTrue(contacts.contacts.first { it.id == "contact-5" }.hasExistingChat)
        assertIs<ChatListLoadResult.Success>(chats)
        assertEquals("Nora Chen", chats.chats.first().title)
        assertTrue(chats.chats.first().lastMessagePreview.contains("Contacts"))
    }

    @Test
    fun restoringContactsScenarioDoesNotDiscardNewlyCreatedChat() {
        val fixtureBundle = InMemoryChatFixtureBundle()

        fixtureBundle.contactsRepository.openContactChat("contact-5")
        fixtureBundle.debugController.setContactListScenario(ContactListScenario.ERROR)
        fixtureBundle.debugController.restoreDefaultContactScenario()

        val contacts = fixtureBundle.contactsRepository.loadContacts()
        val chats = fixtureBundle.chatListRepository.loadChatList()

        assertIs<ContactsLoadResult.Success>(contacts)
        assertTrue(contacts.contacts.first { it.id == "contact-5" }.hasExistingChat)
        assertIs<ChatListLoadResult.Success>(chats)
        assertEquals("Nora Chen", chats.chats.first().title)
    }
}
