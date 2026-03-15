package com.telegram.compare.kmp.android

import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.ChatSummary
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
    ) : MainScreenState
}

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
