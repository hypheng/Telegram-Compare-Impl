package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BuildBootstrapSummaryUseCaseTest {
    @Test
    fun buildsSummaryFromRepositoryData() {
        val repository = object : ChatRepository {
            override fun listChats(): List<ChatSummary> {
                return listOf(
                    ChatSummary(
                        id = "chat-1",
                        title = "Core Team",
                        unreadCount = 2,
                        lastMessagePreview = "Need KMP bootstrap",
                    ),
                )
            }

            override fun listMessages(chatId: String): List<Message> {
                return listOf(
                    Message(
                        id = "message-1",
                        chatId = chatId,
                        text = "Bootstrap looks good",
                        deliveryState = DeliveryState.SENT,
                    ),
                )
            }

            override fun sendMessage(chatId: String, text: String): Message {
                return Message(
                    id = "message-2",
                    chatId = chatId,
                    text = text,
                    deliveryState = DeliveryState.SENT,
                )
            }
        }

        val summary = BuildBootstrapSummaryUseCase(repository).execute(chatId = "chat-1")

        assertEquals("S3 单聊详情与文本发送", summary.firstSlice)
        assertEquals(1, summary.chatCount)
        assertEquals(1, summary.messageCount)
        assertTrue(summary.moduleOrder.contains("androidApp"))
    }
}
