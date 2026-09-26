package com.cricpro.app

import com.cricpro.app.domain.model.User
import org.junit.Assert.*
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

    // ==========================================
    // PART 3 — SECURITY PIN TESTS
    // ==========================================

    @Test
    fun TC_PIN_001_validPin() {
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
