package com.telegram.compare.kmp.shareddomain

class RestoreSyncSnapshotUseCase(
    private val repository: SyncRepository,
) {
    fun execute(): SyncSnapshotRestoreResult = repository.restoreSnapshot()
}

class SaveSyncSnapshotUseCase(
    private val repository: SyncRepository,
) {
    fun execute(
        route: SyncSnapshotRoute,
        searchKeyword: String = "",
        selectedChatId: String? = null,
    ): SyncSnapshotSaveResult {
        return repository.saveSnapshot(
            SyncSnapshotRequest(
                route = route,
                searchKeyword = searchKeyword.trim(),
                selectedChatId = selectedChatId?.trim()?.ifBlank { null },
            ),
        )
    }
}

class ClearSyncSnapshotUseCase(
    private val repository: SyncRepository,
) {
    fun execute() {
        repository.clearSnapshot()
    }
}
