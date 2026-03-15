package com.telegram.compare.kmp.shareddata

import android.content.SharedPreferences
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.DeliveryState
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
        return runCatching { raw.toSyncSnapshot() }.getOrNull()
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
        route = SyncSnapshotRoute.valueOf(root.getString("route")),
        searchKeyword = root.optString("searchKeyword", ""),
        selectedChatId = root.optString("selectedChatId", "").ifBlank { null },
        chats = root.getJSONArray("chats").toChatSummaryList(),
        threads = root.getJSONArray("threads").toChatThreadList(),
    )
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
        id = getString("id"),
        title = getString("title"),
        lastMessagePreview = getString("lastMessagePreview"),
        unreadCount = getInt("unreadCount"),
        lastMessageAtLabel = getString("lastMessageAtLabel"),
        avatarLabel = getString("avatarLabel"),
        isMuted = getBoolean("isMuted"),
        statusLabel = getString("statusLabel"),
        avatarBackgroundColorHex = getString("avatarBackgroundColorHex"),
        avatarTextColorHex = getString("avatarTextColorHex"),
    )
}

private fun JSONObject.toChatThread(): ChatThread {
    return ChatThread(
        chat = getJSONObject("chat").toChatSummary(),
        messages = getJSONArray("messages").toMessageList(),
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
        id = getString("id"),
        chatId = getString("chatId"),
        text = getString("text"),
        sentAtLabel = getString("sentAtLabel"),
        isOutgoing = getBoolean("isOutgoing"),
        deliveryState = DeliveryState.valueOf(getString("deliveryState")),
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
}
