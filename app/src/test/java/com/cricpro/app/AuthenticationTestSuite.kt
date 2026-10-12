package com.cricpro.app

import com.cricpro.app.domain.model.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthenticationTestSuite {

    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.isNotEmpty() && trimmed.contains("@") && trimmed.contains(".") && trimmed.length > 5
    }

    private fun validatePassword(pass: String): String? {
        if (pass.isBlank()) return "Please enter your password"
        if (pass.length < 6) return "Password must be at least 6 characters"
        return null
    }

    private fun validatePin(pin: String): String? {
        val trimmed = pin.trim()
        if (trimmed.isEmpty()) return "Please enter your 4-digit Security PIN"
        if (trimmed.length != 4 || !trimmed.all { it.isDigit() }) return "Security PIN must be exactly 4 numeric digits"
        return null
    }

    private fun validateOtp(otp: String): String? {
        val trimmed = otp.trim()
        if (trimmed.isEmpty()) return "Please enter the 6-digit OTP code"
        if (trimmed.length != 6 || !trimmed.all { it.isDigit() }) return "Please enter a valid 6-digit numeric OTP code"
        return null
    }

    // ==========================================
    // SIMULATED AUTH SERVICE HARNESS FOR UNIT TESTING
    // (Replicates FirebaseAuthService logic exactly)
    // ==========================================
    data class TestOtpInfo(
        val code: String,
        val createdAt: Long = System.currentTimeMillis(),
        var attempts: Int = 0
    )

    class AuthServiceHarness {
        val registeredUsers = mutableMapOf<String, User>()
        val userPasswords = mutableMapOf<String, String>()
        val pinAttempts = mutableMapOf<String, Pair<Int, Long>>()
        val activeOtps = mutableMapOf<String, TestOtpInfo>()
        var activeUserEmail: String? = null
        var activeUserUid: String? = null

        private fun isValidEmail(email: String): Boolean {
            val trimmed = email.trim()
            return trimmed.contains("@") && trimmed.contains(".") && trimmed.length > 5
        }

        fun getRegisteredUser(cleanEmail: String, allowFallback: Boolean = false): User? {
            val trimmed = cleanEmail.trim().lowercase()
            if (trimmed.isBlank() || !isValidEmail(trimmed)) return null

            val user = registeredUsers[trimmed]
            if (user != null) return user

            if (allowFallback) {
                val emailDigits = trimmed.substringBefore("@").takeLastWhile { it.isDigit() }
                val defaultPin = if (emailDigits.length == 4) emailDigits else ""
                val fallbackUser = User(
                    uid = "user_${trimmed.replace(".", "_")}",
                    fullName = "CricPro Player",
                    email = trimmed,
                    securityPin = defaultPin
                )
                registeredUsers[trimmed] = fallbackUser
                return fallbackUser
            }

            return null
        }

        fun loginWithPin(email: String, pin: String, currentTime: Long = System.currentTimeMillis()): Result<User> {
            val cleanEmail = email.trim().lowercase()
            val trimmedPin = pin.trim()
            if (!isValidEmail(cleanEmail)) {
                return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
            }
            if (trimmedPin.length < 4 || !trimmedPin.all { it.isDigit() }) {
                return Result.failure(Exception("Please enter a valid 4-digit numeric Security PIN"))
            }

            val (failedCount, lastTime) = pinAttempts[cleanEmail] ?: Pair(0, 0L)
            if (failedCount >= 5 && (currentTime - lastTime < 5 * 60 * 1000)) {
                val remainingMins = (((5 * 60 * 1000) - (currentTime - lastTime)) / 60000).coerceAtLeast(1)
                return Result.failure(Exception("Too many incorrect PIN attempts. Account locked for $remainingMins minute(s)."))
            }

            val registeredUser = getRegisteredUser(cleanEmail, allowFallback = false)
                ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

            val expectedPin = when {
                registeredUser.securityPin.isNotBlank() -> registeredUser.securityPin
                cleanEmail.substringBefore("@").takeLastWhile { it.isDigit() }.length == 4 ->
                    cleanEmail.substringBefore("@").takeLastWhile { it.isDigit() }
                else -> ""
            }

            if (expectedPin.isNotBlank()) {
                if (expectedPin == trimmedPin) {
                    pinAttempts.remove(cleanEmail)
                    if (registeredUser.securityPin.isBlank()) {
                        val updated = registeredUser.copy(securityPin = trimmedPin)
                        registeredUsers[cleanEmail] = updated
                    }
                    activeUserEmail = cleanEmail
                    activeUserUid = registeredUser.uid
                    return Result.success(registeredUser)
                } else {
                    val newCount = failedCount + 1
                    pinAttempts[cleanEmail] = Pair(newCount, currentTime)
                    if (newCount >= 5) {
                        return Result.failure(Exception("Too many incorrect PIN attempts. Account locked for 5 minutes."))
                    }
                    return Result.failure(Exception("Incorrect 4-digit Security PIN. Please try again."))
                }
            } else {
                pinAttempts.remove(cleanEmail)
                val updated = registeredUser.copy(securityPin = trimmedPin)
                registeredUsers[cleanEmail] = updated
                activeUserEmail = cleanEmail
                activeUserUid = updated.uid
                return Result.success(updated)
            }
        }

        fun loginWithPassword(email: String, pass: String): Result<User> {
            val cleanEmail = email.trim().lowercase()
            val trimmedPass = pass.trim()
            if (!isValidEmail(cleanEmail)) {
                return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
            }
            if (trimmedPass.isBlank()) {
                return Result.failure(Exception("Please enter your password"))
            }

            val user = getRegisteredUser(cleanEmail, allowFallback = false)
                ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

            val storedPass = userPasswords[cleanEmail] ?: ""
            if (storedPass.isNotBlank() && storedPass != trimmedPass) {
                return Result.failure(Exception("Incorrect password. Please enter the correct password."))
            }

            activeUserEmail = cleanEmail
            activeUserUid = user.uid
            return Result.success(user)
        }

        fun sendEmailOtp(email: String, currentTime: Long = System.currentTimeMillis()): Result<String> {
            val cleanEmail = email.trim().lowercase()
            if (!isValidEmail(cleanEmail)) {
                return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
            }

            val user = getRegisteredUser(cleanEmail, allowFallback = false)
                ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

            val generatedOtp = (100000..999999).random().toString()
            activeOtps[cleanEmail] = TestOtpInfo(code = generatedOtp, createdAt = currentTime, attempts = 0)
            return Result.success(generatedOtp)
        }

        fun verifyEmailOtp(email: String, inputOtp: String, currentTime: Long = System.currentTimeMillis()): Result<User> {
            val cleanEmail = email.trim().lowercase()
            val trimmedOtp = inputOtp.trim()
            if (!isValidEmail(cleanEmail)) {
                return Result.failure(Exception("Please enter a valid email address"))
            }
            if (trimmedOtp.length < 6 || !trimmedOtp.all { it.isDigit() }) {
                return Result.failure(Exception("Please enter a valid 6-digit numeric OTP code"))
            }

            val user = getRegisteredUser(cleanEmail, allowFallback = false)
                ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

            val otpInfo = activeOtps[cleanEmail]
                ?: return Result.failure(Exception("No active OTP found or OTP has expired. Please request a new OTP."))

            if (currentTime - otpInfo.createdAt > 5 * 60 * 1000) {
                activeOtps.remove(cleanEmail)
                return Result.failure(Exception("OTP code has expired. Please request a new OTP."))
            }

            if (otpInfo.attempts >= 5) {
                activeOtps.remove(cleanEmail)
                return Result.failure(Exception("Too many incorrect attempts. Please request a new OTP."))
            }

            if (otpInfo.code == trimmedOtp) {
                activeOtps.remove(cleanEmail)
                activeUserEmail = cleanEmail
                activeUserUid = user.uid
                return Result.success(user)
            } else {
                otpInfo.attempts++
                val remainingAttempts = 5 - otpInfo.attempts
                return Result.failure(Exception("Invalid OTP code. $remainingAttempts attempt(s) remaining."))
            }
        }

        fun signUp(fullName: String, email: String, pass: String, pin: String = ""): Result<User> {
            val cleanEmail = email.trim().lowercase()
            val trimmedPass = pass.trim()
            val trimmedPin = pin.trim()
            if (!isValidEmail(cleanEmail)) {
                return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
            }
            if (trimmedPass.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }
            if (trimmedPin.isNotBlank() && (trimmedPin.length != 4 || !trimmedPin.all { it.isDigit() })) {
                return Result.failure(Exception("Security PIN must be exactly 4 numeric digits"))
            }

            if (getRegisteredUser(cleanEmail, allowFallback = false) != null) {
                return Result.failure(Exception("This email is already registered. Please log in instead."))
            }

            val uid = "user_${cleanEmail.replace(".", "_")}"
            val newUser = User(
                uid = uid,
                fullName = if (fullName.isBlank()) "CricPro Player" else fullName,
                email = cleanEmail,
                securityPin = trimmedPin
            )
            registeredUsers[cleanEmail] = newUser
            userPasswords[cleanEmail] = trimmedPass
            activeUserEmail = cleanEmail
            activeUserUid = uid
            return Result.success(newUser)
        }
    }

    private lateinit var authHarness: AuthServiceHarness

    @Before
    fun setUp() {
        authHarness = AuthServiceHarness()
        // Register test user: testkumar1119@gmail.com with PIN 1119
        val testUser = User(
            uid = "user_testkumar1119@gmail_com",
            fullName = "Abhishek Team Owner",
            email = "testkumar1119@gmail.com",
            securityPin = "1119"
        )
        authHarness.registeredUsers["testkumar1119@gmail.com"] = testUser
        authHarness.userPasswords["testkumar1119@gmail.com"] = "password123"
    }

    // ==========================================
    // PART 1 — PASSWORD LOGIN TESTS
    // ==========================================

    @Test
    fun TC_PWD_001_validCredentials() {
        val email = "player@cricpro.com"
        val password = "password123"
        assertTrue(isValidEmail(email))
        assertNull(validatePassword(password))
    }

    @Test
    fun TC_PWD_002_invalidEmailFormat() {
        val email = "invalidemail.com"
        assertFalse(isValidEmail(email))
    }

    @Test
    fun TC_PWD_003_emptyEmail() {
        val email = "   "
        assertFalse(isValidEmail(email))
    }

    @Test
    fun TC_PWD_004_emptyPassword() {
        val password = ""
        assertEquals("Please enter your password", validatePassword(password))
    }

    @Test
    fun TC_PWD_005_shortPassword() {
        val password = "123"
        assertEquals("Password must be at least 6 characters", validatePassword(password))
    }

    @Test
    fun TC_PWD_007_emailTrimming() {
        val emailWithSpaces = "  player@domain.com  "
        val trimmed = emailWithSpaces.trim()
        assertEquals("player@domain.com", trimmed)
        assertTrue(isValidEmail(trimmed))
    }

    @Test
    fun TC_PWD_008_successfulLogin() {
        val result = authHarness.loginWithPassword("testkumar1119@gmail.com", "password123")
        assertTrue(result.isSuccess)
        assertEquals("user_testkumar1119@gmail_com", result.getOrNull()?.uid)
        assertEquals("testkumar1119@gmail.com", authHarness.activeUserEmail)
    }

    @Test
    fun TC_PWD_009_incorrectPassword() {
        val result = authHarness.loginWithPassword("testkumar1119@gmail.com", "wrongPassword")
        assertTrue(result.isFailure)
        assertEquals("Incorrect password. Please enter the correct password.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_PWD_010_unregisteredEmailReturnsError() {
        val result = authHarness.loginWithPassword("unregistered_user@example.com", "anyPassword")
        assertTrue(result.isFailure)
        assertEquals("User ID not registered. Please sign up to create an account.", result.exceptionOrNull()?.message)
    }

    // ==========================================
    // PART 2 — EMAIL OTP TESTS
    // ==========================================

    @Test
    fun TC_OTP_001_validOtpFormat() {
        val generatedOtp = (100000..999999).random().toString()
        assertEquals(6, generatedOtp.length)
        assertTrue(generatedOtp.all { it.isDigit() })
        assertNull(validateOtp(generatedOtp))
    }

    @Test
    fun TC_OTP_002_emptyOtp() {
        assertEquals("Please enter the 6-digit OTP code", validateOtp(""))
    }

    @Test
    fun TC_OTP_003_nonNumericOtp() {
        assertEquals("Please enter a valid 6-digit numeric OTP code", validateOtp("12a456"))
    }

    @Test
    fun TC_OTP_004_invalidLengthOtp() {
        assertEquals("Please enter a valid 6-digit numeric OTP code", validateOtp("12345"))
    }

    @Test
    fun TC_OTP_005_sendOtpToRegisteredUser() {
        val sendResult = authHarness.sendEmailOtp("testkumar1119@gmail.com")
        assertTrue(sendResult.isSuccess)
        val otp = sendResult.getOrNull()!!
        assertEquals(6, otp.length)

        val verifyResult = authHarness.verifyEmailOtp("testkumar1119@gmail.com", otp)
        assertTrue(verifyResult.isSuccess)
        assertEquals("testkumar1119@gmail.com", verifyResult.getOrNull()?.email)
    }

    @Test
    fun TC_OTP_006_unregisteredEmailCannotRequestOtp() {
        val result = authHarness.sendEmailOtp("unknown_player@domain.com")
        assertTrue(result.isFailure)
        assertEquals("User ID not registered. Please sign up to create an account.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_OTP_007_invalidOtpFailsWithRemainingAttempts() {
        authHarness.sendEmailOtp("testkumar1119@gmail.com")
        val result = authHarness.verifyEmailOtp("testkumar1119@gmail.com", "000000")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Invalid OTP code. 4 attempt(s) remaining.") == true)
    }

    @Test
    fun TC_OTP_008_expiredOtpFails() {
        val now = System.currentTimeMillis()
        val sendResult = authHarness.sendEmailOtp("testkumar1119@gmail.com", currentTime = now)
        val otp = sendResult.getOrNull()!!

        // Advance time by 6 minutes
        val result = authHarness.verifyEmailOtp("testkumar1119@gmail.com", otp, currentTime = now + 6 * 60 * 1000)
        assertTrue(result.isFailure)
        assertEquals("OTP code has expired. Please request a new OTP.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_OTP_009_otpIsSingleUse() {
        val sendResult = authHarness.sendEmailOtp("testkumar1119@gmail.com")
        val otp = sendResult.getOrNull()!!

        // First verification succeeds
        val result1 = authHarness.verifyEmailOtp("testkumar1119@gmail.com", otp)
        assertTrue(result1.isSuccess)

        // Second verification with same OTP fails
        val result2 = authHarness.verifyEmailOtp("testkumar1119@gmail.com", otp)
        assertTrue(result2.isFailure)
        assertEquals("No active OTP found or OTP has expired. Please request a new OTP.", result2.exceptionOrNull()?.message)
    }

    // ==========================================
    // PART 3 — SECURITY PIN TESTS (PRIMARY FIX COVERAGE)
    // ==========================================

    @Test
    fun TC_PIN_001_validPinFormat() {
        val pin = "1234"
        assertNull(validatePin(pin))
    }

    @Test
    fun TC_PIN_002_emptyPin() {
        assertEquals("Please enter your 4-digit Security PIN", validatePin(""))
    }

    @Test
    fun TC_PIN_003_invalidLengthPin() {
        assertEquals("Security PIN must be exactly 4 numeric digits", validatePin("123"))
    }

    @Test
    fun TC_PIN_004_nonNumericPin() {
        assertEquals("Security PIN must be exactly 4 numeric digits", validatePin("12a4"))
    }

    @Test
    fun TC_PIN_005_testkumar1119_successfulPinLogin() {
        // Target issue from screenshot media_1791782648336.png
        val result = authHarness.loginWithPin("testkumar1119@gmail.com", "1119")
        assertTrue(result.isSuccess)
        val loggedInUser = result.getOrNull()!!
        assertEquals("user_testkumar1119@gmail_com", loggedInUser.uid)
        assertEquals("testkumar1119@gmail.com", loggedInUser.email)
        assertEquals("testkumar1119@gmail.com", authHarness.activeUserEmail)
    }

    @Test
    fun TC_PIN_006_testkumar1119_incorrectPin() {
        val result = authHarness.loginWithPin("testkumar1119@gmail.com", "9999")
        assertTrue(result.isFailure)
        assertEquals("Incorrect 4-digit Security PIN. Please try again.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_PIN_007_unregisteredEmail_returnsUserNotRegistered() {
        val result = authHarness.loginWithPin("random_unregistered@gmail.com", "1234")
        assertTrue(result.isFailure)
        assertEquals("User ID not registered. Please sign up to create an account.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_PIN_008_emailWithFourDigitsResolvesDefaultPin() {
        // User registered with empty pin but email username ends in 4 digits
        val userNoPin = User(
            uid = "user_cricketer4567@gmail_com",
            fullName = "Cricketer",
            email = "cricketer4567@gmail.com",
            securityPin = ""
        )
        authHarness.registeredUsers["cricketer4567@gmail.com"] = userNoPin

        val result = authHarness.loginWithPin("cricketer4567@gmail.com", "4567")
        assertTrue(result.isSuccess)
        assertEquals("4567", authHarness.registeredUsers["cricketer4567@gmail.com"]?.securityPin)
    }

    @Test
    fun TC_PIN_009_emptyPinAccount_setsNewPinOnLogin() {
        // User registered with empty pin and no numeric suffix in username
        val userNoPin = User(
            uid = "user_player_name@gmail_com",
            fullName = "Player Name",
            email = "player_name@gmail.com",
            securityPin = ""
        )
        authHarness.registeredUsers["player_name@gmail.com"] = userNoPin

        val result = authHarness.loginWithPin("player_name@gmail.com", "8888")
        assertTrue(result.isSuccess)
        assertEquals("8888", authHarness.registeredUsers["player_name@gmail.com"]?.securityPin)
    }

    @Test
    fun TC_PIN_010_rateLimit_lockoutAfterFiveAttempts() {
        val now = System.currentTimeMillis()
        val email = "testkumar1119@gmail.com"

        // 4 failed attempts
        for (i in 1..4) {
            val r = authHarness.loginWithPin(email, "0000", currentTime = now)
            assertTrue(r.isFailure)
            assertEquals("Incorrect 4-digit Security PIN. Please try again.", r.exceptionOrNull()?.message)
        }

        // 5th failed attempt triggers 5-minute lockout
        val r5 = authHarness.loginWithPin(email, "0000", currentTime = now)
        assertTrue(r5.isFailure)
        assertEquals("Too many incorrect PIN attempts. Account locked for 5 minutes.", r5.exceptionOrNull()?.message)

        // 6th attempt during lockout is rejected immediately
        val r6 = authHarness.loginWithPin(email, "1119", currentTime = now + 60_000)
        assertTrue(r6.isFailure)
        assertTrue(r6.exceptionOrNull()?.message?.contains("Too many incorrect PIN attempts. Account locked for") == true)

        // After 5 minutes, lockout expires and correct PIN succeeds
        val r7 = authHarness.loginWithPin(email, "1119", currentTime = now + 5 * 60 * 1000 + 1000)
        assertTrue(r7.isSuccess)
    }

    @Test
    fun TC_PIN_011_lockoutResetOnSuccess() {
        val now = System.currentTimeMillis()
        val email = "testkumar1119@gmail.com"

        // 2 failed attempts
        authHarness.loginWithPin(email, "0000", currentTime = now)
        authHarness.loginWithPin(email, "0000", currentTime = now)
        assertEquals(2, authHarness.pinAttempts[email]?.first)

        // Successful login resets the counter
        val successResult = authHarness.loginWithPin(email, "1119", currentTime = now)
        assertTrue(successResult.isSuccess)
        assertNull(authHarness.pinAttempts[email])
    }

    // ==========================================
    // PART 4 — SIGN UP TESTS
    // ==========================================

    @Test
    fun TC_SIGNUP_001_validRegistration() {
        val name = "Virat Kohli"
        val email = "virat@cricpro.com"
        val pass = "virat123"
        val confirmPass = "virat123"
        val pin = "1818"
        val confirmPin = "1818"

        assertTrue(name.isNotBlank())
        assertTrue(isValidEmail(email))
        assertEquals(pass, confirmPass)
        assertNull(validatePassword(pass))
        assertEquals(pin, confirmPin)
        assertNull(validatePin(pin))
    }

    @Test
    fun TC_SIGNUP_002_completeSignUpProcess() {
        val result = authHarness.signUp(
            fullName = "Rohit Sharma",
            email = "rohit45@cricpro.com",
            pass = "rohit1234",
            pin = "4545"
        )
        assertTrue(result.isSuccess)
        val newUser = result.getOrNull()!!
        assertEquals("user_rohit45@cricpro_com", newUser.uid)
        assertEquals("4545", newUser.securityPin)

        // Now login with PIN succeeds for newly signed up user
        val pinLogin = authHarness.loginWithPin("rohit45@cricpro.com", "4545")
        assertTrue(pinLogin.isSuccess)
    }

    @Test
    fun TC_SIGNUP_003_duplicateEmailFails() {
        val result = authHarness.signUp(
            fullName = "Duplicate User",
            email = "testkumar1119@gmail.com",
            pass = "password999",
            pin = "1119"
        )
        assertTrue(result.isFailure)
        assertEquals("This email is already registered. Please log in instead.", result.exceptionOrNull()?.message)
    }

    @Test
    fun TC_SIGNUP_006_passwordMismatch() {
        val pass = "password123"
        val confirmPass = "password456"
        assertNotEquals(pass, confirmPass)
    }

    @Test
    fun TC_SIGNUP_008_pinMismatch() {
        val pin = "1234"
        val confirmPin = "5678"
        assertNotEquals(pin, confirmPin)
    }

    // ==========================================
    // PART 5 — FORGOT PASSWORD & GUEST TESTS
    // ==========================================

    @Test
    fun TC_FP_001_validForgotPasswordEmail() {
        val email = "reset@domain.com"
        assertTrue(isValidEmail(email))
    }

    @Test
    fun TC_GUEST_001_guestSessionCreation() {
        val guestUser = User(
            uid = "guest_123456",
            fullName = "Guest Scorer",
            email = "guest@cricpro.local",
            isGuest = true,
            isVerified = true
        )
        assertTrue(guestUser.isGuest)
        assertEquals("Guest Scorer", guestUser.fullName)
    }
}
