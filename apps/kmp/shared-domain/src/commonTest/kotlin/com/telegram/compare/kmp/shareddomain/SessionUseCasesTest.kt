package com.telegram.compare.kmp.shareddomain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SessionUseCasesTest {
    @Test
    fun rejectsPhoneNumberWithoutCountryCode() {
        val useCase = LoginWithCodeUseCase(FakeSessionRepository())

        val result = useCase.execute(
            phoneNumber = "13800000000",
            verificationCode = "2046",
        )

        assertEquals(
            LoginResult.InvalidInput("请输入带国家区号的手机号。"),
            result,
        )
    }

    @Test
    fun normalizesPhoneAndCodeBeforeCallingRepository() {
        val repository = FakeSessionRepository()
        val useCase = LoginWithCodeUseCase(repository)

        val result = useCase.execute(
            phoneNumber = "+86 138 0000 0000",
            verificationCode = "20 46",
        )

        assertTrue(result is LoginResult.Success)
        assertEquals("+8613800000000", repository.lastPhoneNumber)
        assertEquals("2046", repository.lastVerificationCode)
    }
}

private class FakeSessionRepository : SessionRepository {
    var lastPhoneNumber: String? = null
    var lastVerificationCode: String? = null

    override fun restoreSession(): SessionRestoreResult = SessionRestoreResult.NoSession

    override fun login(
        phoneNumber: String,
        verificationCode: String,
    ): LoginResult {
        lastPhoneNumber = phoneNumber
        lastVerificationCode = verificationCode

        return LoginResult.Success(
            UserSession(
                sessionId = "session-1",
                userId = "user-1",
                displayName = "Demo",
                phoneNumber = phoneNumber,
            ),
        )
    }

    override fun clearSession() = Unit
}
