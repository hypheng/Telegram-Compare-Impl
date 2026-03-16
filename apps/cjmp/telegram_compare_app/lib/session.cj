package ohos_app_cangjie_entry

import std.fs.*
import std.io.*

enum RestoreResult {
    | Restored(UserSession)
    | NoSession
    | Failed(String)
}

enum LoginResult {
    | Success(UserSession)
    | InvalidInput(String)
    | Failed(String)
}

class UserSession {
    let userId: String
    let displayName: String
    let phone: String
    let authToken: String

    public init(userId: String, displayName: String, phone: String, authToken: String) {
        this.userId = userId
        this.displayName = displayName
        this.phone = phone
        this.authToken = authToken
    }

    func summary(): String {
        return "${displayName}  ${phone}"
    }

    func serialize(): String {
        return String.join(["valid", userId, displayName, phone, authToken], delimiter: "|")
    }
}

class SessionRepository {
    let snapshotRoot: String
    let snapshotFile: String
    var lastWarning: String = ""

    public init(snapshotDir: String) {
        this.snapshotRoot = snapshotDir
        this.snapshotFile = "${snapshotDir}/session.snapshot"
    }

    func restore(): RestoreResult {
        lastWarning = ""
        if (!exists(snapshotFile)) {
            return NoSession
        }

        try {
            let file = File(snapshotFile, Read)
            let payload = String.fromUtf8(readToEnd(file)).trimEnd("\n\r")
            file.close()
            return parseSnapshot(payload)
        } catch (_) {
            clearSnapshot()
            return Failed("读取本地会话失败，已清理损坏快照。")
        }
    }

    func login(phone: String, code: String): LoginResult {
        lastWarning = ""
        let normalizedPhone = normalizePhone(phone)
        let normalizedCode = normalizeCode(code)

        if (normalizedPhone == "") {
            return InvalidInput("请输入手机号。")
        }
        if (normalizedPhone.size < 7) {
            return InvalidInput("请输入合法手机号。")
        }
        if (normalizedCode == "") {
            return InvalidInput("请输入验证码。")
        }
        if (normalizedCode != "2046") {
            return Failed("验证码不正确，请输入 demo 验证码 2046。")
        }

        let current = UserSession("demo-user", "@haifeng", normalizedPhone, "mock-session-token")
        persistSession(current)
        return Success(current)
    }

    func logout(): Unit {
        lastWarning = ""
        clearSnapshot()
    }

    func seedExpiredSession(): Unit {
        lastWarning = ""
        writeSnapshot("expired|legacy-user|snapshot-v0")
    }

    func takeLastWarning(): String {
        let warning = lastWarning
        lastWarning = ""
        return warning
    }

    func snapshotPathString(): String {
        return "${snapshotFile}"
    }

    func parseSnapshot(payload: String): RestoreResult {
        if (payload == "") {
            clearSnapshot()
            return Failed("本地会话为空，已清理后返回登录。")
        }

        let fields = payload.split("|")
        if (fields.size == 0) {
            clearSnapshot()
            return Failed("本地会话格式错误，已清理后返回登录。")
        }

        if (fields[0] == "expired") {
            clearSnapshot()
            return Failed("检测到失效会话，请重新登录。")
        }

        if (fields.size != 5 || fields[0] != "valid") {
            clearSnapshot()
            return Failed("本地会话格式错误，已清理后返回登录。")
        }

        return Restored(UserSession(fields[1], fields[2], fields[3], fields[4]))
    }

    func persistSession(current: UserSession): Unit {
        writeSnapshot(current.serialize())
    }

    func writeSnapshot(payload: String): Unit {
        try {
            ensureSnapshotRoot()
            let file = File(snapshotFile, Write)
            file.write("${payload}\n".toArray())
            file.close()
        } catch (_) {
            lastWarning = "本地持久化未完成，本次仅验证登录 UI。"
        }
    }

    func clearSnapshot(): Unit {
        try {
            if (exists(snapshotFile)) {
                remove(snapshotFile)
            }
        } catch (_) {
            lastWarning = "本地快照清理失败，请重新安装或清理应用数据。"
        }
    }

    func ensureSnapshotRoot(): Unit {
        if (!exists(snapshotRoot)) {
            Directory.create(snapshotRoot, recursive: true)
        }
    }

    func normalizePhone(value: String): String {
        return value.trimStart(" ").trimEnd(" ")
    }

    func normalizeCode(value: String): String {
        return value.trimStart(" ").trimEnd(" ")
    }
}
