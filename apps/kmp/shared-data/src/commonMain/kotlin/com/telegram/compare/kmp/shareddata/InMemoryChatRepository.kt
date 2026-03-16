package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatDetailRepository
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListQuery
import com.telegram.compare.kmp.shareddomain.ChatListRepository
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.ContactSummary
import com.telegram.compare.kmp.shareddomain.ContactsLoadResult
import com.telegram.compare.kmp.shareddomain.ContactsQuery
import com.telegram.compare.kmp.shareddomain.ContactsRepository
import com.telegram.compare.kmp.shareddomain.ContactSnapshot
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.MediaAttachment
import com.telegram.compare.kmp.shareddomain.MediaPickerLoadResult
import com.telegram.compare.kmp.shareddomain.Message
import com.telegram.compare.kmp.shareddomain.MessageSearchHit
import com.telegram.compare.kmp.shareddomain.OpenContactChatResult
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.SearchLoadResult
import com.telegram.compare.kmp.shareddomain.SearchQuery
import com.telegram.compare.kmp.shareddomain.SearchRepository
import com.telegram.compare.kmp.shareddomain.SendMediaResult
import com.telegram.compare.kmp.shareddomain.SendMessageResult
import com.telegram.compare.kmp.shareddomain.SyncRepository
import com.telegram.compare.kmp.shareddomain.SyncSnapshot
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRequest
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRestoreResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRoute
import com.telegram.compare.kmp.shareddomain.SyncSnapshotSaveResult

enum class ChatListScenario {
    DEFAULT,
    EMPTY,
    ERROR,
}

enum class ContactListScenario {
    DEFAULT,
    EMPTY,
    ERROR,
}

class InMemoryChatFixtureBundle(
    snapshotStorage: SyncSnapshotStorage = InMemorySyncSnapshotStorage(),
) {
    private val store = InMemoryChatStore(snapshotStorage)

    val chatListRepository: ChatListRepository = InMemoryChatListRepository(store)
    val chatDetailRepository: ChatDetailRepository = InMemoryChatDetailRepository(store)
    val contactsRepository: ContactsRepository = InMemoryContactsRepository(store)
    val searchRepository: SearchRepository = InMemorySearchRepository(store)
    val syncRepository: SyncRepository = InMemorySyncRepository(store)
    val debugController = InMemoryChatDebugController(store)
}

class InMemoryChatDebugController internal constructor(
    private val store: InMemoryChatStore,
) {
    fun setChatListScenario(next: ChatListScenario) {
        store.setChatListScenario(next)
    }

    fun currentChatListScenario(): ChatListScenario = store.currentChatListScenario()

    fun setContactListScenario(next: ContactListScenario) {
        store.setContactListScenario(next)
    }

    fun currentContactListScenario(): ContactListScenario = store.currentContactListScenario()

    fun restoreDefaultContactScenario() {
        store.restoreDefaultContactScenario()
    }

    fun restoreDefaultFixtures() {
        store.restoreDefaultFixtures()
    }

    fun setNextSendShouldFail(enabled: Boolean) {
        store.setNextSendShouldFail(enabled)
    }

    fun nextSendWillFail(): Boolean = store.nextSendWillFail()
}

private class InMemoryChatListRepository(
    private val store: InMemoryChatStore,
) : ChatListRepository {
    override fun loadChatList(query: ChatListQuery): ChatListLoadResult = store.loadChatList(query)

    override fun refreshChatList(query: ChatListQuery): ChatListLoadResult = store.refreshChatList(query)
}

private class InMemoryChatDetailRepository(
    private val store: InMemoryChatStore,
) : ChatDetailRepository {
    override fun loadChatDetail(chatId: String): ChatDetailLoadResult = store.loadChatDetail(chatId)

    override fun sendMessage(
        chatId: String,
        text: String,
    ): SendMessageResult = store.sendMessage(chatId, text)

    override fun retryMessage(
        chatId: String,
        messageId: String,
    ): RetryMessageResult = store.retryMessage(chatId, messageId)

    override fun loadAvailableMedia(): MediaPickerLoadResult = store.loadAvailableMedia()

    override fun sendMedia(
        chatId: String,
        mediaId: String,
    ): SendMediaResult = store.sendMedia(chatId, mediaId)
}

