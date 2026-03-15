package com.telegram.compare.kmp.android

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.DeliveryState
import com.telegram.compare.kmp.shareddomain.MediaAttachment
import com.telegram.compare.kmp.shareddomain.Message
import com.telegram.compare.kmp.shareddomain.MessageSearchHit
import com.telegram.compare.kmp.shareddomain.SettingsSnapshot
import com.telegram.compare.kmp.shareddomain.UserPreference
import com.telegram.compare.kmp.shareddata.ChatListScenario

internal fun MainActivity.buildRestoringScreen(): View {
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

internal fun MainActivity.buildLoginScreen(state: MainScreenState.Login): View {
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
    container.addView(
        secondaryView(
            if (state.demoAuthEnabled) {
                "使用固定 demo 验证码打通 S1 登录与会话恢复。"
            } else {
                "当前构建已禁用 demo 登录，仅保留壳层结构验证。"
            },
        ),
    )

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
            initialValue = state.phoneDraft,
            hint = "+86 138 0000 0000",
            inputType = InputType.TYPE_CLASS_PHONE,
        ) { updatePhoneDraft(it) },
    )
    container.addView(space(16))
    container.addView(labelView("验证码"))
    container.addView(
        inputField(
            initialValue = state.codeDraft,
            hint = "2046",
            inputType = InputType.TYPE_CLASS_NUMBER,
        ) { updateCodeDraft(it) },
    )
    container.addView(space(24))
    container.addView(
        primaryButton(
            text = if (state.isSubmitting) "登录中..." else "继续",
            enabled = state.demoAuthEnabled && !state.isSubmitting,
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
        secondaryView(
            if (state.demoAuthEnabled) {
                "Demo 环境固定验证码: 2046"
            } else {
                "当前构建未启用 demo 登录。"
            },
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        },
    )

    return root
}

internal fun MainActivity.buildChatListScreen(state: MainScreenState.ChatList): View {
    val root = screenRoot(
        horizontalPaddingDp = 16,
        topPaddingDp = 10,
        bottomPaddingDp = 12,
    )

    root.addView(topBar())
    root.addView(space(8))
    root.addView(searchBar(state))
    if (state.searchDraft.isNotBlank()) {
        root.addView(space(8))
        root.addView(globalSearchEntryRow(state.searchDraft))
    }
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
    root.addView(debugSection(state))
    root.addView(space(8))
    root.addView(bottomNavigation(selectedTab = MainActivity.RootTab.CHATS))

    return root
}

internal fun MainActivity.buildSearchScreen(state: MainScreenState.Search): View {
    val root = screenRoot(
        horizontalPaddingDp = 16,
        topPaddingDp = 10,
        bottomPaddingDp = 12,
    )

    root.addView(searchTopBar())
    root.addView(space(8))
    root.addView(globalSearchBar(state))
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
        SearchContentState.Idle -> {
            listContent.addView(
                emptyStateCard(
                    title = "搜索全部消息",
                    body = "输入关键词后，可同时查看会话命中和消息命中结果。",
                    primaryText = "返回列表",
                    onPrimaryClick = { returnFromSearch() },
                ),
            )
        }
        SearchContentState.Loading -> {
            repeat(4) { index ->
                listContent.addView(chatSkeletonRow())
                if (index < 3) {
                    listContent.addView(thinDivider())
                }
            }
        }
        is SearchContentState.Ready -> {
            if (content.chatResults.isNotEmpty()) {
                listContent.addView(searchSectionLabel("Chats"))
                listContent.addView(space(6))
                content.chatResults.forEachIndexed { index, chat ->
                    listContent.addView(
                        chatListRow(chat = chat) {
                            openChatFromSearchResult(chat)
                        },
                    )
                    if (index < content.chatResults.lastIndex || content.messageResults.isNotEmpty()) {
                        listContent.addView(thinDivider())
                    }
                }
            }

            if (content.messageResults.isNotEmpty()) {
                if (content.chatResults.isNotEmpty()) {
                    listContent.addView(space(10))
                }
                listContent.addView(searchSectionLabel("Messages"))
                listContent.addView(space(6))
                content.messageResults.forEachIndexed { index, hit ->
                    listContent.addView(messageSearchResultRow(hit))
                    if (index < content.messageResults.lastIndex) {
                        listContent.addView(thinDivider())
                    }
                }
            }
        }
        is SearchContentState.Empty -> {
            listContent.addView(
                emptyStateCard(
                    title = content.title,
                    body = content.body,
                    primaryText = "清除关键词",
                    onPrimaryClick = { clearGlobalSearch() },
                ),
            )
        }
        is SearchContentState.Error -> {
            listContent.addView(
                errorStateCard(content.message) {
                    loadGlobalSearch(showLoading = true)
                },
            )
        }
    }

    root.addView(
        wrapInScroll(listContent).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f,
            )
        },
    )
    root.addView(space(8))
    root.addView(bottomNavigation(selectedTab = MainActivity.RootTab.CHATS))

    return root
}

