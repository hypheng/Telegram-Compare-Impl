package com.telegram.compare.kmp.shareddata

import android.content.SharedPreferences

class PreferencesUserSettingsStorage(
    private val sharedPreferences: SharedPreferences,
) : UserSettingsStorage {
    override fun read(): PersistedUserSettings? {
        val username = sharedPreferences.getString(KEY_USERNAME, null) ?: return null
        val about = sharedPreferences.getString(KEY_ABOUT, null) ?: return null
        return PersistedUserSettings(
            username = username,
            about = about,
            notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true),
            autoDownloadMediaOnWifi = sharedPreferences.getBoolean(KEY_AUTO_DOWNLOAD_MEDIA_ON_WIFI, true),
            reducedMotion = sharedPreferences.getBoolean(KEY_REDUCED_MOTION, false),
        )
    }

    override fun write(settings: PersistedUserSettings) {
        sharedPreferences.edit()
            .putString(KEY_USERNAME, settings.username)
            .putString(KEY_ABOUT, settings.about)
            .putBoolean(KEY_NOTIFICATIONS, settings.notificationsEnabled)
            .putBoolean(KEY_AUTO_DOWNLOAD_MEDIA_ON_WIFI, settings.autoDownloadMediaOnWifi)
            .putBoolean(KEY_REDUCED_MOTION, settings.reducedMotion)
            .apply()
    }

    private companion object {
        const val KEY_USERNAME = "settings_username"
        const val KEY_ABOUT = "settings_about"
        const val KEY_NOTIFICATIONS = "settings_notifications"
        const val KEY_AUTO_DOWNLOAD_MEDIA_ON_WIFI = "settings_auto_download_media_on_wifi"
        const val KEY_REDUCED_MOTION = "settings_reduced_motion"
    }
}