private class InMemoryContactsRepository(
    private val store: InMemoryChatStore,
) : ContactsRepository {
    override fun loadContacts(query: ContactsQuery): ContactsLoadResult = store.loadContacts(query)

    override fun openContactChat(contactId: String): OpenContactChatResult = store.openContactChat(contactId)
}

private class InMemorySearchRepository(
    private val store: InMemoryChatStore,
) : SearchRepository {
    override fun search(query: SearchQuery): SearchLoadResult = store.search(query)
}

private class InMemorySyncRepository(
    private val store: InMemoryChatStore,
) : SyncRepository {
    override fun restoreSnapshot(): SyncSnapshotRestoreResult = store.restoreSnapshot()

    override fun saveSnapshot(request: SyncSnapshotRequest): SyncSnapshotSaveResult = store.saveSnapshot(request)

    override fun clearSnapshot() {
        store.clearSnapshot()
    }
}

/**
 * Demo-only in-memory fixture store used by the current KMP shell.
 *
 * Single-thread contract:
 * - The current Android shell mutates this store only from the main thread.
 * - It is not safe for concurrent access from multiple threads without an explicit synchronization strategy.
 */
internal class InMemoryChatStore(
    private val snapshotStorage: SyncSnapshotStorage,
) {
    private val availableMedia = buildAvailableMedia()
    private val chats = buildDefaultChats().toMutableList()
    private val contacts = buildDefaultContacts().toMutableList()
    private var chatListScenario: ChatListScenario = ChatListScenario.DEFAULT
    private var contactListScenario: ContactListScenario = ContactListScenario.DEFAULT
    private var refreshCount = 0
    private var nextSendShouldFail = false
    private var messageCounter = DEFAULT_MESSAGE_COUNTER

    private val messagesByChat = buildDefaultMessages()
        .mapValues { (_, messages) -> messages.toMutableList() }
        .toMutableMap()

    fun loadChatList(query: ChatListQuery): ChatListLoadResult {
        return when (chatListScenario) {
            ChatListScenario.ERROR -> ChatListLoadResult.Failed("会话列表加载失败，请下拉重试。")
            ChatListScenario.EMPTY -> ChatListLoadResult.Empty
            ChatListScenario.DEFAULT -> resolveResult(
                chats = filterChats(keyword = query.keyword),
            )
        }
    }

    fun refreshChatList(query: ChatListQuery): ChatListLoadResult {
        return when (chatListScenario) {
            ChatListScenario.ERROR -> ChatListLoadResult.Failed("刷新失败，请稍后再试。")
            ChatListScenario.EMPTY -> ChatListLoadResult.Empty
            ChatListScenario.DEFAULT -> {
                refreshCount += 1
                updateChatSummary(
                    chatId = "chat-1",
                    lastMessagePreview = "Refresh #$refreshCount completed for the KMP chat list.",
                    lastMessageAtLabel = "刚刚",
                    unreadCount = chats.first { it.id == "chat-1" }.unreadCount + 1,
                )
                resolveResult(
                    chats = filterChats(keyword = query.keyword),
                )
            }
        }
    }

    fun loadChatDetail(chatId: String): ChatDetailLoadResult {
        return resolveThread(chatId)?.let { ChatDetailLoadResult.Success(it) }
            ?: ChatDetailLoadResult.Failed("未找到该会话。")
    }

    fun loadContacts(query: ContactsQuery): ContactsLoadResult {
        return when (contactListScenario) {
            ContactListScenario.ERROR -> ContactsLoadResult.Failed("联系人加载失败，请稍后重试。")
            ContactListScenario.EMPTY -> ContactsLoadResult.Empty
            ContactListScenario.DEFAULT -> {
                val results = filterContacts(query.keyword)
                if (results.isEmpty()) {
                    ContactsLoadResult.Empty
                } else {
                    ContactsLoadResult.Success(results)
                }
            }
        }
    }

    fun search(query: SearchQuery): SearchLoadResult {
        return when (chatListScenario) {
            ChatListScenario.ERROR -> SearchLoadResult.Failed("搜索暂不可用，请稍后重试。")
            ChatListScenario.EMPTY -> SearchLoadResult.Empty
            ChatListScenario.DEFAULT -> {
                val normalized = query.keyword.trim()
                if (normalized.isBlank()) {
                    SearchLoadResult.Empty
                } else {
                    val chatResults = filterChats(keyword = normalized)
                    val messageResults = chats.flatMap { chat ->
                        messagesByChat[chat.id]
                            .orEmpty()
                            .asReversed()
                            .filter { message ->
                                message.text.contains(normalized, ignoreCase = true)
                            }.map { message ->
                                MessageSearchHit(
                                    chat = chat,
                                    message = message,
                                )
                            }
                    }

                    if (chatResults.isEmpty() && messageResults.isEmpty()) {
                        SearchLoadResult.Empty
                    } else {
                        SearchLoadResult.Success(
                            chatResults = chatResults,
                            messageResults = messageResults,
                        )
                    }
                }
            }
        }
    }

    fun openContactChat(contactId: String): OpenContactChatResult {
        val contactIndex = contacts.indexOfFirst { it.id == contactId }
        if (contactIndex == -1) {
            return OpenContactChatResult.Failed("未找到目标联系人。")
        }

        val contact = contacts[contactIndex]
        val existingChatId = contact.linkedChatId
        if (existingChatId != null) {
            val chat = chats.firstOrNull { it.id == existingChatId }
                ?: return OpenContactChatResult.Failed("未找到联系人对应的会话。")
            return OpenContactChatResult.Success(
                chat = chat,
                isNewChat = false,
            )
        }

        val newChat = ChatSummary(
            id = "chat-contact-${contact.id}",
            title = contact.displayName,
            lastMessagePreview = "Hi, this chat started from Contacts.",
            unreadCount = 0,
            lastMessageAtLabel = "刚刚",
            avatarLabel = contact.avatarLabel,
            statusLabel = contact.statusLabel.ifBlank { "last seen recently" },
        )
        val initialMessage = Message(
            id = nextMessageId(),
            chatId = newChat.id,
            text = "Hi, this chat started from Contacts.",
            sentAtLabel = "刚刚",
            isOutgoing = false,
            deliveryState = DeliveryState.SENT,
        )

        chats.add(0, newChat)
        messagesByChat[newChat.id] = mutableListOf(initialMessage)
        contacts[contactIndex] = contact.copy(linkedChatId = newChat.id)

        return OpenContactChatResult.Success(
            chat = newChat,
            isNewChat = true,
        )
    }

    fun sendMessage(
        chatId: String,
        text: String,
    ): SendMessageResult {
        if (resolveThread(chatId) == null) {
            return SendMessageResult.Failed("未找到目标会话。")
        }

        val nextMessage = Message(
            id = nextMessageId(),
            chatId = chatId,
            text = text,
            sentAtLabel = "刚刚",
            isOutgoing = true,
            deliveryState = if (nextSendShouldFail) DeliveryState.FAILED else DeliveryState.SENT,
        )
        val bucket = messagesByChat.getOrPut(chatId) { mutableListOf() }
        bucket += nextMessage

        updateChatSummary(
            chatId = chatId,
            lastMessagePreview = previewFor(nextMessage),
            lastMessageAtLabel = "刚刚",
            unreadCount = 0,
        )
        moveChatToTop(chatId)

        val thread = resolveThread(chatId)
        nextSendShouldFail = false
        return if (nextMessage.deliveryState == DeliveryState.FAILED) {
            SendMessageResult.Failed(
                message = "发送失败，请点击重试。",
                thread = thread,
                failedMessage = nextMessage,
            )
        } else {
            SendMessageResult.Success(
                thread = requireNotNull(thread),
                sentMessage = nextMessage,
            )
        }
    }

    fun loadAvailableMedia(): MediaPickerLoadResult {
        return MediaPickerLoadResult.Success(availableMedia)
    }

    fun sendMedia(
        chatId: String,
        mediaId: String,
    ): SendMediaResult {
        if (resolveThread(chatId) == null) {
            return SendMediaResult.Failed("未找到目标会话。")
        }

        val attachment = availableMedia.firstOrNull { it.id == mediaId }
            ?: return SendMediaResult.Failed("未找到可发送的图片。")

        val nextMessage = Message(
            id = nextMessageId(),
            chatId = chatId,
            text = attachment.defaultCaption,
            sentAtLabel = "刚刚",
            isOutgoing = true,
            deliveryState = DeliveryState.SENT,
            mediaAttachment = attachment,
        )
        val bucket = messagesByChat.getOrPut(chatId) { mutableListOf() }
        bucket += nextMessage

        updateChatSummary(
            chatId = chatId,
            lastMessagePreview = previewFor(nextMessage),
            lastMessageAtLabel = "刚刚",
            unreadCount = 0,
        )
        moveChatToTop(chatId)

        return SendMediaResult.Success(
            thread = requireNotNull(resolveThread(chatId)),
            sentMessage = nextMessage,
        )
    }

    fun retryMessage(
        chatId: String,
        messageId: String,
    ): RetryMessageResult {
        val bucket = messagesByChat[chatId]
            ?: return RetryMessageResult.Failed("未找到目标会话。")
        val targetIndex = bucket.indexOfFirst { it.id == messageId }
        if (targetIndex == -1) {
            return RetryMessageResult.Failed(
                message = "未找到可重试消息。",
                thread = resolveThread(chatId),
            )
        }

        val targetMessage = bucket[targetIndex]
        if (targetMessage.deliveryState != DeliveryState.FAILED) {
            return RetryMessageResult.Failed(
                message = "当前消息不需要重试。",
                thread = resolveThread(chatId),
            )
        }

        val retriedMessage = targetMessage.copy(
            sentAtLabel = "刚刚",
            deliveryState = DeliveryState.SENT,
        )
        bucket[targetIndex] = retriedMessage

        updateChatSummary(
            chatId = chatId,
            lastMessagePreview = previewFor(retriedMessage),
            lastMessageAtLabel = "刚刚",
            unreadCount = 0,
        )
        moveChatToTop(chatId)

        return RetryMessageResult.Success(
            thread = requireNotNull(resolveThread(chatId)),
            retriedMessage = retriedMessage,
        )
    }

    fun restoreSnapshot(): SyncSnapshotRestoreResult {
        val snapshot = snapshotStorage.read() ?: return SyncSnapshotRestoreResult.NoSnapshot

        return runCatching {
            applySnapshot(snapshot)
            SyncSnapshotRestoreResult.Restored(snapshot)
        }.getOrElse {
            snapshotStorage.clear()
            restoreDefaultFixtures()
            SyncSnapshotRestoreResult.Failed("本地缓存不可用，已回退正常加载路径。")
        }
    }

    fun saveSnapshot(request: SyncSnapshotRequest): SyncSnapshotSaveResult {
        val normalizedChatId = request.selectedChatId?.trim()?.ifBlank { null }
        if (request.route == SyncSnapshotRoute.CHAT_DETAIL && normalizedChatId == null) {
            return SyncSnapshotSaveResult.Failed("未找到需要缓存的聊天详情。")
        }
        if (normalizedChatId != null && chats.none { it.id == normalizedChatId }) {
            return SyncSnapshotSaveResult.Failed("未找到需要缓存的目标会话。")
        }

        val snapshot = SyncSnapshot(
            route = request.route,
            searchKeyword = request.searchKeyword.trim(),
            selectedChatId = normalizedChatId,
            chats = chats.toList(),
            threads = chats.mapNotNull { chat -> resolveThread(chat.id) },
            contacts = contacts.map(ContactFixture::toSnapshot),
        )
        snapshotStorage.write(snapshot)
        return SyncSnapshotSaveResult.Success(snapshot)
    }

    fun clearSnapshot() {
        snapshotStorage.clear()
    }

    fun setChatListScenario(next: ChatListScenario) {
        chatListScenario = next
    }

    fun currentChatListScenario(): ChatListScenario = chatListScenario

    fun setContactListScenario(next: ContactListScenario) {
        contactListScenario = next
    }

    fun currentContactListScenario(): ContactListScenario = contactListScenario

    fun restoreDefaultContactScenario() {
        contactListScenario = ContactListScenario.DEFAULT
    }

    fun restoreDefaultFixtures() {
        chats.clear()
        chats += buildDefaultChats()
        contacts.clear()
        contacts += buildDefaultContacts()
        messagesByChat.clear()
        messagesByChat.putAll(
            buildDefaultMessages()
                .mapValues { (_, messages) -> messages.toMutableList() },
        )
        chatListScenario = ChatListScenario.DEFAULT
        contactListScenario = ContactListScenario.DEFAULT
        refreshCount = 0
        nextSendShouldFail = false
        messageCounter = DEFAULT_MESSAGE_COUNTER
    }

    fun setNextSendShouldFail(enabled: Boolean) {
        nextSendShouldFail = enabled
    }

    fun nextSendWillFail(): Boolean = nextSendShouldFail

    private fun applySnapshot(snapshot: SyncSnapshot) {
        if (snapshot.route == SyncSnapshotRoute.CHAT_DETAIL && snapshot.selectedChatId == null) {
            error("detail snapshot missing selectedChatId")
        }

        chats.clear()
        chats += snapshot.chats

        contacts.clear()
        contacts += restoreContacts(snapshot)

        messagesByChat.clear()
        snapshot.threads.forEach { thread ->
            messagesByChat[thread.chat.id] = thread.messages.toMutableList()
        }
        chats.forEach { chat ->
            if (!messagesByChat.containsKey(chat.id)) {
                messagesByChat[chat.id] = mutableListOf()
            }
        }
        if (snapshot.selectedChatId != null && chats.none { it.id == snapshot.selectedChatId }) {
            error("snapshot selected chat not found")
        }

        chatListScenario = ChatListScenario.DEFAULT
        contactListScenario = ContactListScenario.DEFAULT
        refreshCount = 0
        nextSendShouldFail = false
        messageCounter = snapshot.threads
            .flatMap { it.messages }
            .mapNotNull { message ->
                message.id.removePrefix("message-").toIntOrNull()
            }
            .maxOrNull()
            ?: DEFAULT_MESSAGE_COUNTER
    }

    private fun filterChats(keyword: String): List<ChatSummary> {
        if (keyword.isBlank()) {
            return chats.toList()
        }

        val normalized = keyword.trim()
        return chats.filter { chat ->
            chat.title.contains(normalized, ignoreCase = true) ||
                chat.lastMessagePreview.contains(normalized, ignoreCase = true)
        }
    }

    private fun filterContacts(keyword: String): List<ContactSummary> {
        if (keyword.isBlank()) {
            return contacts.map(ContactFixture::toSummary)
        }

        val normalized = keyword.trim()
        return contacts
            .filter { contact ->
                contact.displayName.contains(normalized, ignoreCase = true) ||
                    contact.phoneNumber.contains(normalized, ignoreCase = true)
            }.map(ContactFixture::toSummary)
    }

    private fun restoreContacts(snapshot: SyncSnapshot): List<ContactFixture> {
        if (snapshot.contacts.isNotEmpty()) {
            return snapshot.contacts.map(ContactSnapshot::toFixture)
        }

        val availableChatIds = snapshot.chats.map(ChatSummary::id).toSet()
        return buildDefaultContacts().map { contact ->
            val restoredChatId = when {
                contact.linkedChatId != null && availableChatIds.contains(contact.linkedChatId) -> contact.linkedChatId
                availableChatIds.contains(contact.generatedChatId()) -> contact.generatedChatId()
                else -> null
            }
            contact.copy(linkedChatId = restoredChatId)
        }
    }

    private fun resolveResult(chats: List<ChatSummary>): ChatListLoadResult {
        return if (chats.isEmpty()) {
            ChatListLoadResult.Empty
        } else {
            ChatListLoadResult.Success(chats)
        }
    }

    private fun resolveThread(chatId: String): ChatThread? {
        val chat = chats.find { it.id == chatId } ?: return null
        return ChatThread(
            chat = chat,
            messages = messagesByChat[chatId].orEmpty().toList(),
        )
    }

    private fun updateChatSummary(
        chatId: String,
        lastMessagePreview: String,
        lastMessageAtLabel: String,
        unreadCount: Int,
    ) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex == -1) {
            return
        }

        chats[chatIndex] = chats[chatIndex].copy(
            lastMessagePreview = lastMessagePreview,
            lastMessageAtLabel = lastMessageAtLabel,
            unreadCount = unreadCount,
        )
    }

    private fun moveChatToTop(chatId: String) {
        val chatIndex = chats.indexOfFirst { it.id == chatId }
        if (chatIndex <= 0) {
            return
        }

        val chat = chats.removeAt(chatIndex)
        chats.add(0, chat)
    }

    private fun nextMessageId(): String {
        messageCounter += 1
        return "message-$messageCounter"
    }

    private fun previewFor(message: Message): String {
        val attachment = message.mediaAttachment
        return if (attachment == null) {
            message.text
        } else {
            val caption = message.text.ifBlank { attachment.title }
            "Photo · $caption"
        }
    }

    private companion object {
        const val DEFAULT_MESSAGE_COUNTER = 24
    }
}

