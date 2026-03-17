package com.telegram.compare.kmp.android

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.telegram.compare.kmp.shareddomain.ChatDetailLoadResult
import com.telegram.compare.kmp.shareddomain.ChatListLoadResult
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.ClearSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.ContactSummary
import com.telegram.compare.kmp.shareddomain.ContactsLoadResult
import com.telegram.compare.kmp.shareddomain.LoadChatDetailUseCase
import com.telegram.compare.kmp.shareddomain.LoadChatListUseCase
import com.telegram.compare.kmp.shareddomain.LoadContactsUseCase
import com.telegram.compare.kmp.shareddomain.LoginResult
import com.telegram.compare.kmp.shareddomain.LoginWithCodeUseCase
import com.telegram.compare.kmp.shareddomain.LogoutUseCase
import com.telegram.compare.kmp.shareddomain.LoadAvailableMediaUseCase
import com.telegram.compare.kmp.shareddomain.MediaPickerLoadResult
import com.telegram.compare.kmp.shareddomain.MessageSearchHit
import com.telegram.compare.kmp.shareddomain.LoadSettingsUseCase
import com.telegram.compare.kmp.shareddomain.OpenContactChatResult
import com.telegram.compare.kmp.shareddomain.OpenContactChatUseCase
import com.telegram.compare.kmp.shareddomain.PreferenceKey
import com.telegram.compare.kmp.shareddomain.RefreshChatListUseCase
import com.telegram.compare.kmp.shareddomain.RestoreSessionUseCase
import com.telegram.compare.kmp.shareddomain.RetryChatMessageUseCase
import com.telegram.compare.kmp.shareddomain.RetryMessageResult
import com.telegram.compare.kmp.shareddomain.RestoreSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.SaveSyncSnapshotUseCase
import com.telegram.compare.kmp.shareddomain.SearchChatsAndMessagesUseCase
import com.telegram.compare.kmp.shareddomain.SearchLoadResult
import com.telegram.compare.kmp.shareddomain.SendChatMessageUseCase
import com.telegram.compare.kmp.shareddomain.SendChatMediaUseCase
import com.telegram.compare.kmp.shareddomain.SendMediaResult
import com.telegram.compare.kmp.shareddomain.SendMessageResult
import com.telegram.compare.kmp.shareddomain.SessionRestoreResult
import com.telegram.compare.kmp.shareddomain.SettingsLoadResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRestoreResult
import com.telegram.compare.kmp.shareddomain.SyncSnapshotRoute
import com.telegram.compare.kmp.shareddomain.TogglePreferenceUseCase
import com.telegram.compare.kmp.shareddomain.UpdatePreferenceResult
import com.telegram.compare.kmp.shareddomain.UserSession
import com.telegram.compare.kmp.shareddata.ChatListScenario
import com.telegram.compare.kmp.shareddata.ContactListScenario
import com.telegram.compare.kmp.shareddata.DemoSessionRepository
import com.telegram.compare.kmp.shareddata.InMemoryChatDebugController
import com.telegram.compare.kmp.shareddata.InMemoryChatFixtureBundle
import com.telegram.compare.kmp.shareddata.LocalSettingsRepository
import com.telegram.compare.kmp.shareddata.PreferencesSessionStorage
import com.telegram.compare.kmp.shareddata.PreferencesSyncSnapshotStorage
import com.telegram.compare.kmp.shareddata.PreferencesUserSettingsStorage
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())
    private var pendingWork: Runnable? = null

    private lateinit var chatFixtureBundle: InMemoryChatFixtureBundle
    private lateinit var chatDebugController: InMemoryChatDebugController
    private lateinit var sessionRepository: DemoSessionRepository
    private lateinit var settingsRepository: LocalSettingsRepository

    internal var screenState: MainScreenState = MainScreenState.Restoring
        private set
    private var phoneDraft = ""
    private var codeDraft = ""
    private var latestRestoreMessage: String? = null
    private var currentSession: UserSession? = null
    private var chatListStatusMessage: String? = null
    private var searchDraft: String = ""
    private var globalSearchDraft: String = ""
    private var contactsSearchDraft: String = ""
    private var chatComposerDraft: String = ""
    private var lastChatListState: MainScreenState.ChatList? = null
    private var lastContactsState: MainScreenState.Contacts? = null
    private var lastSettingsEntryState: MainScreenState? = null
    private var demoAuthEnabled = false

    private val chatListRepository get() = chatFixtureBundle.chatListRepository
    private val chatDetailRepository get() = chatFixtureBundle.chatDetailRepository
    private val contactsRepository get() = chatFixtureBundle.contactsRepository
    private val searchRepository get() = chatFixtureBundle.searchRepository
    private val syncRepository get() = chatFixtureBundle.syncRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        demoAuthEnabled = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (demoAuthEnabled) {
            phoneDraft = "+86 138 0000 0000"
            codeDraft = DemoSessionRepository.DEMO_VERIFICATION_CODE
        }

        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        sessionRepository = DemoSessionRepository(
            storage = PreferencesSessionStorage(
                sharedPreferences = sharedPreferences,
            ),
            demoAuthEnabled = demoAuthEnabled,
        )
        chatFixtureBundle = InMemoryChatFixtureBundle(
            snapshotStorage = PreferencesSyncSnapshotStorage(
                sharedPreferences = sharedPreferences,
            ),
        )
        chatDebugController = chatFixtureBundle.debugController
        settingsRepository = LocalSettingsRepository(
            storage = PreferencesUserSettingsStorage(
                sharedPreferences = sharedPreferences,
            ),
        )

        render(MainScreenState.Restoring)
        startRestoreFlow()
    }

    override fun onDestroy() {
        cancelPendingWork()
        super.onDestroy()
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        when (screenState) {
            is MainScreenState.ChatDetail -> {
                if ((screenState as MainScreenState.ChatDetail).mediaPickerState != MediaPickerState.Closed) {
                    closeMediaPicker()
                    return
                }
                returnToChatList()
                return
            }
            is MainScreenState.Search -> {
                returnFromSearch()
                return
            }
            is MainScreenState.Contacts -> {
                openChatsRoot()
                return
            }
            is MainScreenState.Settings -> {
                returnFromSettings()
                return
            }
            else -> super.onBackPressed()
        }
    }

    internal fun loginState(
        restoreMessage: String? = latestRestoreMessage,
        formMessage: String? = null,
        isSubmitting: Boolean = false,
    ): MainScreenState.Login {
        return MainScreenState.Login(
            restoreMessage = restoreMessage,
            formMessage = formMessage,
            isSubmitting = isSubmitting,
            phoneDraft = phoneDraft,
            codeDraft = codeDraft,
            demoAuthEnabled = demoAuthEnabled,
        )
    }

    internal fun chatListState(
        session: UserSession,
        statusMessage: String?,
        contentState: ChatListContentState,
        isRefreshing: Boolean = false,
    ): MainScreenState.ChatList {
        return MainScreenState.ChatList(
            session = session,
            statusMessage = statusMessage,
            contentState = contentState,
            searchDraft = searchDraft,
            isRefreshing = isRefreshing,
            debugScenario = chatDebugController.currentChatListScenario(),
        )
    }

    internal fun contactsState(
        session: UserSession,
        statusMessage: String?,
        contentState: ContactsContentState,
    ): MainScreenState.Contacts {
        return MainScreenState.Contacts(
            session = session,
            searchDraft = contactsSearchDraft,
            statusMessage = statusMessage,
            contentState = contentState,
            debugScenario = chatDebugController.currentContactListScenario(),
        )
    }

    internal fun updatePhoneDraft(value: String) {
        phoneDraft = value
    }

    internal fun updateCodeDraft(value: String) {
        codeDraft = value
    }

    internal fun updateSearchDraft(value: String) {
        searchDraft = value
    }

    internal fun updateGlobalSearchDraft(value: String) {
        globalSearchDraft = value
    }

    internal fun updateContactsSearchDraft(value: String) {
        contactsSearchDraft = value
    }

    internal fun updateChatComposerDraft(value: String) {
        chatComposerDraft = value
    }

    internal fun startRestoreFlow() {
        cancelPendingWork()
        render(MainScreenState.Restoring)

        postPendingWork(RESTORE_DELAY_MS) {
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
                        render(loginState())
                    }
                    is SessionRestoreResult.Failed -> {
                        latestRestoreMessage = result.message
                        currentSession = null
                        chatListStatusMessage = null
                        render(loginState(restoreMessage = result.message))
                    }
                }
        }
    }

    internal fun restoreCachedContextOrLoadDefault() {
        when (val snapshotResult = RestoreSyncSnapshotUseCase(syncRepository).execute()) {
            is SyncSnapshotRestoreResult.Restored -> {
                val snapshot = snapshotResult.snapshot
                searchDraft = snapshot.searchKeyword
                chatComposerDraft = ""
                when (snapshot.route) {
                    SyncSnapshotRoute.CHAT_LIST -> {
                        chatListStatusMessage = "已从本地缓存恢复最近上下文，可能不是最新内容。"
                        renderChatListResult(
                            LoadChatListUseCase(chatListRepository).execute(searchDraft),
                        )
                    }
                    SyncSnapshotRoute.CHAT_DETAIL -> {
                        val selectedChatId = snapshot.selectedChatId
                        if (selectedChatId == null) {
                            chatListStatusMessage = "本地缓存不可用，正在加载会话列表。"
                            loadChatList(showLoading = true, isRefresh = false)
                            return
                        }
                        when (val detailResult = LoadChatDetailUseCase(chatDetailRepository).execute(selectedChatId)) {
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

    internal fun submitLogin() {
        val restoreMessage = latestRestoreMessage
        render(
            loginState(
                restoreMessage = restoreMessage,
                isSubmitting = true,
            ),
        )

        postPendingWork(LOGIN_DELAY_MS) {
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
                        ClearSyncSnapshotUseCase(syncRepository).execute()
                        chatListStatusMessage = "登录成功，正在加载会话列表。"
                        loadChatList(showLoading = true, isRefresh = false)
                    }
                    is LoginResult.InvalidInput -> {
                        render(
                            loginState(
                                restoreMessage = restoreMessage,
                                formMessage = result.message,
                            ),
                        )
                    }
                    is LoginResult.Failed -> {
                        render(
                            loginState(
                                restoreMessage = restoreMessage,
                                formMessage = result.message,
                            ),
                        )
                    }
                }
        }
    }

    internal fun loadChatList(
        showLoading: Boolean,
        isRefresh: Boolean,
    ) {
        val session = currentSession ?: return
        val currentState = screenState as? MainScreenState.ChatList

        if (showLoading) {
            render(
                chatListState(
                    session = session,
                    statusMessage = chatListStatusMessage,
                    contentState = ChatListContentState.Loading,
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

        postPendingWork(if (isRefresh) REFRESH_DELAY_MS else CHAT_LIST_DELAY_MS) {
                val result = if (isRefresh) {
                    RefreshChatListUseCase(chatListRepository).execute(searchDraft)
                } else {
                    LoadChatListUseCase(chatListRepository).execute(searchDraft)
                }

                if (isRefresh) {
                    chatListStatusMessage = when (result) {
                        is ChatListLoadResult.Success -> "会话列表已刷新。"
                        ChatListLoadResult.Empty -> "当前没有可显示的会话。"
                        is ChatListLoadResult.Failed -> result.message
                    }
                }

                renderChatListResult(result)
        }
    }

    internal fun renderChatListResult(result: ChatListLoadResult) {
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
            chatListState(
                session = session,
                statusMessage = chatListStatusMessage,
                contentState = contentState,
            ),
        )

        if (result is ChatListLoadResult.Success) {
            persistChatListSnapshot()
        }
    }

    internal fun submitSearch() {
        val session = currentSession ?: return
        chatListStatusMessage = if (searchDraft.isBlank()) {
            "已恢复默认列表。"
        } else {
            "正在搜索 \"$searchDraft\"..."
        }
        render(
            chatListState(
                session = session,
                statusMessage = chatListStatusMessage,
                contentState = ChatListContentState.Loading,
            ),
        )
        loadChatList(showLoading = false, isRefresh = false)
    }

    internal fun clearSearch() {
        searchDraft = ""
        chatListStatusMessage = "已清除搜索条件。"
        loadChatList(showLoading = true, isRefresh = false)
    }

    internal fun openGlobalSearch(query: String = searchDraft) {
        val session = currentSession ?: return
        val currentState = screenState as? MainScreenState.ChatList
        if (currentState != null) {
            lastChatListState = currentState.copy(searchDraft = searchDraft)
        }

        globalSearchDraft = query.trim()
        val initialState = if (globalSearchDraft.isBlank()) {
            MainScreenState.Search(
                session = session,
                queryDraft = globalSearchDraft,
                statusMessage = "请输入关键词后再搜索全部消息。",
                contentState = SearchContentState.Idle,
            )
        } else {
            MainScreenState.Search(
                session = session,
                queryDraft = globalSearchDraft,
                statusMessage = "正在搜索 \"$globalSearchDraft\"...",
                contentState = SearchContentState.Loading,
            )
        }
        render(initialState)

        if (globalSearchDraft.isNotBlank()) {
            loadGlobalSearch(showLoading = false)
        }
    }

    internal fun submitGlobalSearch() {
        val session = currentSession ?: return
        val normalizedQuery = globalSearchDraft.trim()
        if (normalizedQuery.isBlank()) {
            render(
                MainScreenState.Search(
                    session = session,
                    queryDraft = "",
                    statusMessage = "请输入关键词开始全局搜索。",
                    contentState = SearchContentState.Idle,
                ),
            )
            return
        }

        globalSearchDraft = normalizedQuery
        render(
            MainScreenState.Search(
                session = session,
                queryDraft = globalSearchDraft,
                statusMessage = "正在搜索 \"$globalSearchDraft\"...",
                contentState = SearchContentState.Loading,
            ),
        )
        loadGlobalSearch(showLoading = false)
    }

    internal fun clearGlobalSearch() {
        val session = currentSession ?: return
        globalSearchDraft = ""
        render(
            MainScreenState.Search(
                session = session,
                queryDraft = "",
                statusMessage = "已清除搜索关键词。",
                contentState = SearchContentState.Idle,
            ),
        )
    }

    internal fun loadGlobalSearch(showLoading: Boolean) {
        val session = currentSession ?: return
        val currentState = screenState as? MainScreenState.Search

        if (showLoading) {
            render(
                MainScreenState.Search(
                    session = session,
                    queryDraft = globalSearchDraft,
                    statusMessage = "正在搜索 \"$globalSearchDraft\"...",
                    contentState = SearchContentState.Loading,
                ),
            )
        } else if (currentState != null) {
            render(
                currentState.copy(
                    queryDraft = globalSearchDraft,
                    statusMessage = "正在搜索 \"$globalSearchDraft\"...",
                    contentState = SearchContentState.Loading,
                ),
            )
        }

        postPendingWork(CHAT_LIST_DELAY_MS) {
                val result = SearchChatsAndMessagesUseCase(searchRepository).execute(globalSearchDraft)
                renderSearchResult(result)
        }
    }

    internal fun renderSearchResult(result: SearchLoadResult) {
        val session = currentSession ?: return
        val contentState = when (result) {
            is SearchLoadResult.Success -> SearchContentState.Ready(
                chatResults = result.chatResults,
                messageResults = result.messageResults,
            )
            SearchLoadResult.Empty -> SearchContentState.Empty(
                title = "未找到匹配的结果",
                body = "试试更短的关键词，或改搜会话名与消息里的核心词。",
            )
            is SearchLoadResult.Failed -> SearchContentState.Error(result.message)
        }
        val statusMessage = when (result) {
            is SearchLoadResult.Success -> {
                val chatCount = result.chatResults.size
                val messageCount = result.messageResults.size
                "命中 $chatCount 个会话，$messageCount 条消息。"
            }
            SearchLoadResult.Empty -> "没有找到与 \"$globalSearchDraft\" 相关的结果。"
            is SearchLoadResult.Failed -> result.message
        }

        render(
            MainScreenState.Search(
                session = session,
                queryDraft = globalSearchDraft,
                statusMessage = statusMessage,
                contentState = contentState,
            ),
        )
    }

    internal fun returnFromSearch() {
        val fallbackState = lastChatListState
        if (fallbackState != null) {
            searchDraft = fallbackState.searchDraft
            chatListStatusMessage = fallbackState.statusMessage
            render(fallbackState)
            return
        }
        globalSearchDraft = ""
        loadChatList(showLoading = true, isRefresh = false)
    }

    internal fun openContactsRoot(
        showLoading: Boolean = true,
        statusMessageOverride: String? = null,
    ) {
        val session = currentSession ?: return

        if (showLoading) {
            render(
                contactsState(
                    session = session,
                    statusMessage = statusMessageOverride ?: "正在加载联系人...",
                    contentState = ContactsContentState.Loading,
                ),
            )
        }

        postPendingWork(CONTACTS_DELAY_MS) {
            val result = LoadContactsUseCase(contactsRepository).execute(contactsSearchDraft)
            renderContactsResult(
                result = result,
                statusMessage = statusMessageOverride,
            )
        }
    }

    internal fun renderContactsResult(
        result: ContactsLoadResult,
        statusMessage: String? = null,
    ) {
        val session = currentSession ?: return
        val contentState = when (result) {
            is ContactsLoadResult.Success -> ContactsContentState.Ready(result.contacts)
            ContactsLoadResult.Empty -> {
                if (contactsSearchDraft.isBlank()) {
                    ContactsContentState.Empty(
                        title = "暂无联系人",
                        body = "当前 demo 通讯录为空。你可以恢复默认数据或稍后再试。",
                    )
                } else {
                    ContactsContentState.Empty(
                        title = "未找到匹配的联系人",
                        body = "试试姓名拼音、昵称或手机号片段。",
                    )
                }
            }
            is ContactsLoadResult.Failed -> ContactsContentState.Error(result.message)
        }

        val resolvedStatusMessage = statusMessage ?: when (result) {
            is ContactsLoadResult.Success -> {
                if (contactsSearchDraft.isBlank()) {
                    "联系人已加载。"
                } else {
                    "找到 ${result.contacts.size} 位联系人。"
                }
            }
            ContactsLoadResult.Empty -> {
                if (contactsSearchDraft.isBlank()) {
                    "当前没有可显示的联系人。"
                } else {
                    "没有找到与 \"$contactsSearchDraft\" 相关的联系人。"
                }
            }
            is ContactsLoadResult.Failed -> result.message
        }

        render(
            contactsState(
                session = session,
                statusMessage = resolvedStatusMessage,
                contentState = contentState,
            ),
        )
    }

    internal fun submitContactsSearch() {
        val session = currentSession ?: return
        contactsSearchDraft = contactsSearchDraft.trim()
        render(
            contactsState(
                session = session,
                statusMessage = if (contactsSearchDraft.isBlank()) {
                    "已恢复默认联系人列表。"
                } else {
                    "正在搜索 \"$contactsSearchDraft\"..."
                },
                contentState = ContactsContentState.Loading,
            ),
        )
        openContactsRoot(showLoading = false)
    }

    internal fun clearContactsSearch() {
        contactsSearchDraft = ""
        openContactsRoot(
            showLoading = true,
            statusMessageOverride = "已清除联系人搜索条件。",
        )
    }

    internal fun openContact(contact: ContactSummary) {
        val currentState = screenState as? MainScreenState.Contacts ?: return
        render(
            currentState.copy(
                statusMessage = "正在打开 ${contact.displayName}...",
            ),
        )

        postPendingWork(CONTACT_OPEN_DELAY_MS) {
            when (val result = OpenContactChatUseCase(contactsRepository).execute(contact.id)) {
                is OpenContactChatResult.Success -> {
                    val detailStatusMessage = if (result.isNewChat) {
                        "已开始与 ${contact.displayName} 的新对话。"
                    } else {
                        "已从联系人进入 ${contact.displayName}。"
                    }
                    chatListStatusMessage = detailStatusMessage
                    openChat(
                        chat = result.chat,
                        returnToContacts = currentState.toReturnState(),
                        successStatusMessage = detailStatusMessage,
                    )
                }
                is OpenContactChatResult.Failed -> {
                    render(
                        currentState.copy(
                            statusMessage = result.message,
                        ),
                    )
                }
            }
        }
    }

    internal fun switchContactScenario(next: ContactListScenario) {
        if (next == ContactListScenario.DEFAULT) {
            chatDebugController.restoreDefaultContactScenario()
        } else {
            chatDebugController.setContactListScenario(next)
        }
        contactsSearchDraft = ""
        openContactsRoot(
            showLoading = true,
            statusMessageOverride = when (next) {
                ContactListScenario.DEFAULT -> "已恢复联系人列表。"
                ContactListScenario.EMPTY -> "已切换到联系人空态 fixture。"
                ContactListScenario.ERROR -> "已切换到联系人错误态 fixture。"
            },
        )
    }

    internal fun openSettings(showLoading: Boolean = true) {
        val session = currentSession ?: return
        val currentState = screenState
        if (
            currentState is MainScreenState.ChatList ||
            currentState is MainScreenState.Search ||
            currentState is MainScreenState.Contacts
        ) {
            lastSettingsEntryState = currentState
        }

        if (showLoading) {
            render(
                MainScreenState.Settings(
                    session = session,
                    statusMessage = "正在加载设置...",
                    contentState = SettingsContentState.Loading,
                ),
            )
        }

        postPendingWork(SETTINGS_DELAY_MS) {
                val result = LoadSettingsUseCase(settingsRepository).execute(session)
                renderSettingsResult(result)
        }
    }

    internal fun renderSettingsResult(
        result: SettingsLoadResult,
        statusMessage: String? = null,
    ) {
        val session = currentSession ?: return
        val contentState = when (result) {
            is SettingsLoadResult.Success -> SettingsContentState.Ready(result.snapshot)
            is SettingsLoadResult.Failed -> SettingsContentState.Error(result.message)
        }
        render(
            MainScreenState.Settings(
                session = session,
                statusMessage = statusMessage ?: when (result) {
                    is SettingsLoadResult.Success -> "设置已加载。"
                    is SettingsLoadResult.Failed -> result.message
                },
                contentState = contentState,
            ),
        )
    }

    internal fun togglePreference(key: PreferenceKey) {
        val currentState = screenState as? MainScreenState.Settings ?: return
        val snapshot = (currentState.contentState as? SettingsContentState.Ready)?.snapshot ?: return
        val currentPreference = snapshot.preferences.firstOrNull { it.key == key } ?: return

        val result = TogglePreferenceUseCase(settingsRepository).execute(
            session = currentState.session,
            key = key,
            currentValue = currentPreference.isEnabled,
        )
        when (result) {
            is UpdatePreferenceResult.Success -> {
                render(
                    currentState.copy(
                        statusMessage = "${result.updatedPreference.title} 已${if (result.updatedPreference.isEnabled) "开启" else "关闭"}。",
                        contentState = SettingsContentState.Ready(result.snapshot),
                    ),
                )
            }
            is UpdatePreferenceResult.Failed -> {
                render(
                    currentState.copy(
                        statusMessage = result.message,
                        contentState = result.snapshot?.let(SettingsContentState::Ready)
                            ?: currentState.contentState,
                    ),
                )
            }
        }
    }

    internal fun returnFromSettings() {
        when (val fallback = lastSettingsEntryState) {
            is MainScreenState.Search -> render(fallback)
            is MainScreenState.Contacts -> render(
                fallback.copy(
                    statusMessage = "已返回联系人。",
                ),
            )
            is MainScreenState.ChatList -> render(
                fallback.copy(
                    statusMessage = "已返回聊天主壳。",
                ),
            )
            else -> {
                chatListStatusMessage = "已返回会话列表。"
                loadChatList(showLoading = true, isRefresh = false)
            }
        }
    }

    internal fun openMediaPicker() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(
            currentState.copy(
                statusMessage = "正在加载图片素材...",
                mediaPickerState = MediaPickerState.Loading,
            ),
        )

        postPendingWork(MEDIA_PICKER_DELAY_MS) {
                val result = LoadAvailableMediaUseCase(chatDetailRepository).execute()
                renderMediaPickerResult(result)
        }
    }

    internal fun renderMediaPickerResult(result: MediaPickerLoadResult) {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        when (result) {
            is MediaPickerLoadResult.Success -> {
                render(
                    currentState.copy(
                        statusMessage = "请选择一张图片发送。",
                        mediaPickerState = MediaPickerState.Ready(result.attachments),
                    ),
                )
            }
            is MediaPickerLoadResult.Failed -> {
                render(
                    currentState.copy(
                        statusMessage = result.message,
                        mediaPickerState = MediaPickerState.Error(result.message),
                    ),
                )
            }
        }
    }

    internal fun closeMediaPicker() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(
            currentState.copy(
                statusMessage = "已关闭图片选择器。",
                mediaPickerState = MediaPickerState.Closed,
            ),
        )
    }

    internal fun sendMedia(mediaId: String) {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(
            currentState.copy(
                statusMessage = "正在发送图片...",
                mediaPickerState = MediaPickerState.Closed,
            ),
        )

        postPendingWork(MESSAGE_SEND_DELAY_MS) {
                when (
                    val result = SendChatMediaUseCase(chatDetailRepository).execute(
                        chatId = currentState.chatId,
                        mediaId = mediaId,
                    )
                ) {
                    is SendMediaResult.Success -> {
                        chatListStatusMessage = "会话 ${result.thread.chat.title} 已发送新图片。"
                        renderChatDetailResult(
                            chatId = currentState.chatId,
                            chatTitle = currentState.chatTitle,
                            result = ChatDetailLoadResult.Success(result.thread),
                            statusMessage = "图片已发送。",
                            highlightedMessageId = currentState.highlightedMessageId,
                            returnToSearch = currentState.returnToSearch,
                            returnToContacts = currentState.returnToContacts,
                        )
                    }
                    is SendMediaResult.Failed -> {
                        render(
                            currentState.copy(
                                statusMessage = result.message,
                                contentState = result.thread?.let(ChatDetailContentState::Ready)
                                    ?: currentState.contentState,
                                mediaPickerState = MediaPickerState.Closed,
                            ),
                        )
                        if (result.thread != null) {
                            persistChatDetailSnapshot(chatId = currentState.chatId)
                        }
                        }
                    }
        }
    }

    internal fun openChatFromSearchResult(chat: ChatSummary) {
        val searchState = screenState as? MainScreenState.Search ?: return
        openChat(
            chat = chat,
            returnToSearch = searchState.toReturnState(),
        )
    }

    internal fun openMessageSearchResult(hit: MessageSearchHit) {
        val searchState = screenState as? MainScreenState.Search ?: return
        openChat(
            chat = hit.chat,
            highlightedMessageId = hit.message.id,
            returnToSearch = searchState.toReturnState(),
        )
    }

    internal fun refreshChats() {
        loadChatList(showLoading = false, isRefresh = true)
    }

    internal fun openChat(
        chat: ChatSummary,
        highlightedMessageId: String? = null,
        returnToSearch: SearchReturnState? = null,
        returnToContacts: ContactsReturnState? = null,
        successStatusMessage: String? = null,
    ) {
        val session = currentSession ?: return
        chatComposerDraft = ""
        render(
            MainScreenState.ChatDetail(
                session = session,
                chatId = chat.id,
                chatTitle = chat.title,
                chatSubtitle = chat.statusLabel,
                statusMessage = if (highlightedMessageId == null) {
                    "正在打开 ${chat.title}..."
                } else {
                    "正在定位 ${chat.title} 中的命中消息..."
                },
                contentState = ChatDetailContentState.Loading,
                composerDraft = chatComposerDraft,
                nextSendWillFail = chatDebugController.nextSendWillFail(),
                highlightedMessageId = highlightedMessageId,
                returnToSearch = returnToSearch,
                returnToContacts = returnToContacts,
            ),
        )

        postPendingWork(CHAT_DETAIL_DELAY_MS) {
                val result = LoadChatDetailUseCase(chatDetailRepository).execute(chat.id)
                renderChatDetailResult(
                    chatId = chat.id,
                    chatTitle = chat.title,
                    result = result,
                    statusMessage = when (result) {
                        is ChatDetailLoadResult.Success -> when {
                            highlightedMessageId != null -> {
                                "已定位到包含 \"${returnToSearch?.queryDraft.orEmpty()}\" 的消息。"
                            }
                            returnToSearch != null -> "已从搜索结果打开 ${result.thread.chat.title}。"
                            returnToContacts != null -> {
                                successStatusMessage ?: "已从联系人进入 ${result.thread.chat.title}。"
                            }
                            else -> "已进入 ${result.thread.chat.title}。"
                        }
                        is ChatDetailLoadResult.Failed -> result.message
                    },
                    highlightedMessageId = highlightedMessageId,
                    returnToSearch = returnToSearch,
                    returnToContacts = returnToContacts,
                )
        }
    }

    internal fun renderChatDetailResult(
        chatId: String,
        chatTitle: String,
        result: ChatDetailLoadResult,
        statusMessage: String?,
        pendingOutgoingText: String? = null,
        retryingMessageId: String? = null,
        highlightedMessageId: String? = null,
        returnToSearch: SearchReturnState? = null,
        returnToContacts: ContactsReturnState? = null,
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
                nextSendWillFail = chatDebugController.nextSendWillFail(),
                highlightedMessageId = highlightedMessageId,
                returnToSearch = returnToSearch,
                returnToContacts = returnToContacts,
            ),
        )

        if (result is ChatDetailLoadResult.Success) {
            persistChatDetailSnapshot(chatId = chatId)
        }
    }

    internal fun persistChatListSnapshot() {
        SaveSyncSnapshotUseCase(syncRepository).execute(
            route = SyncSnapshotRoute.CHAT_LIST,
            searchKeyword = searchDraft,
        )
    }

    internal fun persistChatDetailSnapshot(chatId: String) {
        SaveSyncSnapshotUseCase(syncRepository).execute(
            route = SyncSnapshotRoute.CHAT_DETAIL,
            searchKeyword = searchDraft,
            selectedChatId = chatId,
        )
    }

    internal fun submitMessage() {
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

        postPendingWork(MESSAGE_SEND_DELAY_MS) {
                when (
                    val result = SendChatMessageUseCase(chatDetailRepository).execute(
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
                            highlightedMessageId = currentState.highlightedMessageId,
                            returnToSearch = currentState.returnToSearch,
                            returnToContacts = currentState.returnToContacts,
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
                                nextSendWillFail = chatDebugController.nextSendWillFail(),
                            ),
                        )
                        if (result.thread != null) {
                            persistChatDetailSnapshot(chatId = currentState.chatId)
                        }
                        }
                    }
        }
    }

    internal fun retryFailedMessage(messageId: String) {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(
            currentState.copy(
                statusMessage = "正在重试消息...",
                composerDraft = chatComposerDraft,
                retryingMessageId = messageId,
            ),
        )

        postPendingWork(MESSAGE_RETRY_DELAY_MS) {
                when (
                    val result = RetryChatMessageUseCase(chatDetailRepository).execute(
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
                            highlightedMessageId = currentState.highlightedMessageId,
                            returnToSearch = currentState.returnToSearch,
                            returnToContacts = currentState.returnToContacts,
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
                                nextSendWillFail = chatDebugController.nextSendWillFail(),
                            ),
                        )
                        if (result.thread != null) {
                            persistChatDetailSnapshot(chatId = currentState.chatId)
                        }
                        }
                    }
        }
    }

    internal fun toggleNextSendFailure() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        val nextState = !chatDebugController.nextSendWillFail()
        chatDebugController.setNextSendShouldFail(nextState)
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

    internal fun returnToChatList() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        chatComposerDraft = ""
        currentState.returnToSearch?.let { returnState ->
            globalSearchDraft = returnState.queryDraft
            render(
                MainScreenState.Search(
                    session = currentState.session,
                    queryDraft = returnState.queryDraft,
                    statusMessage = "已返回搜索结果。",
                    contentState = returnState.contentState,
                ),
            )
            return
        }
        currentState.returnToContacts?.let { returnState ->
            contactsSearchDraft = returnState.searchDraft
            renderContactsResult(
                result = LoadContactsUseCase(contactsRepository).execute(contactsSearchDraft),
                statusMessage = "已返回联系人。",
            )
            return
        }

        lastChatListState?.let { listState ->
            searchDraft = listState.searchDraft
            chatListStatusMessage = "已返回会话列表。"
            render(
                listState.copy(
                    statusMessage = chatListStatusMessage,
                ),
            )
            return
        }

        chatListStatusMessage = chatListStatusMessage ?: "已返回会话列表。"
        loadChatList(showLoading = true, isRefresh = false)
    }

    internal fun openComposePlaceholder() {
        contactsSearchDraft = ""
        openContactsRoot(
            showLoading = true,
            statusMessageOverride = "选择联系人开始新的对话。",
        )
    }

    internal fun openAddContactPlaceholder() {
        val currentState = screenState as? MainScreenState.Contacts ?: return
        render(
            currentState.copy(
                statusMessage = "添加联系人不在本轮范围。",
            ),
        )
    }

    internal fun openEditPlaceholder() {
        val currentState = screenState as? MainScreenState.ChatList ?: return
        chatListStatusMessage = "编辑入口已预留，本轮不实现批量编辑。"
        render(currentState.copy(statusMessage = chatListStatusMessage))
    }

    internal fun switchScenario(next: ChatListScenario) {
        if (next == ChatListScenario.DEFAULT) {
            chatDebugController.restoreDefaultFixtures()
        } else {
            chatDebugController.setChatListScenario(next)
        }
        searchDraft = ""
        globalSearchDraft = ""
        chatListStatusMessage = when (next) {
            ChatListScenario.DEFAULT -> "已恢复默认 fixture 数据。"
            ChatListScenario.EMPTY -> "已切换到空态 fixture。"
            ChatListScenario.ERROR -> "已切换到错误态 fixture。"
        }
        loadChatList(showLoading = true, isRefresh = false)
    }

    internal fun clearLocalSnapshot() {
        ClearSyncSnapshotUseCase(syncRepository).execute()
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

    internal fun logout() {
        cancelPendingWork()
        ClearSyncSnapshotUseCase(syncRepository).execute()
        LogoutUseCase(sessionRepository).execute()
        latestRestoreMessage = null
        currentSession = null
        searchDraft = ""
        globalSearchDraft = ""
        contactsSearchDraft = ""
        chatComposerDraft = ""
        chatListStatusMessage = null
        lastChatListState = null
        lastContactsState = null
        lastSettingsEntryState = null
        chatDebugController.restoreDefaultFixtures()
        render(loginState(formMessage = "已退出登录。"))
    }

    internal fun openChatsRoot() {
        chatListStatusMessage = "已返回会话列表。"
        loadChatList(showLoading = true, isRefresh = false)
    }

    internal fun openChatDetailOverflowPlaceholder() {
        val currentState = screenState as? MainScreenState.ChatDetail ?: return
        render(currentState.copy(statusMessage = "更多会话操作仍在 backlog。"))
    }

    internal fun seedExpiredSession() {
        sessionRepository.seedExpiredSession()
        val currentState = screenState as? MainScreenState.ChatList ?: return
        chatListStatusMessage = "已写入失效会话。请重新启动应用验证恢复失败路径。"
        render(currentState.copy(statusMessage = chatListStatusMessage))
    }

    internal fun render(state: MainScreenState) {
        screenState = state
        when (state) {
            is MainScreenState.ChatList -> {
                lastChatListState = state
                searchDraft = state.searchDraft
            }
            is MainScreenState.Search -> {
                globalSearchDraft = state.queryDraft
            }
            is MainScreenState.Contacts -> {
                lastContactsState = state
                contactsSearchDraft = state.searchDraft
            }
            else -> Unit
        }
        val content = when (state) {
            MainScreenState.Restoring -> buildRestoringScreen()
            is MainScreenState.Login -> buildLoginScreen(state)
            is MainScreenState.ChatList -> buildChatListScreen(state)
            is MainScreenState.Search -> buildSearchScreen(state)
            is MainScreenState.Contacts -> buildContactsScreen(state)
            is MainScreenState.Settings -> buildSettingsScreen(state)
            is MainScreenState.ChatDetail -> buildChatDetailScreen(state)
        }
        setContentView(content)
    }

    internal fun wrapInScroll(
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

    internal fun cancelPendingWork() {
        pendingWork?.let(handler::removeCallbacks)
        pendingWork = null
    }

    internal fun postPendingWork(
        delayMs: Long,
        block: () -> Unit,
    ) {
        cancelPendingWork()
        lateinit var scheduled: Runnable
        scheduled = Runnable {
            if (pendingWork === scheduled) {
                pendingWork = null
            }
            block()
        }
        pendingWork = scheduled
        handler.postDelayed(scheduled, delayMs)
    }

    internal fun wrapInSwipeRefresh(
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

    internal fun baseColumn(
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

    internal fun screenRoot(
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

    internal fun weightedSpacer(weight: Float): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                weight,
            )
        }
    }

    internal fun titleView(
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

    internal fun bodyView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 17f
            gravity = Gravity.CENTER_HORIZONTAL
            setTextColor(Color.parseColor("#1B1F23"))
        }
    }

    internal fun secondaryView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(Color.parseColor("#667781"))
        }
    }

    internal fun labelView(text: String): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTypeface(Typeface.DEFAULT_BOLD)
            setTextColor(Color.parseColor("#41505A"))
        }
    }

    internal fun inputField(
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

    internal fun primaryButton(
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

    internal fun secondaryButton(
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

    internal fun errorBanner(text: String): TextView {
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

    internal fun infoBanner(text: String): TextView {
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

    internal fun thinDivider(): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1),
            )
            setBackgroundColor(Color.parseColor("#EDF1F5"))
        }
    }

    internal fun skeletonBar(
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

    internal fun roundedBackground(
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

    internal fun space(heightDp: Int): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightDp),
            )
        }
    }

    internal fun spaceWidth(widthDp: Int): View {
        return View(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                dp(widthDp),
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
    }

    internal fun messagePanelContainer(): LinearLayout {
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

    internal fun maxBubbleWidthPx(): Int = (resources.displayMetrics.widthPixels * 0.66f).roundToInt()

    internal fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    internal fun MainScreenState.Search.toReturnState(): SearchReturnState {
        return SearchReturnState(
            queryDraft = queryDraft,
            statusMessage = statusMessage,
            contentState = contentState,
        )
    }

    internal fun MainScreenState.Contacts.toReturnState(): ContactsReturnState {
        return ContactsReturnState(
            searchDraft = searchDraft,
            statusMessage = statusMessage,
            contentState = contentState,
        )
    }

    internal enum class RootTab {
        CHATS,
        CONTACTS,
        SETTINGS,
    }

    private companion object {
        const val PREFS_NAME = "telegram_compare_session"
        const val RESTORE_DELAY_MS = 650L
        const val LOGIN_DELAY_MS = 450L
        const val CHAT_LIST_DELAY_MS = 320L
        const val CONTACTS_DELAY_MS = 260L
        const val CONTACT_OPEN_DELAY_MS = 220L
        const val REFRESH_DELAY_MS = 520L
        const val CHAT_DETAIL_DELAY_MS = 260L
        const val SETTINGS_DELAY_MS = 220L
        const val MEDIA_PICKER_DELAY_MS = 180L
        const val MESSAGE_SEND_DELAY_MS = 520L
        const val MESSAGE_RETRY_DELAY_MS = 420L
    }
}