internal fun MainActivity.buildSettingsScreen(state: MainScreenState.Settings): View {
    val root = screenRoot(
        horizontalPaddingDp = 16,
        topPaddingDp = 10,
        bottomPaddingDp = 12,
    )

    root.addView(settingsTopBar())
    state.statusMessage?.let {
        root.addView(space(8))
        root.addView(infoBanner(it))
    }
    root.addView(space(8))
    root.addView(thinDivider())
    root.addView(space(4))

    val content = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(0, dp(8), 0, dp(12))
    }

    when (val settings = state.contentState) {
        SettingsContentState.Loading -> {
            content.addView(settingsSkeletonCard())
            content.addView(space(14))
            repeat(3) { index ->
                content.addView(settingsSkeletonRow())
                if (index < 2) {
                    content.addView(space(8))
                }
            }
        }
        is SettingsContentState.Ready -> {
            content.addView(profileHero(settings.snapshot))
            content.addView(space(14))
            content.addView(settingsSectionLabel("Account"))
            content.addView(space(8))
            content.addView(accountSummaryCard(settings.snapshot))
            content.addView(space(14))
            content.addView(settingsSectionLabel("Preferences"))
            content.addView(space(8))
            settings.snapshot.preferences.forEachIndexed { index, preference ->
                content.addView(preferenceRow(preference))
                if (index < settings.snapshot.preferences.lastIndex) {
                    content.addView(space(8))
                }
            }
            content.addView(space(14))
            content.addView(settingsSectionLabel("Session"))
            content.addView(space(8))
            content.addView(sessionActionCard())
        }
        is SettingsContentState.Error -> {
            content.addView(
                settingsErrorStateCard(settings.message) {
                    openSettings(showLoading = true)
                },
            )
        }
    }

    root.addView(
        wrapInScroll(content).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f,
            )
        },
    )
    root.addView(space(8))
    root.addView(bottomNavigation(selectedTab = MainActivity.RootTab.SETTINGS))

    return root
}

internal fun MainActivity.buildChatDetailScreen(state: MainScreenState.ChatDetail): View {
    val root = screenRoot(
        horizontalPaddingDp = 0,
        topPaddingDp = 6,
        bottomPaddingDp = 8,
        backgroundColor = Color.parseColor("#DCE8F4"),
    )

    root.addView(
        chatDetailTopBar(state).apply {
            setPadding(dp(12), dp(4), dp(12), dp(6))
        },
    )
    state.statusMessage?.let {
        root.addView(
            centeredStatusPill(it).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ).apply {
                    setMargins(dp(12), 0, dp(12), dp(8))
                }
            },
        )
    }

    val viewport = FrameLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1f,
        )
    }

    when (val content = state.contentState) {
        ChatDetailContentState.Loading -> {
            val loadingPanel = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(2), dp(10), dp(2), dp(16))
                repeat(6) { index ->
                    addView(messageSkeletonRow(outgoing = index % 2 == 1))
                    if (index < 5) {
                        addView(space(10))
                    }
                }
            }

            viewport.addView(
                wrapInScroll(
                    content = loadingPanel,
                    backgroundColor = Color.TRANSPARENT,
                ).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setPadding(dp(12), 0, dp(12), 0)
                    clipToPadding = false
                },
            )
            root.addView(viewport)
            root.addView(
                composerSection(state, enabled = false).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        setMargins(dp(12), 0, dp(12), 0)
                    }
                },
            )
        }
        is ChatDetailContentState.Ready -> {
            viewport.addView(
                wrapInScroll(
                    content = messageThreadView(content.thread, state),
                    backgroundColor = Color.TRANSPARENT,
                ).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setPadding(dp(12), 0, dp(12), 0)
                    clipToPadding = false
                },
            )
            if (state.mediaPickerState != MediaPickerState.Closed) {
                viewport.addView(
                    mediaPickerSheet(state.mediaPickerState).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            Gravity.BOTTOM,
                        ).apply {
                            setMargins(dp(12), dp(12), dp(12), dp(8))
                        }
                    },
                )
            }
            root.addView(viewport)
            root.addView(
                composerSection(
                    state = state,
                    enabled = state.pendingOutgoingText == null && state.retryingMessageId == null,
                ).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        setMargins(dp(12), 0, dp(12), 0)
                    }
                },
            )
            if (state.mediaPickerState == MediaPickerState.Closed) {
                root.addView(
                    detailDebugSection(state).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        ).apply {
                            setMargins(dp(12), dp(6), dp(12), 0)
                        }
                    },
                )
            }
        }
        is ChatDetailContentState.Error -> {
            viewport.addView(
                wrapInScroll(
                    content = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(dp(2), dp(20), dp(2), dp(20))
                        addView(detailErrorStateCard(state, content.message))
                    },
                    backgroundColor = Color.TRANSPARENT,
                ).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    setPadding(dp(12), 0, dp(12), 0)
                    clipToPadding = false
                },
            )
            root.addView(viewport)
        }
    }

    return root
}

