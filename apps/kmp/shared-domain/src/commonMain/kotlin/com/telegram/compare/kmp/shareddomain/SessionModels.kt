package com.telegram.compare.kmp.shareddomain

data class UserSession(
    val sessionId: String,
    val userId: String,
    val displayName: String,
    val phoneNumber: String,
)

sealed interface SessionRestoreResult {
    data class Restored(val session: UserSession) : SessionRestoreResult

    object NoSession : SessionRestoreResult

    data class Failed(val message: String) : SessionRestoreResult
}

sealed interface LoginResult {
    data class Success(val session: UserSession) : LoginResult

    data class InvalidInput(val message: String) : LoginResult

    data class Failed(val message: String) : LoginResult
}

interface SessionRepository {
    fun restoreSession(): SessionRestoreResult

    fun login(phoneNumber: String, verificationCode: String): LoginResult

    fun clearSession()
}
