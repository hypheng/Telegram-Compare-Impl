package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsUseCasesTest {
    @Test
    fun togglesPreferenceToOppositeValue() {
        val repository = FakeSettingsRepository()
        val session = UserSession(
            sessionId = "session-1",
            userId = "user-0001",
            displayName = "Compare 0001",
            phoneNumber = "+86 138 0000 0001",
        )

        TogglePreferenceUseCase(repository).execute(
            session = session,
            key = PreferenceKey.REDUCED_MOTION,
            currentValue = false,
        )

        assertEquals(session, repository.lastSession)
        assertEquals(PreferenceKey.REDUCED_MOTION, repository.lastKey)
        assertEquals(true, repository.lastEnabled)
    }
}

private class FakeSettingsRepository : SettingsRepository {
    var lastSession: UserSession? = null
    var lastKey: PreferenceKey? = null
    var lastEnabled: Boolean? = null

    override fun loadSettings(session: UserSession): SettingsLoadResult {
        error("unused in test")
    }

    override fun updatePreference(
        session: UserSession,
        key: PreferenceKey,
        enabled: Boolean,
    ): UpdatePreferenceResult {
        lastSession = session
        lastKey = key
        lastEnabled = enabled
        return UpdatePreferenceResult.Failed("unused")
    }
}