internal fun MainActivity.topBar(): LinearLayout {
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

internal fun MainActivity.searchTopBar(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        addView(topTextButton("返回") { returnFromSearch() })
        addView(spaceWidth(8))
        addView(
            titleView("Search", sizeSp = 22f).apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            },
        )
        addView(spaceWidth(8))
        addView(
            TextView(context).apply {
                text = "S5"
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

internal fun MainActivity.settingsTopBar(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        addView(topTextButton("返回") { returnFromSettings() })
        addView(spaceWidth(8))
        addView(
            titleView("Settings", sizeSp = 22f).apply {
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            },
        )
        addView(spaceWidth(8))
        addView(
            TextView(context).apply {
                text = "S6"
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

internal fun MainActivity.chatDetailTopBar(state: MainScreenState.ChatDetail): LinearLayout {
    val chat = when (val content = state.contentState) {
        is ChatDetailContentState.Ready -> content.thread.chat
        else -> ChatSummary(
            id = state.chatId,
            title = state.chatTitle,
            lastMessagePreview = "",
            unreadCount = 0,
            lastMessageAtLabel = "",
            avatarLabel = detailAvatarLabel(state.chatTitle),
            statusLabel = state.chatSubtitle,
        )
    }

    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        addView(topIconButton("<") { returnToChatList() })
        addView(spaceWidth(6))
        addView(avatarView(chat, sizeDp = 40, textSizeSp = 13f))
        addView(spaceWidth(10))
        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(
                    TextView(context).apply {
                        text = state.chatTitle
                        textSize = 20f
                        setTypeface(Typeface.DEFAULT_BOLD)
                        setTextColor(Color.parseColor("#1B1F23"))
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                    },
                )
                addView(
                    secondaryView(state.chatSubtitle.ifBlank { "last seen recently" }).apply {
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                    },
                )
            },
        )
        addView(spaceWidth(8))
        addView(topIconButton("...") { openChatDetailOverflowPlaceholder() })
    }
}

internal fun MainActivity.searchBar(state: MainScreenState.ChatList): LinearLayout {
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
                            updateSearchDraft(s?.toString().orEmpty())
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

internal fun MainActivity.globalSearchBar(state: MainScreenState.Search): LinearLayout {
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
                setText(state.queryDraft)
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
                            updateGlobalSearchDraft(s?.toString().orEmpty())
                        }
                    },
                )
            },
        )
        addView(spaceWidth(6))
        addView(
            topTextButton("搜索") {
                submitGlobalSearch()
            },
        )
        if (state.queryDraft.isNotBlank()) {
            addView(spaceWidth(4))
            addView(
                topTextButton("清除") {
                    clearGlobalSearch()
                },
            )
        }
    }
}

internal fun MainActivity.globalSearchEntryRow(query: String): LinearLayout {
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
            secondaryView("已过滤当前列表，也可搜索全部消息。").apply {
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            },
        )
        addView(spaceWidth(10))
        addView(
            compactChipButton("全局搜索", active = false) {
                openGlobalSearch(query)
            },
        )
    }
}

internal fun MainActivity.chatListRow(
    chat: ChatSummary,
    onClick: () -> Unit = { openChat(chat) },
): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.TOP
        minimumHeight = dp(72)
        setPadding(dp(2), dp(10), dp(2), dp(10))
        setOnClickListener {
            onClick()
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

internal fun MainActivity.searchSectionLabel(text: String): TextView {
    return TextView(this).apply {
        this.text = text
        textSize = 13f
        setTypeface(Typeface.DEFAULT_BOLD)
        setTextColor(Color.parseColor("#637280"))
        setPadding(dp(4), dp(6), dp(4), dp(2))
    }
}

internal fun MainActivity.messageSearchResultRow(hit: MessageSearchHit): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.TOP
        minimumHeight = dp(72)
        setPadding(dp(2), dp(10), dp(2), dp(10))
        setOnClickListener {
            openMessageSearchResult(hit)
        }

        addView(avatarView(hit.chat))
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
                                text = hit.chat.title
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
                                text = hit.message.sentAtLabel
                                textSize = 12f
                                setTextColor(Color.parseColor("#7F8A96"))
                            },
                        )
                    },
                )
                addView(space(4))
                addView(
                    TextView(context).apply {
                        text = messagePreviewText(hit.message)
                        textSize = 14f
                        maxLines = 2
                        ellipsize = TextUtils.TruncateAt.END
                        setTextColor(Color.parseColor("#566572"))
                    },
                )
                addView(space(4))
                addView(
                    secondaryView("消息结果 · 点按后定位到该消息")
                )
            },
        )
    }
}

