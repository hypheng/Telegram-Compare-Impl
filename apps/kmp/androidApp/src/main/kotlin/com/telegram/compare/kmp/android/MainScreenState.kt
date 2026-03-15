package com.telegram.compare.kmp.android

import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.UserSession

sealed interface MainScreenState {
    object Restoring : MainScreenState

    data class Login(
        val restoreMessage: String? = null,
        val formMessage: String? = null,
        val isSubmitting: Boolean = false,
    ) : MainScreenState

    data class Home(
        val session: UserSession,
        val chats: List<ChatSummary>,
        val statusMessage: String,
    ) : MainScreenState
}
