package com.telegram.compare.kmp.android

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ChatThread
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.ClearSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.LoadChatDetailUseCase
import com.telegram.compare.kmp.shareddomain.LoadChatListUseCase
import com.telegram.compare.kmp.shareddomain.LoginResult
import com.telegram.compare.kmp.shareddomain.LoginWithCodeUseCase
import com.telegram.compare.kmp.shareddomain.LogoutUseCase
import com.telegram.compare.kmp.shareddomain.Message
import com.telegram.compare.kmp.shareddomain.RefreshChatListUseCase
import com.telegram.compare.kmp.shareddomain.RestoreSessionUseCase
import com.telegram.compare.kmp.shareddomain.RetryChatMessageUseCase
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.RestoreSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.SaveSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.SendChatMessageUseCase
import com.telegram.compare.kmp.shareddomain.SendMessageResult
import com.telegram.compare.kmp.shareddomain.SessionRestoreResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRestoreResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRoute
import com.telegram.compare.kmp.shareddomain.UserSession
import com.telegram.compare.kmp.shareddata.ChatListScenario
import com.telegram.compare.kmp.shareddata.DemoSessionRepository
import com.telegram.compare.kmp.shareddata.InMemoryChatRepository
import com.telegram.compare.kmp.shareddata.PreferencesSessionStorage
import com.telegram.compare.kmp.shareddata.PreferencesSyncSnapshotStorage
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var chatRepository: InMemoryChatRepository
    private lateinit var sessionRepository: DemoSessionRepository

    private var screenState: MainScreenState = MainScreenState.Restoring
    private var phoneDraft = "+86 138 0000 0000"
    private var codeDraft = DemoSessionRepository.DEMO_VERIFICATION_CODE
    private var latestRestoreMessage: String? = null
    private var currentSession: UserSession? = null
    private var chatListStatusMessage: String? = null
    private var searchDraft: String = ""
    private var chatComposerDraft: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        sessionRepository = DemoSessionRepository(
            storage = PreferencesSessionStorage(
                sharedPreferences = sharedPreferences,
            ),
        )
        chatRepository = InMemoryChatRepository(
            snapshotStorage = PreferencesSyncSnapshotStorage(
                sharedPreferences = sharedPreferences,
            ),
        )

        render(MainScreenState.Restoring)
        startRestoreFlow()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (screenState is MainScreenState.ChatDetail) {
            returnToChatList()
            return
        }
        super.onBackPressed()
    }

    private fun startRestoreFlow() {
        handler.removeCallbacksAndMessages(null)
        render(MainScreenState.Restoring)

        handler.postDelayed(
            {
                when (val result = RestoreSessionUseCase(sessionRepository).execute()) {
                    is SessionRestoreResult.Restored -> {
                        latestRestoreMessage = null
                        currentSession = result.session
                        searchDraft = ""
                        chatComposerDraft = ""
                        restoreCachedContextOrLoadDefault()
                    }
                    SessionRestoreResult.NoSession -> {
                        latestRestoreMessage = null
                        currentSession = null
                        chatListStatusMessage = null
                        render(MainScreenState.Login())
                    }
                    is SessionRestoreResult.Failed -> {
                        latestRestoreMessage = result.message
                        currentSession = null
                        chatListStatusMessage = null
                        render(
                            MainScreenState.Login(
                                restoreMessage = result.message,
                            ),
                        )
                    }
                }
            },
            RESTORE_DELAY_MS,
        )
    }

    private fun restoreCachedContextOrLoadDefault() {
        when (val snapshotResult = RestoreSyncSnapshotUseCase(chatRepository).execute()) {
            is SyncSnapshotRestoreResult.Restored -> {
                val snapshot = snapshotResult.snapshot
                searchDraft = snapshot.searchKeyword
                chatComposerDraft = ""
                when (snapshot.route) {
                    SyncSnapshotRoute.CHAT_LIST -> {
                        chatListStatusMessage = "已从本地缓存恢复最近上下文，可能不是最新内容。"
                        renderChatListResult(
                            LoadChatListUseCase(chatRepository).execute(searchDraft),
                        )
                    }
                    SyncSnapshotRoute.CHAT_DETAIL -> {
                        val selectedChatId = snapshot.selectedChatId
                        if (selectedChatId == null) {
                            chatListStatusMessage = "本地缓存不可用，正在加载会话列表。"
                            loadChatList(showLoading = true, isRefresh = false)
                            return
                        }
                        when (val detailResult = LoadChatDetailUseCase(chatRepository).execute(selectedChatId)) {
                            is ChatDetailLoadResult.Success -> {
                                renderChatDetailResult(
                                    chatId = selectedChatId,
                                    chatTitle = detailResult.thread.chat.title,
                                    result = detailResult,
                                    statusMessage = "已从本地缓存恢复最近上下文，可能不是最新内容。",
                                )
                            }
                            is ChatDetailLoadResult.Failed -> {
                                chatListStatusMessage = "本地缓存不可用，正在加载会话列表。"
                                loadChatList(showLoading = true, isRefresh = false)
                            }
                        }
                    }
                }
            }
            SyncSnapshotRestoreResult.NoSnapshot -> {
                chatListStatusMessage = "已恢复上次会话，正在加载会话列表。"
                loadChatList(showLoading = true, isRefresh = false)
            }
            is SyncSnapshotRestoreResult.Failed -> {
                chatListStatusMessage = snapshotResult.message
                loadChatList(showLoading = true, isRefresh = false)
            }
        }
    }

    private fun submitLogin() {
        val restoreMessage = latestRestoreMessage
        render(
            MainScreenState.Login(
                restoreMessage = restoreMessage,
                isSubmitting = true,
            ),
        )

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                when (
                    val result = LoginWithCodeUseCase(sessionRepository).execute(
                        phoneNumber = phoneDraft,
                        verificationCode = codeDraft,
                    )
                ) {
                    is LoginResult.Success -> {
                        latestRestoreMessage = null
                        currentSession = result.session
                        searchDraft = ""
                        chatComposerDraft = ""
                        ClearSyncSnapshotUseCase(chatRepository).execute()
                        chatListStatusMessage = "登录成功，正在加载会话列表。"
                        loadChatList(showLoading = true, isRefresh = false)
                    }
                    is LoginResult.InvalidInput -> {
                        render(
                            MainScreenState.Login(
                                restoreMessage = restoreMessage,
                                formMessage = result.message,
                            ),
                        )
                    }
                    is LoginResult.Failed -> {
                        render(
                            MainScreenState.Login(
                                restoreMessage = restoreMessage,
                                formMessage = result.message,
                            ),
                        )
                    }
                }
            },
            LOGIN_DELAY_MS,
        )
    }

    private fun loadChatList(
        showLoading: Boolean,
        isRefresh: Boolean,
    ) {
        val session = currentSession ?: return
        val currentState = screenState as? MainScreenState.ChatList

        if (showLoading) {
            render(
                MainScreenState.ChatList(
                    session = session,
                    statusMessage = chatListStatusMessage,
                    contentState = ChatListContentState.Loading,
                    searchDraft = searchDraft,
                ),
            )
        } else if (isRefresh && currentState != null) {
            render(
                currentState.copy(
                    isRefreshing = true,
                    statusMessage = chatListStatusMessage,
                ),
            )
        }

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                val result = if (isRefresh) {
                    RefreshChatListUseCase(chatRepository).execute(searchDraft)
                } else {
                    LoadChatListUseCase(chatRepository).execute(searchDraft)
                }

                if (isRefresh) {
                    chatListStatusMessage = when (result) {
                        is ChatListLoadResult.Success -> "会话列表已刷新。"
                        ChatListLoadResult.Empty -> "当前没有可显示的会话。"
                        is ChatListLoadResult.Failed -> result.message
                    }
                }

                renderChatListResult(result)
            },
            if (isRefresh) REFRESH_DELAY_MS else CHAT_LIST_DELAY_MS,
        )
    }

    private fun renderChatListResult(result: ChatListLoadResult) {
        val session = currentSession ?: return
        val contentState = when (result) {
            is ChatListLoadResult.Success -> ChatListContentState.Ready(result.chats)
            ChatListLoadResult.Empty -> {
                if (searchDraft.isBlank()) {
                    ChatListContentState.Empty(
                        title = "暂无会话",
                        body = "当前 demo 数据为空。你可以恢复默认数据或稍后再试。",
                    )
                } else {
                    ChatListContentState.Empty(
                        title = "未找到匹配的会话",
                        body = "试试搜索会话名或最近消息中的关键词。",
                    )
                }
            }
            is ChatListLoadResult.Failed -> ChatListContentState.Error(result.message)
        }

        render(
            MainScreenState.ChatList(
                session = session,
                statusMessage = chatListStatusMessage,
                contentState = contentState,
                searchDraft = searchDraft,
                isRefreshing = false,
            ),
        )

        if (result is ChatListLoadResult.Success) {
            persistChatListSnapshot()
        }
    }

    private fun submitSearch() {
        val session = currentSession ?: return
        chatListStatusMessage = if (searchDraft.isBlank()) {
            "已恢复默认列表。"
        } else {
            "正在搜索 \"$searchDraft\"..."
        }
        render(
            MainScreenState.ChatList(
                session = session,
                statusMessage = chatListStatusMessage,
                contentState = ChatListContentState.Loading,
                searchDraft = searchDraft,
            ),
        )
        loadChatList(showLoading = false, isRefresh = false)
    }

    private fun clearSearch() {
        searchDraft = ""
        chatListStatusMessage = "已清除搜索条件。"
        loadChatList(showLoading = true, isRefresh = false)
    }

    private fun refreshChats() {
        loadChatList(showLoading = false, isRefresh = true)
    }

    private fun openChat(chat: ChatSummary) {
        val session = currentSession ?: return
        chatComposerDraft = ""
        render(
            MainScreenState.ChatDetail(
                session = session,
                chatId = chat.id,
                chatTitle = chat.title,
                chatSubtitle = chat.statusLabel,
                statusMessage = "正在打开 ${chat.title}...",
                contentState = ChatDetailContentState.Loading,
                composerDraft = chatComposerDraft,
                nextSendWillFail = chatRepository.nextSendWillFail(),
            ),
        )

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                val result = LoadChatDetailUseCase(chatRepository).execute(chat.id)
                renderChatDetailResult(
                    chatId = chat.id,
                    chatTitle = chat.title,
                    result = result,
                    statusMessage = when (result) {
                        is ChatDetailLoadResult.Success -> "已进入 ${result.thread.chat.title}。"
                        is ChatDetailLoadResult.Failed -> result.message
                    },
                )
            },
            CHAT_DETAIL_DELAY_MS,
        )
    }

    private fun renderChatDetailResult(
        chatId: String,
        chatTitle: String,
        result: ChatDetailLoadResult,
        statusMessage: String?,
        pendingOutgoingText: String? = null,
        retryingMessageId: String? = null,
    ) {
        val session = currentSession ?: return
        val contentState = when (result) {
            is ChatDetailLoadResult.Success -> ChatDetailContentState.Ready(result.thread)
            is ChatDetailLoadResult.Failed -> ChatDetailContentState.Error(result.message)
        }

        render(
            MainScreenState.ChatDetail(
                session = session,
                chatId = chatId,
                chatTitle = when (result) {
                    is ChatDetailLoadResult.Success -> result.thread.chat.title
                    is ChatDetailLoadResult.Failed -> chatTitle
                },
                chatSubtitle = when (result) {
                    is ChatDetailLoadResult.Success -> result.thread.chat.statusLabel
                    is ChatDetailLoadResult.Failed -> ""
                },
                statusMessage = statusMessage,
                contentState = contentState,
                composerDraft = chatComposerDraft,
                pendingOutgoingText = pendingOutgoingText,
                retryingMessageId = retryingMessageId,
                nextSendWillFail = chatRepository.nextSendWillFail(),
            ),
        )

        if (result is ChatDetailLoadResult.Success) {
            persistChatDetailSnapshot(chatId = chatId)
        }
    }

    private fun persistChatListSnapshot() {
        SaveSyncSnapshotUseCase(chatRepository).execute(
            route = SyncSnapshotRoute.CHAT_LIST,
            searchKeyword = searchDraft,
        )
    }

    private fun persistChatDetailSnapshot(chatId: String) {
        SaveSyncSnapshotUseCase(chatRepository).execute(
            route = SyncSnapshotRoute.CHAT_DETAIL,
            searchKeyword = searchDraft,
            selectedChatId = chatId,
        )
    }

    private fun submitMessage() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        val pendingText = chatComposerDraft.trim()
        if (pendingText.isBlank()) {
            render(currentState.copy(statusMessage = "请输入消息内容。", composerDraft = chatComposerDraft))
            return
        }

        chatComposerDraft = ""
        render(
            currentState.copy(
                statusMessage = "正在发送消息...",
                composerDraft = chatComposerDraft,
                pendingOutgoingText = pendingText,
                retryingMessageId = null,
            ),
        )

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                when (
                    val result = SendChatMessageUseCase(chatRepository).execute(
                        chatId = currentState.chatId,
                        text = pendingText,
                    )
                ) {
                    is SendMessageResult.Success -> {
                        chatListStatusMessage = "会话 ${result.thread.chat.title} 已有新消息。"
                        renderChatDetailResult(
                            chatId = currentState.chatId,
                            chatTitle = currentState.chatTitle,
                            result = ChatDetailLoadResult.Success(result.thread),
                            statusMessage = "消息已发送。",
                        )
                    }
                    is SendMessageResult.InvalidInput -> {
                        render(
                            currentState.copy(
                                statusMessage = result.message,
                                composerDraft = chatComposerDraft,
                                pendingOutgoingText = null,
                            ),
                        )
                    }
                    is SendMessageResult.Failed -> {
                        chatListStatusMessage = "会话 ${currentState.chatTitle} 有一条发送失败的消息。"
                        val contentState = result.thread?.let { ChatDetailContentState.Ready(it) }
                            ?: ChatDetailContentState.Error(result.message)
                        render(
                            currentState.copy(
                                statusMessage = result.message,
                                contentState = contentState,
                                composerDraft = chatComposerDraft,
                                pendingOutgoingText = null,
                                retryingMessageId = null,
                                nextSendWillFail = chatRepository.nextSendWillFail(),
                            ),
                        )
                        if (result.thread != null) {
                            persistChatDetailSnapshot(chatId = currentState.chatId)
                        }
                    }
                }
            },
            MESSAGE_SEND_DELAY_MS,
        )
    }

    private fun retryFailedMessage(messageId: String) {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(
            currentState.copy(
                statusMessage = "正在重试消息...",
                composerDraft = chatComposerDraft,
                retryingMessageId = messageId,
            ),
        )

        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(
            {
                when (
                    val result = RetryChatMessageUseCase(chatRepository).execute(
                        chatId = currentState.chatId,
                        messageId = messageId,
                    )
                ) {
                    is RetryMessageResult.Success -> {
                        chatListStatusMessage = "会话 ${result.thread.chat.title} 的失败消息已重试成功。"
                        renderChatDetailResult(
                            chatId = currentState.chatId,
                            chatTitle = currentState.chatTitle,
                            result = ChatDetailLoadResult.Success(result.thread),
                            statusMessage = "消息已重试发送。",
                        )
                    }
                    is RetryMessageResult.Failed -> {
                        val contentState = result.thread?.let { ChatDetailContentState.Ready(it) }
                            ?: ChatDetailContentState.Error(result.message)
                        render(
                            currentState.copy(
                                statusMessage = result.message,
                                contentState = contentState,
                                composerDraft = chatComposerDraft,
                                retryingMessageId = null,
                                nextSendWillFail = chatRepository.nextSendWillFail(),
                            ),
                        )
                        if (result.thread != null) {
                            persistChatDetailSnapshot(chatId = currentState.chatId)
                        }
                    }
                }
            },
            MESSAGE_RETRY_DELAY_MS,
        )
    }

    private fun toggleNextSendFailure() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        val nextState = !chatRepository.nextSendWillFail()
        chatRepository.setNextSendShouldFail(nextState)
        render(
            currentState.copy(
                statusMessage = if (nextState) {
                    "已开启“下一条发送失败”调试开关。"
                } else {
                    "已关闭“下一条发送失败”调试开关。"
                },
                composerDraft = chatComposerDraft,
                nextSendWillFail = nextState,
            ),
        )
    }

    private fun returnToChatList() {
        chatComposerDraft = ""
        chatListStatusMessage = chatListStatusMessage ?: "已返回会话列表。"
        loadChatList(showLoading = true, isRefresh = false)
    }

    private fun openComposePlaceholder() {
        val currentState = screenState as? MainScreenState.ChatList ?: return
        chatListStatusMessage = "写消息入口已保留，但真实新建会话将在后续切片实现。"
        render(currentState.copy(statusMessage = chatListStatusMessage))
    }

    private fun openEditPlaceholder() {
        val currentState = screenState as? MainScreenState.ChatList ?: return
        chatListStatusMessage = "编辑入口已预留，本轮不实现批量编辑。"
        render(currentState.copy(statusMessage = chatListStatusMessage))
    }

    private fun switchScenario(next: ChatListScenario) {
        if (next == ChatListScenario.DEFAULT) {
            chatRepository.restoreDefaultFixtures()
        } else {
            chatRepository.setChatListScenario(next)
        }
        searchDraft = ""
        chatListStatusMessage = when (next) {
            ChatListScenario.DEFAULT -> "已恢复默认 fixture 数据。"
            ChatListScenario.EMPTY -> "已切换到空态 fixture。"
            ChatListScenario.ERROR -> "已切换到错误态 fixture。"
        }
        loadChatList(showLoading = true, isRefresh = false)
    }

    private fun clearLocalSnapshot() {
        ClearSyncSnapshotUseCase(chatRepository).execute()
        chatListStatusMessage = "已清空本地缓存。下次冷启动将走正常加载路径。"

        when (val currentState = screenState) {
            is MainScreenState.ChatList -> render(
                currentState.copy(
                    statusMessage = chatListStatusMessage,
                ),
            )
            is MainScreenState.ChatDetail -> render(
                currentState.copy(
                    statusMessage = chatListStatusMessage,
                ),
            )
            else -> Unit
        }
    }

    private fun logout() {
        handler.removeCallbacksAndMessages(null)
        ClearSyncSnapshotUseCase(chatRepository).execute()
        LogoutUseCase(sessionRepository).execute()
        latestRestoreMessage = null
        currentSession = null
        searchDraft = ""
        chatComposerDraft = ""
        chatListStatusMessage = null
        chatRepository.restoreDefaultFixtures()
        render(MainScreenState.Login(formMessage = "已退出登录。"))
    }

    private fun seedExpiredSession() {
        sessionRepository.seedExpiredSession()
        val currentState = screenState as? MainScreenState.ChatList ?: return
        chatListStatusMessage = "已写入失效会话。请重新启动应用验证恢复失败路径。"
        render(currentState.copy(statusMessage = chatListStatusMessage))
    }

    private fun render(state: MainScreenState) {
        screenState = state
        val content = when (state) {
            MainScreenState.Restoring -> buildRestoringScreen()
            is MainScreenState.Login -> buildLoginScreen(state)
            is MainScreenState.ChatList -> buildChatListScreen(state)
            is MainScreenState.ChatDetail -> buildChatDetailScreen(state)
        }
        setContentView(content)
    }

    private fun buildRestoringScreen(): View {
        val root = screenRoot(
            gravity = Gravity.CENTER_HORIZONTAL,
            verticalGravity = Gravity.CENTER_VERTICAL,
            horizontalPaddingDp = 24,
            topPaddingDp = 24,
            bottomPaddingDp = 24,
        )

        root.addView(titleView("Telegram Compare", sizeSp = 24f))
        root.addView(space(20))
        root.addView(
            ProgressBar(this).apply {
                isIndeterminate = true
                contentDescription = "正在恢复会话"
            },
        )
        root.addView(space(18))
        root.addView(bodyView("正在恢复上次会话..."))
        root.addView(space(8))
        root.addView(
            secondaryView("如果没有保存的会话，将自动进入登录。").apply {
                gravity = Gravity.CENTER_HORIZONTAL
            },
        )

        return root
    }

    private fun buildLoginScreen(state: MainScreenState.Login): View {
        val root = screenRoot(
            horizontalPaddingDp = 24,
            topPaddingDp = 24,
            bottomPaddingDp = 24,
        )
        root.addView(weightedSpacer(1f))

        val container = baseColumn(
            horizontalPaddingDp = 0,
            topPaddingDp = 0,
            bottomPaddingDp = 0,
        ).apply {
            background = roundedBackground(
                fillColor = Color.WHITE,
                strokeColor = Color.parseColor("#E8EDF2"),
                radiusDp = 28,
            )
            setPadding(dp(24), dp(28), dp(24), dp(24))
        }

        container.addView(titleView("登录 Telegram Compare", sizeSp = 26f))
        container.addView(space(8))
        container.addView(secondaryView("使用固定 demo 验证码打通 S1 登录与会话恢复。"))

        state.restoreMessage?.let {
            container.addView(space(20))
            container.addView(errorBanner(it))
        }

        state.formMessage?.let {
            container.addView(space(12))
            container.addView(errorBanner(it))
        }

        container.addView(space(24))
        container.addView(labelView("手机号"))
        container.addView(
            inputField(
                initialValue = phoneDraft,
                hint = "+86 138 0000 0000",
                inputType = InputType.TYPE_CLASS_PHONE,
            ) { phoneDraft = it },
        )
        container.addView(space(16))
        container.addView(labelView("验证码"))
        container.addView(
            inputField(
                initialValue = codeDraft,
                hint = "2046",
                inputType = InputType.TYPE_CLASS_NUMBER,
            ) { codeDraft = it },
        )
        container.addView(space(24))
        container.addView(
            primaryButton(
                text = if (state.isSubmitting) "登录中..." else "继续",
                enabled = !state.isSubmitting,
            ) {
                submitLogin()
            },
        )
        container.addView(space(12))
        container.addView(
            secondaryButton("重试恢复") {
                startRestoreFlow()
            },
        )
        root.addView(container)
        root.addView(weightedSpacer(1.2f))
        root.addView(
            secondaryView("Demo 环境固定验证码: 2046").apply {
                gravity = Gravity.CENTER_HORIZONTAL
            },
        )

        return root
    }

    private fun buildChatListScreen(state: MainScreenState.ChatList): View {
        val root = screenRoot(
            horizontalPaddingDp = 16,
            topPaddingDp = 10,
            bottomPaddingDp = 12,
        )

        root.addView(topBar())
        root.addView(space(8))
        root.addView(searchBar(state))
        state.statusMessage?.let {
            root.addView(space(8))
            root.addView(infoBanner(it))
        }
        root.addView(space(8))
        root.addView(thinDivider())
        root.addView(space(4))

        val listContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(2), 0, dp(10))
        }

        when (val content = state.contentState) {
            ChatListContentState.Loading -> {
                repeat(4) { index ->
                    listContent.addView(chatSkeletonRow())
                    if (index < 3) {
                        listContent.addView(thinDivider())
                    }
                }
            }
            is ChatListContentState.Ready -> {
                content.chats.forEachIndexed { index, chat ->
                    listContent.addView(chatListRow(chat))
                    if (index < content.chats.lastIndex) {
                        listContent.addView(thinDivider())
                    }
                }
            }
            is ChatListContentState.Empty -> {
                listContent.addView(
                    emptyStateCard(
                        title = content.title,
                        body = content.body,
                        primaryText = if (state.searchDraft.isBlank()) "恢复默认数据" else "清除搜索",
                        onPrimaryClick = {
                            if (state.searchDraft.isBlank()) {
                                switchScenario(ChatListScenario.DEFAULT)
                            } else {
                                clearSearch()
                            }
                        },
                    ),
                )
            }
            is ChatListContentState.Error -> {
                listContent.addView(
                    errorStateCard(content.message) {
                        loadChatList(showLoading = true, isRefresh = false)
                    },
                )
            }
        }

        root.addView(
            wrapInSwipeRefresh(
                content = wrapInScroll(listContent),
                isRefreshing = state.isRefreshing,
            ).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1f,
                )
            },
        )
        root.addView(space(8))
        root.addView(debugSection())
        root.addView(space(8))
        root.addView(bottomNavigation())

        return root
    }

    private fun buildChatDetailScreen(state: MainScreenState.ChatDetail): View {
        val root = screenRoot(
            horizontalPaddingDp = 12,
            topPaddingDp = 10,
            bottomPaddingDp = 12,
            backgroundColor = Color.parseColor("#DCE8F4"),
        )

        root.addView(chatDetailTopBar(state))
        state.statusMessage?.let {
            root.addView(space(8))
            root.addView(infoBanner(it))
        }
        root.addView(space(8))

        when (val content = state.contentState) {
            ChatDetailContentState.Loading -> {
                val loadingPanel = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(4), dp(6), dp(4), dp(12))
                    repeat(6) { index ->
                        addView(messageSkeletonRow(outgoing = index % 2 == 1))
                        if (index < 5) {
                            addView(space(8))
                        }
                    }
                }
                root.addView(
                    wrapInScroll(
                        content = loadingPanel,
                        backgroundColor = Color.TRANSPARENT,
                    ).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0,
                            1f,
                        )
                    },
                )
                root.addView(space(8))
                root.addView(composerSection(state, enabled = false))
            }
            is ChatDetailContentState.Ready -> {
                root.addView(
                    wrapInScroll(
                        content = messageThreadView(content.thread, state),
                        backgroundColor = Color.TRANSPARENT,
                    ).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0,
                            1f,
                        )
                    },
                )
                root.addView(space(8))
                root.addView(
                    composerSection(
                        state = state,
                        enabled = state.pendingOutgoingText == null && state.retryingMessageId == null,
                    ),
                )
                root.addView(space(8))
                root.addView(detailDebugSection(state))
            }
            is ChatDetailContentState.Error -> {
                root.addView(
                    wrapInScroll(
                        content = LinearLayout(this).apply {
                            orientation = LinearLayout.VERTICAL
                            setPadding(dp(2), dp(20), dp(2), dp(20))
                            addView(detailErrorStateCard(content.message))
                        },
                        backgroundColor = Color.TRANSPARENT,
                    ).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0,
                            1f,
                        )
                    },
                )
            }
        }

        return root
    }

    private fun topBar(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            addView(
                topTextButton("编辑") {
                    openEditPlaceholder()
                }.apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                },
            )
            addView(
                titleView("Chats", sizeSp = 22f).apply {
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                },
            )
            addView(
                topTextButton("写消息") {
                    openComposePlaceholder()
                }.apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                },
            )
        }
    }

    private fun chatDetailTopBar(state: MainScreenState.ChatDetail): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            addView(topTextButton("返回") { returnToChatList() })
            addView(spaceWidth(8))
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    addView(
                        titleView(state.chatTitle, sizeSp = 20f).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        },
                    )
                    addView(
                        secondaryView(state.chatSubtitle.ifBlank { "Demo chat detail" }).apply {
                            gravity = Gravity.CENTER_HORIZONTAL
                        },
                    )
                },
            )
            addView(spaceWidth(8))
            addView(
                TextView(context).apply {
                    text = "S3"
                    textSize = 11f
                    setTypeface(Typeface.DEFAULT_BOLD)
                    setTextColor(Color.parseColor("#6C7884"))
                    background = roundedBackground(
                        fillColor = Color.parseColor("#E8EEF5"),
                        strokeColor = Color.TRANSPARENT,
                        radiusDp = 12,
                    )
                    setPadding(dp(10), dp(6), dp(10), dp(6))
                },
            )
        }
    }

    private fun searchBar(state: MainScreenState.ChatList): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#F1F3F6"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 18,
            )
            setPadding(dp(14), dp(6), dp(14), dp(6))

            addView(
                TextView(context).apply {
                    text = "搜索"
                    textSize = 13f
                    setTextColor(Color.parseColor("#8A95A1"))
                },
            )
            addView(spaceWidth(10))
            addView(
                EditText(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setText(state.searchDraft)
                    hint = "会话或消息"
                    inputType = InputType.TYPE_CLASS_TEXT
                    background = null
                    maxLines = 1
                    setTextColor(Color.parseColor("#25303A"))
                    setHintTextColor(Color.parseColor("#A3ADB8"))
                    addTextChangedListener(
                        object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int,
                            ) = Unit

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int,
                            ) = Unit

                            override fun afterTextChanged(s: Editable?) {
                                searchDraft = s?.toString().orEmpty()
                            }
                        },
                    )
                },
            )
            addView(spaceWidth(6))
            addView(
                topTextButton(if (state.searchDraft.isBlank()) "搜索" else "清除") {
                    if (state.searchDraft.isBlank()) {
                        submitSearch()
                    } else {
                        clearSearch()
                    }
                },
            )
        }
    }

    private fun chatListRow(chat: ChatSummary): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            minimumHeight = dp(72)
            setPadding(dp(2), dp(10), dp(2), dp(10))
            setOnClickListener {
                openChat(chat)
            }

            addView(avatarView(chat))
            addView(spaceWidth(12))
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

                    addView(
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            addView(
                                TextView(context).apply {
                                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                                    text = chat.title
                                    textSize = 16f
                                    maxLines = 1
                                    ellipsize = TextUtils.TruncateAt.END
                                    setTypeface(Typeface.DEFAULT_BOLD)
                                    setTextColor(Color.parseColor("#1F2730"))
                                },
                            )
                            addView(spaceWidth(8))
                            addView(
                                TextView(context).apply {
                                    text = chat.lastMessageAtLabel
                                    textSize = 12f
                                    setTextColor(Color.parseColor("#7F8A96"))
                                },
                            )
                        },
                    )
                    addView(space(4))
                    addView(
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            addView(
                                TextView(context).apply {
                                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                                    text = chat.lastMessagePreview
                                    textSize = 14f
                                    maxLines = 1
                                    ellipsize = TextUtils.TruncateAt.END
                                    setTextColor(Color.parseColor("#70808D"))
                                },
                            )
                            if (chat.isMuted) {
                                addView(spaceWidth(8))
                                addView(mutedBadge())
                            }
                            if (chat.unreadCount > 0) {
                                addView(spaceWidth(8))
                                addView(unreadBadge(chat.unreadCount))
                            }
                        },
                    )
                },
            )
        }
    }

    private fun chatSkeletonRow(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            minimumHeight = dp(72)
            setPadding(dp(2), dp(10), dp(2), dp(10))

            addView(
                View(context).apply {
                    layoutParams = LinearLayout.LayoutParams(dp(54), dp(54))
                    background = roundedBackground(
                        fillColor = Color.parseColor("#EBEFF4"),
                        strokeColor = Color.TRANSPARENT,
                        radiusDp = 27,
                    )
                },
            )
            addView(spaceWidth(12))
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    addView(
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            gravity = Gravity.CENTER_VERTICAL
                            addView(
                                skeletonBar(widthDp = 132).apply {
                                    layoutParams = LinearLayout.LayoutParams(0, dp(14), 1f)
                                },
                            )
                            addView(spaceWidth(12))
                            addView(skeletonBar(widthDp = 38, heightDp = 12))
                        },
                    )
                    addView(space(8))
                    addView(skeletonBar(widthDp = 212, heightDp = 12))
                },
            )
        }
    }

    private fun messageThreadView(
        thread: ChatThread,
        state: MainScreenState.ChatDetail,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(6), dp(4), dp(12))

            thread.messages.forEachIndexed { index, message ->
                addView(
                    messageRow(
                        message = message,
                        visualState = if (state.retryingMessageId == message.id) {
                            DeliveryState.SENDING
                        } else {
                            message.deliveryState
                        },
                        actionLabel = if (state.retryingMessageId == message.id) "重试中" else null,
                    ),
                )
                if (index < thread.messages.lastIndex || state.pendingOutgoingText != null) {
                    addView(space(8))
                }
            }

            state.pendingOutgoingText?.let { text ->
                addView(
                    messageRow(
                        message = Message(
                            id = "pending",
                            chatId = state.chatId,
                            text = text,
                            sentAtLabel = "刚刚",
                            isOutgoing = true,
                            deliveryState = DeliveryState.SENDING,
                        ),
                        visualState = DeliveryState.SENDING,
                        actionLabel = "发送中",
                    ),
                )
            }
        }
    }

    private fun messageRow(
        message: Message,
        visualState: DeliveryState,
        actionLabel: String? = null,
    ): LinearLayout {
        val isOutgoing = message.isOutgoing
        val isFailed = visualState == DeliveryState.FAILED
        val isBusy = actionLabel == "发送中" || actionLabel == "重试中"

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = if (isOutgoing) Gravity.END else Gravity.START
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    background = roundedBackground(
                        fillColor = when {
                            isFailed -> Color.parseColor("#FDECEC")
                            isOutgoing -> Color.parseColor("#D9FDD2")
                            else -> Color.WHITE
                        },
                        strokeColor = when {
                            isFailed -> Color.parseColor("#F1D0D0")
                            isOutgoing -> Color.TRANSPARENT
                            else -> Color.parseColor("#D6E0EA")
                        },
                        radiusDp = 18,
                    )
                    setPadding(dp(14), dp(10), dp(14), dp(10))

                    addView(
                        TextView(context).apply {
                            text = message.text
                            textSize = 15f
                            maxWidth = maxBubbleWidthPx()
                            setTextColor(Color.parseColor("#1B1F23"))
                        },
                    )
                    addView(space(8))
                    addView(
                        TextView(context).apply {
                            text = buildMessageMetaText(
                                message = message,
                                visualState = visualState,
                                actionLabel = actionLabel,
                            )
                            textSize = 11f
                            setTextColor(
                                when {
                                    isFailed -> Color.parseColor("#9A4B50")
                                    isBusy -> Color.parseColor("#477BA7")
                                    else -> Color.parseColor("#758290")
                                },
                            )
                        },
                    )

                    if (isFailed) {
                        addView(space(8))
                        addView(
                            compactChipButton("重试", active = true) {
                                retryFailedMessage(message.id)
                            },
                        )
                    }
                }.apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                },
            )
        }
    }

    private fun buildMessageMetaText(
        message: Message,
        visualState: DeliveryState,
        actionLabel: String?,
    ): String {
        if (!message.isOutgoing) {
            return message.sentAtLabel
        }

        val statusLabel = when {
            actionLabel != null -> actionLabel
            visualState == DeliveryState.SENT -> "已发送"
            visualState == DeliveryState.FAILED -> "发送失败"
            else -> "发送中"
        }
        return "${message.sentAtLabel} · $statusLabel"
    }

    private fun messageSkeletonRow(outgoing: Boolean): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = if (outgoing) Gravity.END else Gravity.START
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    background = roundedBackground(
                        fillColor = if (outgoing) {
                            Color.parseColor("#CFEFD0")
                        } else {
                            Color.parseColor("#F7FAFD")
                        },
                        strokeColor = Color.TRANSPARENT,
                        radiusDp = 18,
                    )
                    setPadding(dp(16), dp(12), dp(16), dp(12))
                    addView(skeletonBar(widthDp = if (outgoing) 142 else 168))
                    addView(space(8))
                    addView(skeletonBar(widthDp = if (outgoing) 74 else 96, heightDp = 12))
                },
            )
        }
    }

    private fun composerSection(
        state: MainScreenState.ChatDetail,
        enabled: Boolean,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = roundedBackground(
                fillColor = Color.WHITE,
                strokeColor = Color.parseColor("#D7E0E8"),
                radiusDp = 24,
            )
            setPadding(dp(8), dp(8), dp(8), dp(8))

            addView(
                TextView(context).apply {
                    text = "+"
                    textSize = 18f
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(dp(36), dp(36))
                    setTextColor(Color.parseColor("#7C8A96"))
                    background = roundedBackground(
                        fillColor = Color.parseColor("#F1F4F7"),
                        strokeColor = Color.TRANSPARENT,
                        radiusDp = 18,
                    )
                },
            )
            addView(spaceWidth(8))

            addView(
                EditText(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    setText(state.composerDraft)
                    hint = "输入消息"
                    inputType = InputType.TYPE_CLASS_TEXT
                    background = null
                    maxLines = 4
                    minLines = 1
                    isEnabled = enabled
                    setTextColor(Color.parseColor("#25303A"))
                    setHintTextColor(Color.parseColor("#9AA5AF"))
                    addTextChangedListener(
                        object : TextWatcher {
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int,
                            ) = Unit

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int,
                            ) = Unit

                            override fun afterTextChanged(s: Editable?) {
                                chatComposerDraft = s?.toString().orEmpty()
                            }
                        },
                    )
                },
            )
            addView(spaceWidth(8))
            addView(
                circleSendButton(
                    text = if (enabled) "发送" else "...",
                    enabled = enabled,
                ) {
                    submitMessage()
                },
            )
        }
    }

    private fun detailDebugSection(state: MainScreenState.ChatDetail): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#F7FAFD"),
                strokeColor = Color.parseColor("#E2E9F1"),
                radiusDp = 18,
            )
            setPadding(dp(12), dp(10), dp(12), dp(10))

            addView(
                secondaryView("Debug").apply {
                    setTypeface(Typeface.DEFAULT_BOLD)
                    setTextColor(Color.parseColor("#55636F"))
                },
            )
            addView(spaceWidth(10))
            addView(
                compactChipButton(
                    label = if (state.nextSendWillFail) "下一条将失败" else "下一条正常发送",
                    active = state.nextSendWillFail,
                ) {
                    toggleNextSendFailure()
                },
            )
            addView(spaceWidth(10))
            addView(
                secondaryView(
                    if (state.nextSendWillFail) {
                        "用于验收 failed / retry。"
                    } else {
                        "用于切换下一条失败。"
                    },
                ).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                },
            )
            addView(spaceWidth(10))
            addView(
                compactChipButton("清空缓存", active = false) {
                    clearLocalSnapshot()
                },
            )
        }
    }

    private fun emptyStateCard(
        title: String,
        body: String,
        primaryText: String,
        onPrimaryClick: () -> Unit,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#F8FAFC"),
                strokeColor = Color.parseColor("#E7EDF4"),
            )
            setPadding(dp(20), dp(22), dp(20), dp(22))
            gravity = Gravity.CENTER_HORIZONTAL

            addView(titleView(title, sizeSp = 20f).apply { gravity = Gravity.CENTER_HORIZONTAL })
            addView(space(8))
            addView(
                secondaryView(body).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                },
            )
            addView(space(18))
            addView(primaryButton(primaryText) { onPrimaryClick() })
        }
    }

    private fun errorStateCard(
        message: String,
        onRetry: () -> Unit,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#FFF6F5"),
                strokeColor = Color.parseColor("#F4D7D4"),
            )
            setPadding(dp(20), dp(22), dp(20), dp(22))

            addView(titleView("列表加载失败", sizeSp = 20f))
            addView(space(8))
            addView(secondaryView(message))
            addView(space(18))
            addView(primaryButton("重试加载") { onRetry() })
        }
    }

    private fun detailErrorStateCard(message: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#FFF6F5"),
                strokeColor = Color.parseColor("#F4D7D4"),
            )
            setPadding(dp(20), dp(22), dp(20), dp(22))

            addView(titleView("聊天详情加载失败", sizeSp = 20f))
            addView(space(8))
            addView(secondaryView(message))
            addView(space(18))
            addView(
                primaryButton("重试加载") {
                    openChat(
                        ChatSummary(
                            id = (screenState as? MainScreenState.ChatDetail)?.chatId.orEmpty(),
                            title = (screenState as? MainScreenState.ChatDetail)?.chatTitle.orEmpty(),
                            lastMessagePreview = "",
                            unreadCount = 0,
                            lastMessageAtLabel = "",
                            avatarLabel = "TG",
                        ),
                    )
                },
            )
            addView(space(10))
            addView(
                secondaryButton("返回列表") {
                    returnToChatList()
                },
            )
        }
    }

    private fun debugSection(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#F7FAFD"),
                strokeColor = Color.parseColor("#E2E9F1"),
                radiusDp = 18,
            )
            setPadding(dp(12), dp(10), dp(12), dp(10))

            addView(
                secondaryView("Debug controls").apply {
                    setTypeface(Typeface.DEFAULT_BOLD)
                    setTextColor(Color.parseColor("#55636F"))
                },
            )
            addView(space(8))
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    addView(debugScenarioButton("默认", ChatListScenario.DEFAULT))
                    addView(spaceWidth(8))
                    addView(debugScenarioButton("空态", ChatListScenario.EMPTY))
                    addView(spaceWidth(8))
                    addView(debugScenarioButton("错误态", ChatListScenario.ERROR))
                },
            )
            addView(space(8))
            addView(
                LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    addView(
                        compactChipButton("退出登录", active = false) {
                            logout()
                        }.apply {
                            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        },
                    )
                    addView(spaceWidth(8))
                    addView(
                        compactChipButton("写入失效会话", active = false) {
                            seedExpiredSession()
                        }.apply {
                            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        },
                    )
                },
            )
            addView(space(8))
            addView(
                compactChipButton("清空本地缓存", active = false) {
                    clearLocalSnapshot()
                }.apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    )
                },
            )
        }
    }

    private fun debugScenarioButton(
        label: String,
        scenario: ChatListScenario,
    ): Button {
        val active = chatRepository.currentChatListScenario() == scenario
        return Button(this).apply {
            text = label
            isAllCaps = false
            setTextColor(if (active) Color.WHITE else Color.parseColor("#2481CC"))
            background = roundedBackground(
                fillColor = if (active) Color.parseColor("#2481CC") else Color.parseColor("#EAF3FB"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 16,
            )
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnClickListener { switchScenario(scenario) }
        }
    }

    private fun bottomNavigation(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = roundedBackground(
                fillColor = Color.parseColor("#F7FAFD"),
                strokeColor = Color.parseColor("#E3EAF1"),
                radiusDp = 26,
            )
            setPadding(dp(8), dp(8), dp(8), dp(8))

            addView(bottomTab("Chats", selected = true))
            addView(bottomTab("Calls", selected = false))
            addView(bottomTab("Settings", selected = false))
        }
    }

    private fun bottomTab(
        text: String,
        selected: Boolean,
    ): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = dp(4)
                marginEnd = dp(4)
            }
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setTypeface(if (selected) Typeface.DEFAULT_BOLD else Typeface.DEFAULT)
            setTextColor(
                if (selected) Color.parseColor("#1B1F23") else Color.parseColor("#7B8794"),
            )
            background = roundedBackground(
                fillColor = if (selected) Color.parseColor("#EAF3FB") else Color.TRANSPARENT,
                strokeColor = Color.TRANSPARENT,
                radiusDp = 20,
            )
        }
    }

    private fun avatarView(chat: ChatSummary): TextView {
        return TextView(this).apply {
            text = chat.avatarLabel
            textSize = 16f
            gravity = Gravity.CENTER
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor(chat.avatarTextColorHex))
            layoutParams = LinearLayout.LayoutParams(dp(54), dp(54))
            background = roundedBackground(
                fillColor = Color.parseColor(chat.avatarBackgroundColorHex),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 27,
            )
        }
    }

    private fun mutedBadge(): TextView {
        return TextView(this).apply {
            text = "静音"
            textSize = 11f
            setTextColor(Color.parseColor("#7F8A96"))
        }
    }

    private fun unreadBadge(count: Int): TextView {
        return TextView(this).apply {
            text = if (count > 0) count.toString() else ""
            textSize = 12f
            gravity = Gravity.CENTER
            minWidth = dp(24)
            setPadding(dp(8), dp(4), dp(8), dp(4))
            setTextColor(if (count > 0) Color.WHITE else Color.parseColor("#9AA6B2"))
            background = roundedBackground(
                fillColor = if (count > 0) Color.parseColor("#2481CC") else Color.parseColor("#E7EDF2"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 12,
            )
        }
    }

    private fun topTextButton(
        text: String,
        onClick: () -> Unit,
    ): Button {
        return Button(this).apply {
            this.text = text
            isAllCaps = false
            setTextColor(Color.parseColor("#2481CC"))
            background = null
            minWidth = 0
            minimumWidth = 0
            setPadding(dp(4), dp(6), dp(4), dp(6))
            setOnClickListener { onClick() }
        }
    }

    private fun compactChipButton(
        label: String,
        active: Boolean,
        onClick: () -> Unit,
    ): Button {
        return Button(this).apply {
            text = label
            isAllCaps = false
            setTextColor(if (active) Color.WHITE else Color.parseColor("#2481CC"))
            background = roundedBackground(
                fillColor = if (active) Color.parseColor("#2481CC") else Color.parseColor("#EAF3FB"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 16,
            )
            setPadding(dp(12), dp(8), dp(12), dp(8))
            setOnClickListener { onClick() }
        }
    }

    private fun circleSendButton(
        text: String,
        enabled: Boolean,
        onClick: () -> Unit,
    ): Button {
        return Button(this).apply {
            this.text = text
            isAllCaps = false
            isEnabled = enabled
            setTextColor(Color.WHITE)
            background = roundedBackground(
                fillColor = if (enabled) Color.parseColor("#2481CC") else Color.parseColor("#A6C8E5"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 20,
            )
            setPadding(dp(14), dp(10), dp(14), dp(10))
            setOnClickListener { onClick() }
        }
    }

    private fun wrapInScroll(
        content: LinearLayout,
        backgroundColor: Int = Color.WHITE,
    ): ScrollView {
        return ScrollView(this).apply {
            setBackgroundColor(backgroundColor)
            isFillViewport = true
            isVerticalScrollBarEnabled = false
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }

    private fun wrapInSwipeRefresh(
        content: View,
        isRefreshing: Boolean,
    ): SwipeRefreshLayout {
        return SwipeRefreshLayout(this).apply {
            setColorSchemeColors(Color.parseColor("#2481CC"))
            setBackgroundColor(Color.WHITE)
            this.isRefreshing = isRefreshing
            setOnRefreshListener { refreshChats() }
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                ),
            )
        }
    }

    private fun baseColumn(
        gravity: Int = Gravity.TOP,
        verticalGravity: Int = Gravity.NO_GRAVITY,
        horizontalPaddingDp: Int = 24,
        topPaddingDp: Int = 32,
        bottomPaddingDp: Int = 32,
        backgroundColor: Int = Color.WHITE,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(
                dp(horizontalPaddingDp),
                dp(topPaddingDp),
                dp(horizontalPaddingDp),
                dp(bottomPaddingDp),
            )
            this.gravity = gravity or verticalGravity
        }
    }

    private fun screenRoot(
        gravity: Int = Gravity.TOP,
        verticalGravity: Int = Gravity.NO_GRAVITY,
        horizontalPaddingDp: Int = 24,
        topPaddingDp: Int = 24,
        bottomPaddingDp: Int = 24,
        backgroundColor: Int = Color.WHITE,
    ): LinearLayout {
        return baseColumn(
            gravity = gravity,
            verticalGravity = verticalGravity,
            horizontalPaddingDp = horizontalPaddingDp,
            topPaddingDp = topPaddingDp,
            bottomPaddingDp = bottomPaddingDp,
            backgroundColor = backgroundColor,
        ).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private fun weightedSpacer(weight: Float): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                weight,
            )
        }
    }

    private fun titleView(
        text: String,
        sizeSp: Float = 28f,
    ): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = sizeSp
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor("#1B1F23"))
        }
    }

    private fun bodyView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 17f
            gravity = Gravity.CENTER_HORIZONTAL
            setTextColor(Color.parseColor("#1B1F23"))
        }
    }

    private fun secondaryView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(Color.parseColor("#667781"))
        }
    }

    private fun labelView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor("#41505A"))
        }
    }

    private fun inputField(
        initialValue: String,
        hint: String,
        inputType: Int,
        onTextChanged: (String) -> Unit,
    ): EditText {
        return EditText(this).apply {
            setText(initialValue)
            this.hint = hint
            this.inputType = inputType
            setTextColor(Color.parseColor("#1B1F23"))
            setHintTextColor(Color.parseColor("#99A4AE"))
            background = roundedBackground(
                fillColor = Color.parseColor("#F4F5F7"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(14), dp(16), dp(14))
            addTextChangedListener(
                object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) = Unit

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) = Unit

                    override fun afterTextChanged(s: Editable?) {
                        onTextChanged(s?.toString().orEmpty())
                    }
                },
            )
        }
    }

    private fun primaryButton(
        text: String,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ): Button {
        return Button(this).apply {
            this.text = text
            isAllCaps = false
            isEnabled = enabled
            setTextColor(Color.WHITE)
            background = roundedBackground(
                fillColor = if (enabled) Color.parseColor("#2481CC") else Color.parseColor("#A6C8E5"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(16), dp(16), dp(16))
            setOnClickListener { onClick() }
        }
    }

    private fun secondaryButton(
        text: String,
        onClick: () -> Unit,
    ): Button {
        return Button(this).apply {
            this.text = text
            isAllCaps = false
            setTextColor(Color.parseColor("#2481CC"))
            background = roundedBackground(
                fillColor = Color.parseColor("#EAF3FB"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setOnClickListener { onClick() }
        }
    }

    private fun errorBanner(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(Color.parseColor("#8F2D31"))
            background = roundedBackground(
                fillColor = Color.parseColor("#FDECEC"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
    }

    private fun infoBanner(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(Color.parseColor("#1F5F8B"))
            background = roundedBackground(
                fillColor = Color.parseColor("#EEF7FF"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = 16,
            )
            setPadding(dp(14), dp(10), dp(14), dp(10))
        }
    }

    private fun thinDivider(): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1),
            )
            setBackgroundColor(Color.parseColor("#EDF1F5"))
        }
    }

    private fun skeletonBar(
        widthDp: Int,
        heightDp: Int = 14,
    ): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(dp(widthDp), dp(heightDp))
            background = roundedBackground(
                fillColor = Color.parseColor("#EEF1F4"),
                strokeColor = Color.TRANSPARENT,
                radiusDp = heightDp / 2,
            )
        }
    }

    private fun roundedBackground(
        fillColor: Int,
        strokeColor: Int,
        radiusDp: Int = 18,
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(radiusDp).toFloat()
            setColor(fillColor)
            if (strokeColor != Color.TRANSPARENT) {
                setStroke(dp(1), strokeColor)
            }
        }
    }

    private fun space(heightDp: Int): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightDp),
            )
        }
    }

    private fun spaceWidth(widthDp: Int): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                dp(widthDp),
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    private fun messagePanelContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#EAF1F7"),
                strokeColor = Color.parseColor("#DCE7F1"),
                radiusDp = 24,
            )
            setPadding(dp(12), dp(14), dp(12), dp(14))
        }
    }

    private fun maxBubbleWidthPx(): Int = (resources.displayMetrics.widthPixels * 0.72f).roundToInt()

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    private companion object {
        const val PREFS_NAME = "telegram_compare_session"
        const val RESTORE_DELAY_MS = 650L
        const val LOGIN_DELAY_MS = 450L
        const val CHAT_LIST_DELAY_MS = 320L
        const val REFRESH_DELAY_MS = 520L
        const val CHAT_DETAIL_DELAY_MS = 260L
        const val MESSAGE_SEND_DELAY_MS = 520L
        const val MESSAGE_RETRY_DELAY_MS = 420L
    }
}