internal fun MainActivity.searchHitPreviewCard(message: Message): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFF7D8"),
            strokeColor = Color.parseColor("#E1C766"),
            radiusDp = 18,
        )
        setPadding(dp(14), dp(12), dp(14), dp(12))

        addView(
            TextView(context).apply {
                text = "命中消息预览"
                textSize = 12f
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor("#8C6A12"))
            },
        )
        addView(space(6))
        addView(
            TextView(context).apply {
                text = messagePreviewText(message)
                textSize = 14f
                setTextColor(Color.parseColor("#3B3320"))
            },
        )
        addView(space(6))
        addView(
            secondaryView("${message.sentAtLabel} · 已从搜索结果定位").apply {
                setTextColor(Color.parseColor("#8A6E29"))
            },
        )
    }
}

internal fun MainActivity.chatSkeletonRow(): LinearLayout {
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

internal fun MainActivity.messageThreadView(
    thread: com.telegram.compare.kmp.shareddomain.ChatThread,
    state: MainScreenState.ChatDetail,
): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(2), dp(10), dp(2), dp(16))

        thread.messages.find { it.id == state.highlightedMessageId }?.let { highlighted ->
            addView(searchHitPreviewCard(highlighted))
            addView(space(12))
        }

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
                    isHighlighted = state.highlightedMessageId == message.id,
                ),
            )
            if (index < thread.messages.lastIndex || state.pendingOutgoingText != null) {
                addView(space(10))
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
                    isHighlighted = false,
                ),
            )
        }
    }
}

internal fun MainActivity.messageRow(
    message: Message,
    visualState: DeliveryState,
    actionLabel: String? = null,
    isHighlighted: Boolean = false,
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
                        isHighlighted && isOutgoing -> Color.parseColor("#E4F7DA")
                        isHighlighted -> Color.parseColor("#FFF4CC")
                        isFailed -> Color.parseColor("#FDECEC")
                        isOutgoing -> Color.parseColor("#DCF8C6")
                        else -> Color.WHITE
                    },
                    strokeColor = when {
                        isHighlighted -> Color.parseColor("#E5CC79")
                        isFailed -> Color.parseColor("#F1D0D0")
                        else -> Color.TRANSPARENT
                    },
                    radiusDp = 20,
                )
                setPadding(dp(12), dp(8), dp(12), dp(8))

                if (isHighlighted) {
                    addView(
                        TextView(context).apply {
                            text = "Search hit"
                            textSize = 11f
                            setTypeface(Typeface.DEFAULT_BOLD)
                            setTextColor(Color.parseColor("#8C6A12"))
                        },
                    )
                    addView(space(4))
                }
                message.mediaAttachment?.let { attachment ->
                    addView(mediaBubbleCard(attachment, message.isOutgoing))
                    if (message.text.isNotBlank()) {
                        addView(space(6))
                    }
                }
                if (message.text.isNotBlank()) {
                    addView(
                        TextView(context).apply {
                            text = message.text
                            textSize = 15f
                            setLineSpacing(dp(1).toFloat(), 1f)
                            maxWidth = maxBubbleWidthPx()
                            setTextColor(Color.parseColor("#1B1F23"))
                        },
                    )
                }
                addView(space(4))
                addView(
                    LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.END or Gravity.CENTER_VERTICAL
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
                    },
                )

                if (isFailed) {
                    addView(space(6))
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

internal fun MainActivity.buildMessageMetaText(
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

internal fun MainActivity.messagePreviewText(message: Message): String {
    val attachment = message.mediaAttachment
    return if (attachment == null) {
        message.text
    } else {
        val caption = message.text.ifBlank { attachment.title }
        "Photo · $caption"
    }
}

internal fun MainActivity.messageSkeletonRow(outgoing: Boolean): LinearLayout {
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

internal fun MainActivity.composerSection(
    state: MainScreenState.ChatDetail,
    enabled: Boolean,
): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        addView(
            circleActionButton(
                text = "+",
                enabled = true,
                filled = state.mediaPickerState != MediaPickerState.Closed,
            ) {
                if (state.mediaPickerState == MediaPickerState.Closed) {
                    openMediaPicker()
                } else {
                    closeMediaPicker()
                }
            },
        )
        addView(spaceWidth(8))

        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                background = roundedBackground(
                    fillColor = Color.WHITE,
                    strokeColor = Color.parseColor("#D7E0E8"),
                    radiusDp = 24,
                )
                setPadding(dp(14), dp(6), dp(6), dp(6))

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
                                    updateChatComposerDraft(s?.toString().orEmpty())
                                }
                            },
                        )
                    },
                )
                addView(spaceWidth(6))
                addView(
                    circleActionButton(
                        text = ">",
                        enabled = enabled,
                        filled = true,
                    ) {
                        submitMessage()
                    },
                )
            },
        )
    }
}

