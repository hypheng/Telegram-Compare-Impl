package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.LoginResult
import com.telegram.compare.kmp.shareddomain.SessionRepository
import com.telegram.compare.kmp.shareddomain.SessionRestoreResult
import com.telegram.compare.kmp.shareddomain.UserSession

class DemoSessionRepository(
    private val storage: SessionStorage,
) : SessionRepository {
    /*
     * Demo-only auth repository for the current comparison shell.
     * Follow-up: wire a release-safe session/auth implementation before any non-demo distribution path.
     */
    override fun restoreSession(): SessionRestoreResult {
        val persistedSession = storage.read() ?: return SessionRestoreResult.NoSession

        return when (persistedSession.status) {
            PersistedSessionStatus.ACTIVE -> SessionRestoreResult.Restored(persistedSession.toDomain())
            PersistedSessionStatus.EXPIRED -> {
                storage.clear()
                SessionRestoreResult.Failed("已保存的会话已失效，请重新登录。")
            }
        }
    }

    override fun login(
        phoneNumber: String,
        verificationCode: String,
    ): LoginResult {
        if (verificationCode != DEMO_VERIFICATION_CODE) {
            return LoginResult.Failed("验证码不正确，请输入 demo 验证码 2046。")
        }

        val digits = phoneNumber.filter(Char::isDigit)
        val session = PersistedSession(
            sessionId = "session-$digits",
            userId = "user-${digits.takeLast(4)}",
            displayName = "Compare ${digits.takeLast(4)}",
            phoneNumber = phoneNumber,
            status = PersistedSessionStatus.ACTIVE,
        )

        storage.write(session)
        return LoginResult.Success(session.toDomain())
    }

    override fun clearSession() {
        storage.clear()
    }

    fun seedExpiredSession() {
        val current = storage.read()
        val expiredSession = current?.copy(status = PersistedSessionStatus.EXPIRED)
            ?: PersistedSession(
                sessionId = "expired-session",
                userId = "expired-user",
                displayName = "Expired Demo",
                phoneNumber = "+86 138 0000 0000",
                status = PersistedSessionStatus.EXPIRED,
            )

        storage.write(expiredSession)
    }

    companion object {
        const val DEMO_VERIFICATION_CODE = "2046"
    }
}

private fun PersistedSession.toDomain(): UserSession {
    return UserSession(
        sessionId = sessionId,
        userId = userId,
        displayName = displayName,
        phoneNumber = phoneNumber,
    )
}
