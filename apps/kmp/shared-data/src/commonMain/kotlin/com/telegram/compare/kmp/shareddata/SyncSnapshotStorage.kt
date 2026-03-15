package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.SyncSnapshot

interface SyncSnapshotStorage {
    fun read(): SyncSnapshot?

    fun write(snapshot: SyncSnapshot)

    fun clear()
}

class InMemorySyncSnapshotStorage(
    private var snapshot: SyncSnapshot? = null,
) : SyncSnapshotStorage {
    override fun read(): SyncSnapshot? = snapshot

    override fun write(snapshot: SyncSnapshot) {
        this.snapshot = snapshot
    }

    override fun clear() {
        snapshot = null
    }
}