internal fun MainActivity.mediaPickerSheet(state: MediaPickerState): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#DDE5EC"),
            radiusDp = 26,
        )
        setPadding(dp(14), dp(10), dp(14), dp(14))

        addView(
            View(context).apply {
                layoutParams = LinearLayout.LayoutParams(dp(38), dp(4)).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                background = roundedBackground(
                    fillColor = Color.parseColor("#D8E1E8"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 2,
                )
            },
        )
        addView(space(10))

        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(
                    titleView("Send photo", sizeSp = 18f).apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    },
                )
                addView(
                    topIconButton("x") {
                        closeMediaPicker()
                    },
                )
            },
        )
        addView(space(6))
        addView(secondaryView("从 fixture 缩略图里选一张，直接发送到当前会话。"))
        addView(space(12))

        when (state) {
            MediaPickerState.Loading -> {
                repeat(2) { rowIndex ->
                    addView(
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            repeat(2) { columnIndex ->
                                addView(
                                    settingsSkeletonRow().apply {
                                        layoutParams = LinearLayout.LayoutParams(
                                            0,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f,
                                        )
                                    },
                                )
                                if (columnIndex == 0) {
                                    addView(spaceWidth(10))
                                }
                            }
                        },
                    )
                    if (rowIndex == 0) {
                        addView(space(8))
                    }
                }
            }
            is MediaPickerState.Ready -> {
                val rows = state.attachments.chunked(2)
                rows.forEachIndexed { rowIndex, attachments ->
                    addView(
                        LinearLayout(context).apply {
                            orientation = LinearLayout.HORIZONTAL
                            attachments.forEachIndexed { columnIndex, attachment ->
                                addView(
                                    mediaPickerRow(attachment).apply {
                                        layoutParams = LinearLayout.LayoutParams(
                                            0,
                                            ViewGroup.LayoutParams.WRAP_CONTENT,
                                            1f,
                                        )
                                    },
                                )
                                if (columnIndex < attachments.lastIndex) {
                                    addView(spaceWidth(10))
                                }
                            }
                            if (attachments.size == 1) {
                                addView(
                                    View(context).apply {
                                        layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
                                    },
                                )
                            }
                        },
                    )
                    if (rowIndex < rows.lastIndex) {
                        addView(space(8))
                    }
                }
            }
            is MediaPickerState.Error -> {
                addView(secondaryView(state.message))
                addView(space(12))
                addView(
                    primaryButton("重试加载") {
                        openMediaPicker()
                    },
                )
            }
            MediaPickerState.Closed -> Unit
        }
    }
}

internal fun MainActivity.mediaPickerRow(attachment: MediaAttachment): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#F8FAFC"),
            strokeColor = Color.parseColor("#E1E8EF"),
            radiusDp = 20,
        )
        setPadding(dp(10), dp(10), dp(10), dp(10))
        setOnClickListener { sendMedia(attachment.id) }

        addView(
            mediaThumbnailView(
                attachment = attachment,
                heightDp = 112,
                showOverlayTitle = false,
            ).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(112),
                )
            },
        )
        addView(space(8))
        addView(
            TextView(context).apply {
                text = attachment.title
                textSize = 14f
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor("#1F2730"))
            },
        )
        addView(space(2))
        addView(
            secondaryView(attachment.defaultCaption).apply {
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            },
        )
    }
}

internal fun MainActivity.mediaBubbleCard(
    attachment: MediaAttachment,
    outgoing: Boolean,
): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        addView(
            mediaThumbnailView(
                attachment = attachment,
                heightDp = if (outgoing) 152 else 144,
                showOverlayTitle = true,
            ).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(188),
                    dp(if (outgoing) 152 else 144),
                )
            },
        )
    }
}

internal fun MainActivity.detailDebugSection(state: MainScreenState.ChatDetail): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#F5F8FB"),
            strokeColor = Color.parseColor("#E4EBF2"),
            radiusDp = 18,
        )
        setPadding(dp(10), dp(6), dp(10), dp(6))

        addView(
            secondaryView("Dev").apply {
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor("#55636F"))
            },
        )
        addView(spaceWidth(8))
        addView(
            compactChipButton(
                label = if (state.nextSendWillFail) "下条失败" else "下条正常",
                active = state.nextSendWillFail,
            ) {
                toggleNextSendFailure()
            },
        )
        addView(spaceWidth(8))
        addView(
            compactChipButton("清缓存", active = false) {
                clearLocalSnapshot()
            },
        )
    }
}

internal fun MainActivity.settingsSectionLabel(text: String): TextView {
    return TextView(this).apply {
        this.text = text
        textSize = 13f
        setTypeface(Typeface.DEFAULT_BOLD)
        setTextColor(Color.parseColor("#637280"))
        setPadding(dp(4), dp(4), dp(4), dp(2))
    }
}

