package com.telegram.compare.kmp.shareddata

import com.telegram.compare.kmp.shareddomain.LoginResult
import com.telegram.compare.kmp.shareddomain.SessionRestoreResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoSessionRepositoryTest {
    @Test
    fun loginPersistsSessionAndRestoreReadsItBack() {
        val storage = InMemorySessionStorage()
        val repository = DemoSessionRepository(storage)

        val loginResult = repository.login(
            phoneNumber = "+8613800000000",
            verificationCode = DemoSessionRepository.DEMO_VERIFICATION_CODE,
        )

        assertTrue(loginResult is LoginResult.Success)
        val restoreResult = repository.restoreSession()
        assertTrue(restoreResult is SessionRestoreResult.Restored)
        assertEquals("+8613800000000", restoreResult.session.phoneNumber)
    }

    @Test
    fun wrongVerificationCodeDoesNotPersistSession() {
        val storage = InMemorySessionStorage()
        val repository = DemoSessionRepository(storage)

        val loginResult = repository.login(
            phoneNumber = "+8613800000000",
            verificationCode = "0000",
        )

        assertEquals(
            LoginResult.Failed("验证码不正确，请输入 demo 验证码 2046。"),
            loginResult,
        )
        assertEquals(null, storage.read())
    }

    @Test
    fun expiredSessionFailsOnceAndThenClearsStorage() {
        val storage = InMemorySessionStorage()
        val repository = DemoSessionRepository(storage)

        repository.seedExpiredSession()

        val firstRestore = repository.restoreSession()
        val secondRestore = repository.restoreSession()

        assertEquals(
            SessionRestoreResult.Failed("已保存的会话已失效，请重新登录。"),
            firstRestore,
        )
        assertEquals(SessionRestoreResult.NoSession, secondRestore)
    }
}
