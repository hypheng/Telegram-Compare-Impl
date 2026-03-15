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
import com.telegram.compare.kmp.shareddomain.ChatSummary
import com.telegram.compare.kmp.shareddomain.LoginResult
import com.telegram.compare.kmp.shareddomain.LoginWithCodeUseCase
import com.telegram.compare.kmp.shareddomain.LogoutUseCase
import com.telegram.compare.kmp.shareddomain.RestoreSessionUseCase
import com.telegram.compare.kmp.shareddomain.SessionRestoreResult
import com.telegram.compare.kmp.shareddomain.UserSession
import com.telegram.compare.kmp.shareddata.DemoSessionRepository
import com.telegram.compare.kmp.shareddata.InMemoryChatRepository
import com.telegram.compare.kmp.shareddata.PreferencesSessionStorage
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())

    private val chatRepository = InMemoryChatRepository()
    private lateinit var sessionRepository: DemoSessionRepository

    private var screenState: MainScreenState = MainScreenState.Restoring
    private var phoneDraft = "+86 138 0000 0000"
    private var codeDraft = DemoSessionRepository.DEMO_VERIFICATION_CODE
    private var latestRestoreMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionRepository = DemoSessionRepository(
            storage = PreferencesSessionStorage(
                sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE),
            ),
        )

        render(MainScreenState.Restoring)
        startRestoreFlow()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun startRestoreFlow() {
        handler.removeCallbacksAndMessages(null)
        render(MainScreenState.Restoring)

        handler.postDelayed(
            {
                when (val result = RestoreSessionUseCase(sessionRepository).execute()) {
                    is SessionRestoreResult.Restored -> {
                        latestRestoreMessage = null
                        renderHome(
                            session = result.session,
                            statusMessage = "已恢复上次会话，主壳入口已就绪。",
                        )
                    }
                    SessionRestoreResult.NoSession -> {
                        latestRestoreMessage = null
                        render(MainScreenState.Login())
                    }
                    is SessionRestoreResult.Failed -> {
                        latestRestoreMessage = result.message
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
                        renderHome(
                            session = result.session,
                            statusMessage = "登录成功，已进入 Chat List Entry Shell。",
                        )
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

    private fun logout() {
        handler.removeCallbacksAndMessages(null)
        LogoutUseCase(sessionRepository).execute()
        latestRestoreMessage = null
        render(MainScreenState.Login(formMessage = "已退出登录。"))
    }

    private fun seedExpiredSession() {
        sessionRepository.seedExpiredSession()
        val homeState = screenState as? MainScreenState.Home ?: return
        render(
            homeState.copy(
                statusMessage = "已写入失效会话。请重新启动应用验证恢复失败路径。",
            ),
        )
    }

    private fun renderHome(
        session: UserSession,
        statusMessage: String,
    ) {
        render(
            MainScreenState.Home(
                session = session,
                chats = chatRepository.listChats(),
                statusMessage = statusMessage,
            ),
        )
    }

    private fun render(state: MainScreenState) {
        screenState = state
        val content = when (state) {
            MainScreenState.Restoring -> buildRestoringScreen()
            is MainScreenState.Login -> buildLoginScreen(state)
            is MainScreenState.Home -> buildHomeScreen(state)
        }
        setContentView(content)
    }

    private fun buildRestoringScreen(): View {
        val container = baseColumn(
            gravity = Gravity.CENTER_HORIZONTAL,
            verticalGravity = Gravity.CENTER_VERTICAL,
        )

        container.addView(titleView("Telegram Compare", sizeSp = 24f))
        container.addView(space(24))
        container.addView(
            ProgressBar(this).apply {
                isIndeterminate = true
                contentDescription = "正在恢复会话"
            },
        )
        container.addView(space(20))
        container.addView(bodyView("正在恢复上次会话..."))
        container.addView(space(8))
        container.addView(secondaryView("如果没有保存的会话，将自动进入登录。"))

        return wrapInScroll(container)
    }

    private fun buildLoginScreen(state: MainScreenState.Login): View {
        val container = baseColumn()

        container.addView(titleView("登录 Telegram Compare"))
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
        container.addView(space(16))
        container.addView(infoBanner("Demo 环境固定验证码: 2046"))

        return wrapInScroll(container)
    }

    private fun buildHomeScreen(state: MainScreenState.Home): View {
        val container = baseColumn()

        container.addView(titleView("Chats"))
        container.addView(space(8))
        container.addView(infoBanner(state.statusMessage))
        container.addView(space(20))
        container.addView(
            sessionCard(
                title = state.session.displayName,
                subtitle = "${state.session.userId}  ·  ${state.session.phoneNumber}",
            ),
        )
        container.addView(space(20))
        container.addView(labelView("Chat List Entry Shell"))
        container.addView(space(8))
        state.chats.forEachIndexed { index, chat ->
            container.addView(chatRow(chat))
            if (index < state.chats.lastIndex) {
                container.addView(space(8))
            }
        }
        container.addView(space(16))
        container.addView(
            secondaryView("当前列表只用于承接 S1 成功落点，S2 列表刷新和搜索仍未验收。"),
        )
        container.addView(space(24))
        container.addView(
            primaryButton("退出登录") {
                logout()
            },
        )
        container.addView(space(12))
        container.addView(
            secondaryButton("写入失效会话用于测试") {
                seedExpiredSession()
            },
        )

        return wrapInScroll(container)
    }

    private fun wrapInScroll(content: LinearLayout): ScrollView {
        return ScrollView(this).apply {
            setBackgroundColor(Color.WHITE)
            addView(
                content,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                ),
            )
        }
    }

    private fun baseColumn(
        gravity: Int = Gravity.TOP,
        verticalGravity: Int = Gravity.NO_GRAVITY,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(dp(24), dp(32), dp(24), dp(32))
            this.gravity = gravity or verticalGravity
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
            textSize = 14f
            setTextColor(Color.parseColor("#1F5F8B"))
            background = roundedBackground(
                fillColor = Color.parseColor("#EEF7FF"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(14), dp(16), dp(14))
        }
    }

    private fun sessionCard(
        title: String,
        subtitle: String,
    ): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#F7F8FA"),
                strokeColor = Color.TRANSPARENT,
            )
            setPadding(dp(16), dp(16), dp(16), dp(16))
            addView(
                TextView(context).apply {
                    text = title
                    textSize = 18f
                    setTypeface(Typeface.DEFAULT_BOLD)
                    setTextColor(Color.parseColor("#1B1F23"))
                },
            )
            addView(space(6))
            addView(
                TextView(context).apply {
                    text = subtitle
                    textSize = 14f
                    setTextColor(Color.parseColor("#667781"))
                },
            )
        }
    }

    private fun chatRow(chat: ChatSummary): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = roundedBackground(
                fillColor = Color.parseColor("#FFFFFF"),
                strokeColor = Color.parseColor("#E8EDF1"),
            )
            setPadding(dp(16), dp(14), dp(16), dp(14))

            val topLine = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                addView(
                    TextView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        text = chat.title
                        textSize = 16f
                        setTypeface(Typeface.DEFAULT_BOLD)
                        setTextColor(Color.parseColor("#1B1F23"))
                    },
                )
                addView(
                    TextView(context).apply {
                        text = if (chat.unreadCount > 0) "${chat.unreadCount} 未读" else "已读"
                        textSize = 12f
                        setTextColor(Color.parseColor("#2481CC"))
                    },
                )
            }

            addView(topLine)
            addView(space(6))
            addView(
                TextView(context).apply {
                    text = chat.lastMessagePreview
                    textSize = 14f
                    setTextColor(Color.parseColor("#667781"))
                },
            )
        }
    }

    private fun roundedBackground(
        fillColor: Int,
        strokeColor: Int,
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dp(18).toFloat()
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

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    private companion object {
        const val PREFS_NAME = "telegram_compare_session"
        const val RESTORE_DELAY_MS = 650L
        const val LOGIN_DELAY_MS = 450L
    }
}