internal fun MainActivity.profileHero(snapshot: SettingsSnapshot): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 24,
        )
        setPadding(dp(18), dp(18), dp(18), dp(18))

        addView(
            TextView(context).apply {
                text = snapshot.profile.displayName.take(2).uppercase()
                textSize = 20f
                gravity = Gravity.CENTER
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor("#1F5F8B"))
                layoutParams = LinearLayout.LayoutParams(dp(64), dp(64))
                background = roundedBackground(
                    fillColor = Color.parseColor("#EAF3FB"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 32,
                )
            },
        )
        addView(spaceWidth(14))
        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(titleView(snapshot.profile.displayName, sizeSp = 22f))
                addView(space(4))
                addView(secondaryView(snapshot.profile.phoneNumber))
                addView(space(6))
                addView(
                    TextView(context).apply {
                        text = "@${snapshot.profile.username}"
                        textSize = 13f
                        setTextColor(Color.parseColor("#2481CC"))
                        background = roundedBackground(
                            fillColor = Color.parseColor("#EEF7FF"),
                            strokeColor = Color.TRANSPARENT,
                            radiusDp = 14,
                        )
                        setPadding(dp(10), dp(6), dp(10), dp(6))
                    },
                )
                addView(space(8))
                addView(secondaryView(snapshot.profile.about))
            },
        )
    }
}

internal fun MainActivity.accountSummaryCard(snapshot: SettingsSnapshot): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 20,
        )
        setPadding(dp(16), dp(14), dp(16), dp(14))

        addView(secondaryView("Display name").apply { setTypeface(Typeface.DEFAULT_BOLD) })
        addView(space(4))
        addView(bodyView(snapshot.profile.displayName).apply { gravity = Gravity.START })
        addView(space(10))
        addView(secondaryView("Phone"))
        addView(space(2))
        addView(bodyView(snapshot.profile.phoneNumber).apply { gravity = Gravity.START })
        addView(space(10))
        addView(secondaryView("About"))
        addView(space(2))
        addView(secondaryView(snapshot.profile.about))
    }
}

internal fun MainActivity.preferenceRow(preference: UserPreference): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 20,
        )
        setPadding(dp(16), dp(14), dp(16), dp(14))
        setOnClickListener { togglePreference(preference.key) }

        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(
                    TextView(context).apply {
                        text = preference.title
                        textSize = 15f
                        setTypeface(Typeface.DEFAULT_BOLD)
                        setTextColor(Color.parseColor("#1F2730"))
                    },
                )
                addView(space(4))
                addView(secondaryView(preference.description))
            },
        )
        addView(spaceWidth(12))
        addView(
            compactChipButton(
                label = if (preference.isEnabled) "On" else "Off",
                active = preference.isEnabled,
            ) {
                togglePreference(preference.key)
            },
        )
    }
}

internal fun MainActivity.sessionActionCard(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFF7F6"),
            strokeColor = Color.parseColor("#F2DEDA"),
            radiusDp = 20,
        )
        setPadding(dp(16), dp(14), dp(16), dp(14))

        addView(
            TextView(context).apply {
                text = "退出登录会清空当前会话和聊天缓存，但保留本地偏好设置。"
                textSize = 14f
                setTextColor(Color.parseColor("#7A4C51"))
            },
        )
        addView(space(14))
        addView(
            primaryButton("退出登录") {
                logout()
            },
        )
    }
}

internal fun MainActivity.settingsSkeletonCard(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 24,
        )
        setPadding(dp(18), dp(18), dp(18), dp(18))

        addView(
            View(context).apply {
                layoutParams = LinearLayout.LayoutParams(dp(64), dp(64))
                background = roundedBackground(
                    fillColor = Color.parseColor("#EEF1F4"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 32,
                )
            },
        )
        addView(spaceWidth(14))
        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(skeletonBar(widthDp = 160, heightDp = 16))
                addView(space(8))
                addView(skeletonBar(widthDp = 132, heightDp = 12))
                addView(space(8))
                addView(skeletonBar(widthDp = 96, heightDp = 12))
            },
        )
    }
}

internal fun MainActivity.settingsSkeletonRow(): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        background = roundedBackground(
            fillColor = Color.parseColor("#FFFFFF"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 20,
        )
        setPadding(dp(16), dp(14), dp(16), dp(14))

        addView(
            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                addView(skeletonBar(widthDp = 148, heightDp = 14))
                addView(space(8))
                addView(skeletonBar(widthDp = 220, heightDp = 12))
            },
        )
        addView(spaceWidth(12))
        addView(skeletonBar(widthDp = 56, heightDp = 28))
    }
}

