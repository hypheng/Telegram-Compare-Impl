package com.telegram.compare.kmp.shareddata

import android.content.SharedPreferences

class PreferencesSessionStorage(
    private val sharedPreferences: SharedPreferences,
) : SessionStorage {
    override fun read(): PersistedSession? {
        val sessionId = sharedPreferences.getString(KEY_SESSION_ID, null) ?: return null
        val userId = sharedPreferences.getString(KEY_USER_ID, null) ?: return null
        val displayName = sharedPreferences.getString(KEY_DISPLAY_NAME, null) ?: return null
        val phoneNumber = sharedPreferences.getString(KEY_PHONE_NUMBER, null) ?: return null
        val statusName = sharedPreferences.getString(KEY_STATUS, PersistedSessionStatus.ACTIVE.name)
            ?: PersistedSessionStatus.ACTIVE.name

        val status = PersistedSessionStatus.entries.firstOrNull { it.name == statusName }
            ?: PersistedSessionStatus.ACTIVE

        return PersistedSession(
            sessionId = sessionId,
            userId = userId,
            displayName = displayName,
            phoneNumber = phoneNumber,
            status = status,
        )
    }

    override fun write(session: PersistedSession) {
        sharedPreferences.edit()
            .putString(KEY_SESSION_ID, session.sessionId)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_DISPLAY_NAME, session.displayName)
            .putString(KEY_PHONE_NUMBER, session.phoneNumber)
            .putString(KEY_STATUS, session.status.name)
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove(KEY_SESSION_ID)
            .remove(KEY_USER_ID)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_PHONE_NUMBER)
            .remove(KEY_STATUS)
            .apply()
    }

    private companion object {
        const val KEY_SESSION_ID = "session_id"
        const val KEY_USER_ID = "user_id"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_PHONE_NUMBER = "phone_number"
        const val KEY_STATUS = "status"
    }
}
