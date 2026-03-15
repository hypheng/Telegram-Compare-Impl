package com.telegram.compare.kmp.shareddomain

class LoadSettingsUseCase(
    private val repository: SettingsRepository,
) {
    fun execute(session: UserSession): SettingsLoadResult {
        return repository.loadSettings(session)
    }
}

class TogglePreferenceUseCase(
    private val repository: SettingsRepository,
) {
    fun execute(
        session: UserSession,
        key: PreferenceKey,
        currentValue: Boolean,
    ): UpdatePreferenceResult {
        return repository.updatePreference(
            session = session,
            key = key,
            enabled = !currentValue,
        )
    }
}