internal fun MainActivity.emptyStateCard(
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

internal fun MainActivity.errorStateCard(
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

internal fun MainActivity.detailErrorStateCard(
    state: MainScreenState.ChatDetail,
    message: String,
): LinearLayout {
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
                        id = state.chatId,
                        title = state.chatTitle,
                        lastMessagePreview = "",
                        unreadCount = 0,
                        lastMessageAtLabel = "",
                        avatarLabel = "TG",
                    ),
                    highlightedMessageId = state.highlightedMessageId,
                    returnToSearch = state.returnToSearch,
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

internal fun MainActivity.settingsErrorStateCard(
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

        addView(titleView("设置加载失败", sizeSp = 20f))
        addView(space(8))
        addView(secondaryView(message))
        addView(space(18))
        addView(primaryButton("重试加载") { onRetry() })
    }
}

internal fun MainActivity.debugSection(state: MainScreenState.ChatList): LinearLayout {
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
                addView(debugScenarioButton("默认", ChatListScenario.DEFAULT, state.debugScenario == ChatListScenario.DEFAULT))
                addView(spaceWidth(8))
                addView(debugScenarioButton("空态", ChatListScenario.EMPTY, state.debugScenario == ChatListScenario.EMPTY))
                addView(spaceWidth(8))
                addView(debugScenarioButton("错误态", ChatListScenario.ERROR, state.debugScenario == ChatListScenario.ERROR))
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

internal fun MainActivity.debugScenarioButton(
    label: String,
    scenario: ChatListScenario,
    active: Boolean,
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
        setOnClickListener { switchScenario(scenario) }
    }
}

internal fun MainActivity.bottomNavigation(selectedTab: MainActivity.RootTab): LinearLayout {
    return LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        background = roundedBackground(
            fillColor = Color.parseColor("#F7FAFD"),
            strokeColor = Color.parseColor("#E3EAF1"),
            radiusDp = 26,
        )
        setPadding(dp(8), dp(8), dp(8), dp(8))

        addView(
            bottomTab("Chats", selected = selectedTab == MainActivity.RootTab.CHATS) {
                openChatsRoot()
            },
        )
        addView(
            bottomTab("Calls", selected = false) {
                openCallsPlaceholder()
            },
        )
        addView(
            bottomTab("Settings", selected = selectedTab == MainActivity.RootTab.SETTINGS) {
                openSettings(showLoading = selectedTab != MainActivity.RootTab.SETTINGS)
            },
        )
    }
}

internal fun MainActivity.bottomTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
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
        setOnClickListener { onClick() }
    }
}

internal fun MainActivity.avatarView(
    chat: ChatSummary,
    sizeDp: Int = 54,
    textSizeSp: Float = 16f,
): TextView {
    val style = avatarStyleFor(chat)
    return TextView(this).apply {
        text = chat.avatarLabel
        textSize = textSizeSp
        gravity = Gravity.CENTER
        setTypeface(Typeface.DEFAULT_BOLD)
        setTextColor(Color.parseColor(style.textColorHex))
        layoutParams = LinearLayout.LayoutParams(dp(sizeDp), dp(sizeDp))
        background = roundedBackground(
            fillColor = Color.parseColor(style.backgroundColorHex),
            strokeColor = Color.TRANSPARENT,
            radiusDp = sizeDp / 2,
        )
    }
}

internal fun MainActivity.mutedBadge(): TextView {
    return TextView(this).apply {
        text = "静音"
        textSize = 11f
        setTextColor(Color.parseColor("#7F8A96"))
    }
}

