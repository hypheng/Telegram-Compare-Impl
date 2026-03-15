package com.telegram.compare.kmp.shareddomain

data class UserProfileSummary(
    val displayName: String,
    val phoneNumber: String,
    val username: String,
    val about: String,
)

enum class PreferenceKey {
    NOTIFICATIONS,
    AUTO_DOWNLOAD_MEDIA_ON_WIFI,
    REDUCED_MOTION,
}

data class UserPreference(
    val key: PreferenceKey,
    val title: String,
    val description: String,
    val isEnabled: Boolean,
)

data class SettingsSnapshot(
    val profile: UserProfileSummary,
    val preferences: List<UserPreference>,
)

sealed interface SettingsLoadResult {
    data class Success(val snapshot: SettingsSnapshot) : SettingsLoadResult

    data class Failed(val message: String) : SettingsLoadResult
}

sealed interface UpdatePreferenceResult {
    data class Success(
        val snapshot: SettingsSnapshot,
        val updatedPreference: UserPreference,
    ) : UpdatePreferenceResult

    data class Failed(
        val message: String,
        val snapshot: SettingsSnapshot? = null,
    ) : UpdatePreferenceResult
}

interface SettingsRepository {
    fun loadSettings(session: UserSession): SettingsLoadResult

    fun updatePreference(
        session: UserSession,
        key: PreferenceKey,
        enabled: Boolean,
    ): UpdatePreferenceResult
}
