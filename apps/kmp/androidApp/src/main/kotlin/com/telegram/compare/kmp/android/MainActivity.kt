package com.telegram.compare.kmp.android

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import com.telegram.compare.kmp.shareddomain.BuildBootstrapSummaryUseCase
import com.telegram.compare.kmp.shareddata.InMemoryChatRepository

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = InMemoryChatRepository()
        val summary = BuildBootstrapSummaryUseCase(repository).execute(chatId = "chat-1")
        val moduleOrder = summary.moduleOrder.joinToString(separator = " -> ")
        val lines = buildList {
            add("Telegram Compare KMP")
            add("")
            add("First slice: ${summary.firstSlice}")
            add("Sample chats: ${summary.chatCount}")
            add("Sample messages: ${summary.messageCount}")
            add("Module order: $moduleOrder")
            add("")
            add("Next steps:")
            summary.nextSteps.forEachIndexed { index, step ->
                add("${index + 1}. $step")
            }
        }

        val textView = TextView(this).apply {
            text = lines.joinToString(separator = "\n")
            textSize = 16f
            setPadding(48, 64, 48, 64)
        }

        setContentView(
            ScrollView(this).apply {
                addView(textView)
            },
        )
    }
}