internal fun MainActivity.unreadBadge(count: Int): TextView {
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

internal fun MainActivity.topTextButton(
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

internal fun MainActivity.topIconButton(
    text: String,
    onClick: () -> Unit,
): Button {
    return Button(this).apply {
        this.text = text
        textSize = 16f
        isAllCaps = false
        minWidth = 0
        minimumWidth = 0
        setTextColor(Color.parseColor("#2481CC"))
        background = roundedBackground(
            fillColor = Color.parseColor("#EDF4FB"),
            strokeColor = Color.TRANSPARENT,
            radiusDp = 16,
        )
        setPadding(dp(10), dp(8), dp(10), dp(8))
        setOnClickListener { onClick() }
    }
}

internal fun MainActivity.centeredStatusPill(text: String): LinearLayout {
    return LinearLayout(this).apply {
        gravity = Gravity.CENTER
        addView(
            TextView(context).apply {
                this.text = text
                textSize = 12f
                setTextColor(Color.parseColor("#456983"))
                background = roundedBackground(
                    fillColor = Color.parseColor("#EEF5FB"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 14,
                )
                setPadding(dp(12), dp(7), dp(12), dp(7))
                maxLines = 2
                ellipsize = TextUtils.TruncateAt.END
            },
        )
    }
}

internal fun MainActivity.compactChipButton(
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

internal fun MainActivity.circleActionButton(
    text: String,
    enabled: Boolean,
    filled: Boolean,
    onClick: () -> Unit,
): Button {
    return Button(this).apply {
        this.text = text
        textSize = 18f
        isAllCaps = false
        isEnabled = enabled
        minWidth = 0
        minimumWidth = 0
        setTextColor(
            if (filled) {
                Color.WHITE
            } else {
                Color.parseColor("#2481CC")
            },
        )
        layoutParams = LinearLayout.LayoutParams(dp(42), dp(42))
        background = roundedBackground(
            fillColor = if (!enabled && filled) {
                Color.parseColor("#A6C8E5")
            } else if (filled) {
                Color.parseColor("#2481CC")
            } else {
                Color.WHITE
            },
            strokeColor = if (filled) Color.TRANSPARENT else Color.parseColor("#D7E0E8"),
            radiusDp = 21,
        )
        setPadding(0, 0, 0, 0)
        setOnClickListener { onClick() }
    }
}

internal fun MainActivity.detailAvatarLabel(title: String): String {
    val compact = title
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString(separator = "") { it.take(1) }
        .uppercase()
    return compact.ifBlank { "TG" }
}

internal fun MainActivity.mediaThumbnailView(
    attachment: MediaAttachment,
    heightDp: Int,
    showOverlayTitle: Boolean,
): FrameLayout {
    val backgroundColor = Color.parseColor(mediaToneFor(attachment))
    return FrameLayout(this).apply {
        background = roundedBackground(
            fillColor = backgroundColor,
            strokeColor = Color.TRANSPARENT,
            radiusDp = 18,
        )

        addView(
            TextView(context).apply {
                text = "PHOTO"
                textSize = 10f
                setTypeface(Typeface.DEFAULT_BOLD)
                setTextColor(Color.parseColor("#29475C"))
                background = roundedBackground(
                    fillColor = Color.parseColor("#CCFFFFFF"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 11,
                )
                setPadding(dp(8), dp(4), dp(8), dp(4))
            },
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START,
            ).apply {
                setMargins(dp(10), dp(10), 0, 0)
            },
        )

        addView(
            View(context).apply {
                background = roundedBackground(
                    fillColor = Color.parseColor("#22FFFFFF"),
                    strokeColor = Color.TRANSPARENT,
                    radiusDp = 14,
                )
            },
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(heightDp / 2),
                Gravity.CENTER,
            ).apply {
                setMargins(dp(10), dp(18), dp(10), dp(18))
            },
        )

        if (showOverlayTitle) {
            addView(
                TextView(context).apply {
                    text = attachment.title
                    textSize = 13f
                    setTypeface(Typeface.DEFAULT_BOLD)
                    setTextColor(Color.WHITE)
                    background = roundedBackground(
                        fillColor = Color.parseColor("#5E213445"),
                        strokeColor = Color.TRANSPARENT,
                        radiusDp = 12,
                    )
                    setPadding(dp(10), dp(7), dp(10), dp(7))
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                },
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM or Gravity.START,
                ).apply {
                    setMargins(dp(10), 0, dp(10), dp(10))
                },
            )
        }
    }
}

private data class AvatarStyle(
    val backgroundColorHex: String,
    val textColorHex: String,
)

private val avatarStyles = listOf(
    AvatarStyle(backgroundColorHex = "#DDF0FF", textColorHex = "#0E5D91"),
    AvatarStyle(backgroundColorHex = "#E8EAFD", textColorHex = "#4E5ABD"),
    AvatarStyle(backgroundColorHex = "#FFEBCD", textColorHex = "#A15E12"),
    AvatarStyle(backgroundColorHex = "#E5F7EC", textColorHex = "#1D7A47"),
    AvatarStyle(backgroundColorHex = "#FFE4EC", textColorHex = "#A03B63"),
    AvatarStyle(backgroundColorHex = "#EAF7FF", textColorHex = "#2B6B98"),
    AvatarStyle(backgroundColorHex = "#F0E8FF", textColorHex = "#6541A8"),
    AvatarStyle(backgroundColorHex = "#FDEDDC", textColorHex = "#9E6316"),
    AvatarStyle(backgroundColorHex = "#E7F8F0", textColorHex = "#25704A"),
    AvatarStyle(backgroundColorHex = "#ECEFF3", textColorHex = "#5D6875"),
)

private val mediaTones = listOf(
    "#A6D4FF",
    "#FFD6A5",
    "#CFEFD0",
    "#F8D5E7",
    "#DAD2FF",
)

private fun avatarStyleFor(chat: ChatSummary): AvatarStyle {
    return avatarStyles[paletteIndex(chat.id, avatarStyles.size)]
}

private fun mediaToneFor(attachment: MediaAttachment): String {
    return mediaTones[paletteIndex(attachment.id, mediaTones.size)]
}

private fun paletteIndex(key: String, size: Int): Int {
    return (key.hashCode() and Int.MAX_VALUE) % size
}