private data class ContactFixture(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    val avatarLabel: String,
    val statusLabel: String,
    val linkedChatId: String?,
) {
    fun toSummary(): ContactSummary {
        return ContactSummary(
            id = id,
            displayName = displayName,
            phoneNumber = phoneNumber,
            avatarLabel = avatarLabel,
            statusLabel = statusLabel,
            hasExistingChat = linkedChatId != null,
        )
    }

    fun toSnapshot(): ContactSnapshot {
        return ContactSnapshot(
            id = id,
            displayName = displayName,
            phoneNumber = phoneNumber,
            avatarLabel = avatarLabel,
            statusLabel = statusLabel,
            linkedChatId = linkedChatId,
        )
    }

    fun generatedChatId(): String = "chat-contact-$id"
}

private fun ContactSnapshot.toFixture(): ContactFixture {
    return ContactFixture(
        id = id,
        displayName = displayName,
        phoneNumber = phoneNumber,
        avatarLabel = avatarLabel,
        statusLabel = statusLabel,
        linkedChatId = linkedChatId,
    )
}

private fun buildDefaultChats(): List<ChatSummary> {
    return listOf(
        ChatSummary(
            id = "chat-1",
            title = "Telegram Compare",
            lastMessagePreview = "Photo · Telegram-style settings reference",
            unreadCount = 3,
            lastMessageAtLabel = "09:24",
            avatarLabel = "TC",
            statusLabel = "last seen recently",
        ),
        ChatSummary(
            id = "chat-2",
            title = "AI Infra",
            lastMessagePreview = "Figma MCP is ready, waiting for editable frames.",
            unreadCount = 1,
            lastMessageAtLabel = "08:11",
            avatarLabel = "AI",
            isMuted = true,
            statusLabel = "syncing logs",
        ),
        ChatSummary(
            id = "chat-3",
            title = "Product Design",
            lastMessagePreview = "S2 still needs a Telegram-style search field.",
            unreadCount = 0,
            lastMessageAtLabel = "昨天",
            avatarLabel = "PD",
            statusLabel = "reviewing handoff",
        ),
        ChatSummary(
            id = "chat-4",
            title = "KMP Delivery",
            lastMessagePreview = "Wire the shared-domain use cases before polishing UI.",
            unreadCount = 6,
            lastMessageAtLabel = "周四",
            avatarLabel = "KD",
            statusLabel = "typing...",
        ),
        ChatSummary(
            id = "chat-5",
            title = "QA Evidence",
            lastMessagePreview = "Photo · Fresh emulator screenshot for the search flow.",
            unreadCount = 2,
            lastMessageAtLabel = "周三",
            avatarLabel = "QA",
            statusLabel = "online",
        ),
        ChatSummary(
            id = "chat-6",
            title = "Growth Ops",
            lastMessagePreview = "Longer preview copy helps validate truncation in the row layout.",
            unreadCount = 0,
            lastMessageAtLabel = "周二",
            avatarLabel = "GO",
            statusLabel = "last seen 2h ago",
        ),
        ChatSummary(
            id = "chat-7",
            title = "Design Systems",
            lastMessagePreview = "Avatar colors and muted metadata should feel less generic.",
            unreadCount = 12,
            lastMessageAtLabel = "周一",
            avatarLabel = "DS",
            statusLabel = "reviewing",
        ),
        ChatSummary(
            id = "chat-8",
            title = "Release Notes",
            lastMessagePreview = "S4 planning starts after the S1-S3 UI stabilizes.",
            unreadCount = 0,
            lastMessageAtLabel = "3月12日",
            avatarLabel = "RN",
            statusLabel = "drafting summary",
        ),
        ChatSummary(
            id = "chat-9",
            title = "Bot Sandbox",
            lastMessagePreview = "Search should still find keywords inside preview text.",
            unreadCount = 4,
            lastMessageAtLabel = "3月10日",
            avatarLabel = "BS",
            statusLabel = "awaiting input",
        ),
        ChatSummary(
            id = "chat-10",
            title = "Mock Data Lab",
            lastMessagePreview = "Need enough rows to force a real scroll region on small screens.",
            unreadCount = 0,
            lastMessageAtLabel = "3月08日",
            avatarLabel = "MD",
            statusLabel = "fixture owner",
        ),
    )
}

