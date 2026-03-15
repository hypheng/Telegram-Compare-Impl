package com.telegram.compare.kmp.android

import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.MediaAttachment
import com.telegram.compare.kmp.shareddomain.MessageSearchHit
import com.telegram.compare.kmp.shareddomain.SettingsSnapshot
import com.telegram.compare.kmp.shareddomain.UserSession

sealed interface MainScreenState {
    object Restoring : MainScreenState

    data class Login(
        val restoreMessage: String? = null,
        val formMessage: String? = null,
        val isSubmitting: Boolean = false,
    ) : MainScreenState

    data class ChatList(
        val session: UserSession,
        val statusMessage: String? = null,
        val contentState: ChatListContentState = ChatListContentState.Loading,
        val searchDraft: String = "",
        val isRefreshing: Boolean = false,
    ) : MainScreenState

    data class Search(
        val session: UserSession,
        val queryDraft: String = "",
        val statusMessage: String? = null,
        val contentState: SearchContentState = SearchContentState.Idle,
    ) : MainScreenState

    data class Settings(
        val session: UserSession,
        val statusMessage: String? = null,
        val contentState: SettingsContentState = SettingsContentState.Loading,
    ) : MainScreenState

    data class ChatDetail(
        val session: UserSession,
        val chatId: String,
        val chatTitle: String,
        val chatSubtitle: String,
        val statusMessage: String? = null,
        val contentState: ChatDetailContentState = ChatDetailContentState.Loading,
        val composerDraft: String = "",
        val pendingOutgoingText: String? = null,
        val retryingMessageId: String? = null,
        val nextSendWillFail: Boolean = false,
        val highlightedMessageId: String? = null,
        val returnToSearch: SearchReturnState? = null,
        val mediaPickerState: MediaPickerState = MediaPickerState.Closed,
    ) : MainScreenState
}

data class SearchReturnState(
    val queryDraft: String,
    val statusMessage: String? = null,
    val contentState: SearchContentState = SearchContentState.Idle,
)

sealed interface ChatListContentState {
    object Loading : ChatListContentState

    data class Ready(val chats: List<ChatSummary>) : ChatListContentState

    data class Empty(
        val title: String,
        val body: String,
    ) : ChatListContentState

    data class Error(val message: String) : ChatListContentState
}

sealed interface ChatDetailContentState {
    object Loading : ChatDetailContentState

    data class Ready(val thread: ChatThread) : ChatDetailContentState

    data class Error(val message: String) : ChatDetailContentState
}

sealed interface SearchContentState {
    object Idle : SearchContentState

    object Loading : SearchContentState

    data class Ready(
        val chatResults: List<ChatSummary>,
        val messageResults: List<MessageSearchHit>,
    ) : SearchContentState

    data class Empty(
        val title: String,
        val body: String,
    ) : SearchContentState

    data class Error(val message: String) : SearchContentState
}

sealed interface SettingsContentState {
    object Loading : SettingsContentState

    data class Ready(val snapshot: SettingsSnapshot) : SettingsContentState

    data class Error(val message: String) : SettingsContentState
}

sealed interface MediaPickerState {
    object Closed : MediaPickerState

    object Loading : MediaPickerState

    data class Ready(val attachments: List<MediaAttachment>) : MediaPickerState

    data class Error(val message: String) : MediaPickerState
}
