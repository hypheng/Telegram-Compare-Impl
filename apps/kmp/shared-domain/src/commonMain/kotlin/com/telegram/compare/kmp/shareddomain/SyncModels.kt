package com.telegram.compare.kmp.shareddomain

enum class SyncSnapshotRoute {
    CHAT_LIST,
    CHAT_DETAIL,
}

data class SyncSnapshotRequest(
    val route: SyncSnapshotRoute,
    val searchKeyword: String = "",
    val selectedChatId: String? = null,
)

data class SyncSnapshot(
    val route: SyncSnapshotRoute,
    val searchKeyword: String = "",
    val selectedChatId: String? = null,
    val chats: List<ChatSummary>,
    val threads: List<ChatThread>,
    val contacts: List<ContactSnapshot> = emptyList(),
)

data class ContactSnapshot(
    val id: String,
    val displayName: String,
    val phoneNumber: String,
    val avatarLabel: String,
    val statusLabel: String = "",
    val linkedChatId: String? = null,
)

sealed interface SyncSnapshotRestoreResult {
    data class Restored(val snapshot: SyncSnapshot) : SyncSnapshotRestoreResult

    object NoSnapshot : SyncSnapshotRestoreResult

    data class Failed(val message: String) : SyncSnapshotRestoreResult
}

sealed interface SyncSnapshotSaveResult {
    data class Success(val snapshot: SyncSnapshot) : SyncSnapshotSaveResult

    data class Failed(val message: String) : SyncSnapshotSaveResult
}

interface SyncRepository {
    fun restoreSnapshot(): SyncSnapshotRestoreResult

    fun saveSnapshot(request: SyncSnapshotRequest): SyncSnapshotSaveResult

    fun clearSnapshot()
}