private fun buildAvailableMedia(): List<MediaAttachment> {
    return listOf(
        MediaAttachment(
            id = "media-1",
            title = "Settings reference",
            defaultCaption = "Telegram-style settings reference",
        ),
        MediaAttachment(
            id = "media-2",
            title = "Search evidence",
            defaultCaption = "Fresh emulator screenshot for the search flow.",
        ),
        MediaAttachment(
            id = "media-3",
            title = "Delivery board",
            defaultCaption = "Media picker board for the S7 acceptance path.",
        ),
    )
}

private fun buildDefaultContacts(): List<ContactFixture> {
    return listOf(
        ContactFixture(
            id = "contact-1",
            displayName = "Telegram Compare",
            phoneNumber = "+86 138 0000 0101",
            avatarLabel = "TC",
            statusLabel = "last seen recently",
            linkedChatId = "chat-1",
        ),
        ContactFixture(
            id = "contact-2",
            displayName = "AI Infra",
            phoneNumber = "+86 138 0000 0102",
            avatarLabel = "AI",
            statusLabel = "syncing logs",
            linkedChatId = "chat-2",
        ),
        ContactFixture(
            id = "contact-3",
            displayName = "Product Design",
            phoneNumber = "+86 138 0000 0103",
            avatarLabel = "PD",
            statusLabel = "reviewing handoff",
            linkedChatId = "chat-3",
        ),
        ContactFixture(
            id = "contact-4",
            displayName = "QA Evidence",
            phoneNumber = "+86 138 0000 0104",
            avatarLabel = "QA",
            statusLabel = "online",
            linkedChatId = "chat-5",
        ),
        ContactFixture(
            id = "contact-5",
            displayName = "Nora Chen",
            phoneNumber = "+86 138 0000 0105",
            avatarLabel = "NC",
            statusLabel = "online",
            linkedChatId = null,
        ),
        ContactFixture(
            id = "contact-6",
            displayName = "Sam Rivera",
            phoneNumber = "+86 138 0000 0106",
            avatarLabel = "SR",
            statusLabel = "last seen 10m ago",
            linkedChatId = null,
        ),
        ContactFixture(
            id = "contact-7",
            displayName = "Mia Zhou",
            phoneNumber = "+86 138 0000 0107",
            avatarLabel = "MZ",
            statusLabel = "last seen 1h ago",
            linkedChatId = null,
        ),
        ContactFixture(
            id = "contact-8",
            displayName = "Mock Data Lab",
            phoneNumber = "+86 138 0000 0108",
            avatarLabel = "MD",
            statusLabel = "fixture owner",
            linkedChatId = "chat-10",
        ),
    )
}

