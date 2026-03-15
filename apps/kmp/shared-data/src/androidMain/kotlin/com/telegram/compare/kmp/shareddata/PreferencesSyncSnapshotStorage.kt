package com.telegram.compare.kmp.shareddata

import android.content.SharedPreferences
import android.util.Log
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.MediaAttachment
import com.telegram.compare.kmp.shareddomain.Message
import com.telegram.compare.kmp.shareddomain.SyncSnapshot
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRoute
import org.json.JSONArray
import org.json.JSONObject

class PreferencesSyncSnapshotStorage(
    private val sharedPreferences: SharedPreferences,
) : SyncSnapshotStorage {
    override fun read(): SyncSnapshot? {
        val raw = sharedPreferences.getString(KEY_SYNC_SNAPSHOT, null) ?: return null
        return try {
            raw.toSyncSnapshot()
        } catch (error: Exception) {
            Log.w(TAG, "Failed to deserialize sync snapshot, discarding cache", error)
            null
        }
    }

    override fun write(snapshot: SyncSnapshot) {
        sharedPreferences.edit()
            .putString(KEY_SYNC_SNAPSHOT, snapshot.toJson().toString())
            .apply()
    }

    override fun clear() {
        sharedPreferences.edit()
            .remove(KEY_SYNC_SNAPSHOT)
            .apply()
    }

    private companion object {
        const val KEY_SYNC_SNAPSHOT = "sync_snapshot"
        const val TAG = "SyncSnapshotStorage"
    }
}

private fun SyncSnapshot.toJson(): JSONObject {
    return JSONObject()
        .put("route", route.name)
        .put("searchKeyword", searchKeyword)
        .put("selectedChatId", selectedChatId)
        .put("chats", JSONArray().apply { chats.forEach { put(it.toJson()) } })
        .put("threads", JSONArray().apply { threads.forEach { put(it.toJson()) } })
}

private fun String.toSyncSnapshot(): SyncSnapshot {
    val root = JSONObject(this)
    return SyncSnapshot(
        route = root.optString("route", SyncSnapshotRoute.CHAT_LIST.name).toSyncSnapshotRoute(),
        searchKeyword = root.optString("searchKeyword", ""),
        selectedChatId = root.optString("selectedChatId", "").ifBlank { null },
        chats = root.optJSONArray("chats")?.toChatSummaryList().orEmpty(),
        threads = root.optJSONArray("threads")?.toChatThreadList().orEmpty(),
    )
}

private fun String.toSyncSnapshotRoute(): SyncSnapshotRoute {
    return SyncSnapshotRoute.entries.firstOrNull { it.name == this } ?: SyncSnapshotRoute.CHAT_LIST
}

private fun String.toDeliveryState(): DeliveryState {
    return DeliveryState.entries.firstOrNull { it.name == this } ?: DeliveryState.SENT
}

private fun JSONArray.toChatSummaryList(): List<ChatSummary> {
    return buildList(length()) {
        for (index in 0 until length()) {
            add(getJSONObject(index).toChatSummary())
        }
    }
}

private fun JSONArray.toChatThreadList(): List<ChatThread> {
    return buildList(length()) {
        for (index in 0 until length()) {
            add(getJSONObject(index).toChatThread())
        }
    }
}

private fun JSONObject.toChatSummary(): ChatSummary {
    return ChatSummary(
        id = optString("id", ""),
        title = optString("title", ""),
        lastMessagePreview = optString("lastMessagePreview", ""),
        unreadCount = optInt("unreadCount", 0),
        lastMessageAtLabel = optString("lastMessageAtLabel", ""),
        avatarLabel = optString("avatarLabel", "TG"),
        isMuted = optBoolean("isMuted", false),
        statusLabel = optString("statusLabel", ""),
        avatarBackgroundColorHex = optString("avatarBackgroundColorHex", "#EAF3FB"),
        avatarTextColorHex = optString("avatarTextColorHex", "#1F5F8B"),
    )
}

private fun JSONObject.toChatThread(): ChatThread {
    return ChatThread(
        chat = optJSONObject("chat")?.toChatSummary()
            ?: ChatSummary(
                id = "",
                title = "",
                lastMessagePreview = "",
                unreadCount = 0,
                lastMessageAtLabel = "",
                avatarLabel = "TG",
            ),
        messages = optJSONArray("messages")?.toMessageList().orEmpty(),
    )
}

private fun JSONArray.toMessageList(): List<Message> {
    return buildList(length()) {
        for (index in 0 until length()) {
            add(getJSONObject(index).toMessage())
        }
    }
}

private fun JSONObject.toMessage(): Message {
    return Message(
        id = optString("id", ""),
        chatId = optString("chatId", ""),
        text = optString("text", ""),
        sentAtLabel = optString("sentAtLabel", ""),
        isOutgoing = optBoolean("isOutgoing", false),
        deliveryState = optString("deliveryState", DeliveryState.SENT.name).toDeliveryState(),
        mediaAttachment = optJSONObject("mediaAttachment")?.toMediaAttachment(),
    )
}

private fun ChatSummary.toJson(): JSONObject {
    return JSONObject()
        .put("id", id)
        .put("title", title)
        .put("lastMessagePreview", lastMessagePreview)
        .put("unreadCount", unreadCount)
        .put("lastMessageAtLabel", lastMessageAtLabel)
        .put("avatarLabel", avatarLabel)
        .put("isMuted", isMuted)
        .put("statusLabel", statusLabel)
        .put("avatarBackgroundColorHex", avatarBackgroundColorHex)
        .put("avatarTextColorHex", avatarTextColorHex)
}

private fun ChatThread.toJson(): JSONObject {
    return JSONObject()
        .put("chat", chat.toJson())
        .put("messages", JSONArray().apply { messages.forEach { put(it.toJson()) } })
}

private fun Message.toJson(): JSONObject {
    return JSONObject()
        .put("id", id)
        .put("chatId", chatId)
        .put("text", text)
        .put("sentAtLabel", sentAtLabel)
        .put("isOutgoing", isOutgoing)
        .put("deliveryState", deliveryState.name)
        .put("mediaAttachment", mediaAttachment?.toJson())
}

private fun JSONObject.toMediaAttachment(): MediaAttachment {
    return MediaAttachment(
        id = optString("id", ""),
        title = optString("title", ""),
        defaultCaption = optString("defaultCaption", ""),
        accentColorHex = optString("accentColorHex", "#EAF3FB"),
    )
}

private fun MediaAttachment.toJson(): JSONObject {
    return JSONObject()
        .put("id", id)
        .put("title", title)
        .put("defaultCaption", defaultCaption)
        .put("accentColorHex", accentColorHex)
}
