package com.telegram.compare.kmp.shareddomain

class RestoreSessionUseCase(
    private val repository: SessionRepository,
) {
    fun execute(): SessionRestoreResult = repository.restoreSession()
}

class LoginWithCodeUseCase(
    private val repository: SessionRepository,
) {
    fun execute(
        phoneNumber: String,
        verificationCode: String,
    ): LoginResult {
        val normalizedPhone = phoneNumber.filterNot(Char::isWhitespace)
        if (!normalizedPhone.startsWith("+") || normalizedPhone.length < 8) {
            return LoginResult.InvalidInput("请输入带国家区号的手机号。")
        }

        val normalizedCode = verificationCode.filter(Char::isDigit)
        if (normalizedCode.length != 4) {
            return LoginResult.InvalidInput("请输入 4 位验证码。")
        }

        return repository.login(
            phoneNumber = normalizedPhone,
            verificationCode = normalizedCode,
        )
    }
}

class LogoutUseCase(
    private val repository: SessionRepository,
) {
    fun execute() {
        repository.clearSession()
    }
}
