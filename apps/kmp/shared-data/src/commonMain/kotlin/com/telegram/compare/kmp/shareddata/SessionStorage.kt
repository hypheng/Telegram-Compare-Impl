package com.telegram.compare.kmp.shareddata

data class PersistedSession(
    val sessionId: String,
    val userId: String,
    val displayName: String,
    val phoneNumber: String,
    val status: PersistedSessionStatus,
)

enum class PersistedSessionStatus {
    ACTIVE,
    EXPIRED,
}

interface SessionStorage {
    fun read(): PersistedSession?

    fun write(session: PersistedSession)

    fun clear()
}

class InMemorySessionStorage(
    private var persistedSession: PersistedSession? = null,
) : SessionStorage {
    override fun read(): PersistedSession? = persistedSession

    override fun write(session: PersistedSession) {
        persistedSession = session
    }

    override fun clear() {
        persistedSession = null
    }
}
