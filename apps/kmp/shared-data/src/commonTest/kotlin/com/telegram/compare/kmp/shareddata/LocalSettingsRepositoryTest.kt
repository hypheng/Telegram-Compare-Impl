package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.PreferenceKey
import com.telegram.compare.kmp.shareddomain.SettingsLoadResult
import com.telegram.compare.kmp.shareddomain.UpdatePreferenceResult
import com.telegram.compare.kmp.shareddomain.UserSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LocalSettingsRepositoryTest {
    private val session = UserSession(
        sessionId = "session-13800000000",
        userId = "user-0000",
        displayName = "Compare 0000",
        phoneNumber = "+86 138 0000 0000",
    )

    @Test
    fun loadsDefaultProfileAndPreferences() {
        val repository = LocalSettingsRepository()

        val result = repository.loadSettings(session)

        assertIs<SettingsLoadResult.Success>(result)
        assertEquals("Compare 0000", result.snapshot.profile.displayName)
        assertEquals("compare_0000", result.snapshot.profile.username)
        assertEquals(
            listOf(
                PreferenceKey.NOTIFICATIONS,
                PreferenceKey.AUTO_DOWNLOAD_MEDIA_ON_WIFI,
                PreferenceKey.REDUCED_MOTION,
            ),
            result.snapshot.preferences.map { it.key },
        )
    }

    @Test
    fun persistsPreferenceUpdatesAcrossRepositoryInstances() {
        val storage = InMemoryUserSettingsStorage()
        val firstRepository = LocalSettingsRepository(storage = storage)
        val secondRepository = LocalSettingsRepository(storage = storage)

        val update = firstRepository.updatePreference(
            session = session,
            key = PreferenceKey.REDUCED_MOTION,
            enabled = true,
        )
        val reload = secondRepository.loadSettings(session)

        assertIs<UpdatePreferenceResult.Success>(update)
        assertEquals(PreferenceKey.REDUCED_MOTION, update.updatedPreference.key)
        assertEquals(true, update.updatedPreference.isEnabled)
        assertIs<SettingsLoadResult.Success>(reload)
        assertEquals(
            true,
            reload.snapshot.preferences.first { it.key == PreferenceKey.REDUCED_MOTION }.isEnabled,
        )
    }
}