private fun buildDefaultMessages(): Map<String, List<Message>> {
    val media = buildAvailableMedia()
    return mapOf(
        "chat-1" to listOf(
            Message("message-1", "chat-1", "Spec is ready. Build the KMP shell next.", "09:02", false, DeliveryState.SENT),
            Message("message-2", "chat-1", "Understood. I will keep the acceptance evidence synced.", "09:05", true, DeliveryState.SENT),
            Message("message-3", "chat-1", "The whole page still scrolls. Fix the viewport boundaries first.", "09:09", false, DeliveryState.SENT),
            Message("message-4", "chat-1", "I am moving top chrome and composer out of the scroll container.", "09:11", true, DeliveryState.SENT),
            Message("message-5", "chat-1", "Good. The list should feel denser and closer to Telegram.", "09:13", false, DeliveryState.SENT),
            Message("message-6", "chat-1", "I will tighten row spacing and flatten the visual treatment.", "09:15", true, DeliveryState.SENT),
            Message("message-7", "chat-1", "Do not forget richer fixture data for long titles and previews.", "09:17", false, DeliveryState.SENT),
            Message("message-8", "chat-1", "Adding more chats and longer copy now.", "09:18", true, DeliveryState.SENT),
            Message("message-9", "chat-1", "Composer must stay pinned while the thread keeps scrolling.", "09:20", false, DeliveryState.SENT),
            Message("message-10", "chat-1", "That will also make send and retry states much easier to read.", "09:22", true, DeliveryState.SENT),
            Message("message-11", "chat-1", "Remember to log AI friction and blockers.", "09:23", false, DeliveryState.SENT),
            Message(
                id = "message-12",
                chatId = "chat-1",
                text = media[0].defaultCaption,
                sentAtLabel = "09:24",
                isOutgoing = true,
                deliveryState = DeliveryState.SENT,
                mediaAttachment = media[0],
            ),
        ),
        "chat-2" to listOf(
            Message("message-13", "chat-2", "Issue labels should distinguish common and KMP-specific friction.", "08:01", false, DeliveryState.SENT),
            Message("message-14", "chat-2", "Agreed. The design blocker is no longer MCP access, it is the missing editable file.", "08:06", true, DeliveryState.SENT),
            Message("message-15", "chat-2", "Keep the infra summary concise in each acceptance note.", "08:11", false, DeliveryState.SENT),
        ),
        "chat-3" to listOf(
            Message("message-16", "chat-3", "Default state and failure state should both be ready-for-dev.", "昨天", false, DeliveryState.SENT),
            Message("message-17", "chat-3", "I will keep the visual density close to Telegram.", "昨天", true, DeliveryState.SENT),
            Message("message-18", "chat-3", "The search field still needs better native spacing.", "昨天", false, DeliveryState.SENT),
        ),
        "chat-4" to listOf(
            Message("message-19", "chat-4", "Refresh needs a visible indicator and updated fixture data.", "周四", false, DeliveryState.SENT),
            Message("message-20", "chat-4", "Agreed. I will wire the Android shell after the shared layer lands.", "周四", true, DeliveryState.SENT),
            Message("message-21", "chat-4", "Do not let debug controls dominate the viewport.", "周四", false, DeliveryState.SENT),
        ),
        "chat-5" to listOf(
            Message(
                id = "message-22",
                chatId = "chat-5",
                text = media[1].defaultCaption,
                sentAtLabel = "周三",
                isOutgoing = false,
                deliveryState = DeliveryState.SENT,
                mediaAttachment = media[1],
            ),
            Message("message-23", "chat-5", "Will do after the layout rebuild passes assembleDebug.", "周三", true, DeliveryState.SENT),
        ),
        "chat-10" to listOf(
            Message("message-24", "chat-10", "Need enough rows to force a real scroll region.", "3月08日", false, DeliveryState.SENT),
        ),
    )
}
