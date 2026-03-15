package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals

class SyncUseCasesTest {
    @Test
    fun trimsSearchKeywordAndChatIdBeforeSavingSnapshot() {
        val repository = FakeSyncRepository()

        SaveSyncSnapshotUseCase(repository).execute(
            route = SyncSnapshotRoute.CHAT_DETAIL,
            searchKeyword = "  infra  ",
            selectedChatId = " chat-2 ",
        )

        assertEquals("infra", repository.lastRequest?.searchKeyword)
        assertEquals("chat-2", repository.lastRequest?.selectedChatId)
    }

    @Test
    fun convertsBlankChatIdToNullBeforeSavingSnapshot() {
        val repository = FakeSyncRepository()

        SaveSyncSnapshotUseCase(repository).execute(
            route = SyncSnapshotRoute.CHAT_LIST,
            searchKeyword = "  ",
            selectedChatId = "   ",
        )

        assertEquals("", repository.lastRequest?.searchKeyword)
        assertEquals(null, repository.lastRequest?.selectedChatId)
    }
}

private class FakeSyncRepository : SyncRepository {
    var lastRequest: SyncSnapshotRequest? = null

    override fun restoreSnapshot(): SyncSnapshotRestoreResult {
        return SyncSnapshotRestoreResult.NoSnapshot
    }

    override fun saveSnapshot(request: SyncSnapshotRequest): SyncSnapshotSaveResult {
        lastRequest = request
        return SyncSnapshotSaveResult.Failed("stub")
    }

    override fun clearSnapshot() = Unit
}
