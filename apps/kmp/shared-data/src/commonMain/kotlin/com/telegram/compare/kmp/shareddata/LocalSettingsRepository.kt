package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.PreferenceKey
import com.telegram.compare.kmp.shareddomain.SettingsLoadResult
import com.telegram.compare.kmp.shareddomain.SettingsRepository
import com.telegram.compare.kmp.shareddomain.SettingsSnapshot
import com.telegram.compare.kmp.shareddomain.UpdatePreferenceResult
import com.telegram.compare.kmp.shareddomain.UserPreference
import com.telegram.compare.kmp.shareddomain.UserProfileSummary
import com.telegram.compare.kmp.shareddomain.UserSession

class LocalSettingsRepository(
    private val storage: UserSettingsStorage = InMemoryUserSettingsStorage(),
) : SettingsRepository {
    override fun loadSettings(session: UserSession): SettingsLoadResult {
        return SettingsLoadResult.Success(snapshotFor(session))
    }

    override fun updatePreference(
        session: UserSession,
        key: PreferenceKey,
        enabled: Boolean,
    ): UpdatePreferenceResult {
        val current = storage.read() ?: defaultSettingsFor(session)
        val updated = when (key) {
            PreferenceKey.NOTIFICATIONS -> current.copy(notificationsEnabled = enabled)
            PreferenceKey.AUTO_DOWNLOAD_MEDIA_ON_WIFI -> current.copy(autoDownloadMediaOnWifi = enabled)
            PreferenceKey.REDUCED_MOTION -> current.copy(reducedMotion = enabled)
        }
        storage.write(updated)

        val snapshot = snapshotFor(session)
        val updatedPreference = snapshot.preferences.firstOrNull { it.key == key }
            ?: return UpdatePreferenceResult.Failed(
                message = "未找到需要更新的偏好项。",
                snapshot = snapshot,
            )

        return UpdatePreferenceResult.Success(
            snapshot = snapshot,
            updatedPreference = updatedPreference,
        )
    }

    private fun snapshotFor(session: UserSession): SettingsSnapshot {
        val persisted = storage.read() ?: defaultSettingsFor(session).also(storage::write)
        return SettingsSnapshot(
            profile = UserProfileSummary(
                displayName = session.displayName,
                phoneNumber = session.phoneNumber,
                username = persisted.username,
                about = persisted.about,
            ),
            preferences = listOf(
                UserPreference(
                    key = PreferenceKey.NOTIFICATIONS,
                    title = "Notifications",
                    description = "保留横幅与未读提醒，模拟 Telegram 默认通知体验。",
                    isEnabled = persisted.notificationsEnabled,
                ),
                UserPreference(
                    key = PreferenceKey.AUTO_DOWNLOAD_MEDIA_ON_WIFI,
                    title = "Auto-download media on Wi-Fi",
                    description = "在 Wi-Fi 环境下自动加载图片消息，降低浏览阻力。",
                    isEnabled = persisted.autoDownloadMediaOnWifi,
                ),
                UserPreference(
                    key = PreferenceKey.REDUCED_MOTION,
                    title = "Reduced motion",
                    description = "减少过渡与面板动效，为后续切片提供统一动效开关。",
                    isEnabled = persisted.reducedMotion,
                ),
            ),
        )
    }

    private fun defaultSettingsFor(session: UserSession): PersistedUserSettings {
        val suffix = session.userId.takeLast(4)
        return PersistedUserSettings(
            username = "compare_$suffix",
            about = "KMP demo account for Telegram Compare.",
            notificationsEnabled = true,
            autoDownloadMediaOnWifi = true,
            reducedMotion = false,
        )
    }
}
