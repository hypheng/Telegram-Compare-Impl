package com.telegram.compare.kmp.shareddata

data class PersistedUserSettings(
    val username: String,
    val about: String,
    val notificationsEnabled: Boolean,
    val autoDownloadMediaOnWifi: Boolean,
    val reducedMotion: Boolean,
)

interface UserSettingsStorage {
    fun read(): PersistedUserSettings?

    fun write(settings: PersistedUserSettings)
}

class InMemoryUserSettingsStorage : UserSettingsStorage {
    private var current: PersistedUserSettings? = null

    override fun read(): PersistedUserSettings? = current

    override fun write(settings: PersistedUserSettings) {
        current = settings
    }
}
