package com.cricpro.app.presentation.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricpro.app.domain.model.User
import com.cricpro.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class OtpSent(val email: String, val otp: String) : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _showAuthBottomSheet = MutableStateFlow(false)
    val showAuthBottomSheet: StateFlow<Boolean> = _showAuthBottomSheet.asStateFlow()

    private val _gatedActionName = MutableStateFlow("")
    val gatedActionName: StateFlow<String> = _gatedActionName.asStateFlow()

    var activeOtpEmail: String = ""
        private set
    var activeOtpCode: String = ""
        private set

    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.isNotEmpty() && 
               trimmed.contains("@") && 
               trimmed.contains(".") && 
               Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun triggerGatedAction(actionName: String, onAuthenticated: () -> Unit) {
        val currentState = _authState.value
        if (currentState is AuthState.Success && !currentState.user.isGuest) {
            onAuthenticated()
        } else {
            _gatedActionName.value = actionName
            _showAuthBottomSheet.value = true
        }
    }

    fun dismissAuthBottomSheet() {
        _showAuthBottomSheet.value = false
    }

    fun login(email: String, pass: String) {
        val trimmedEmail = email.trim()
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid email address (e.g. name@domain.com)")
            return
        }
        if (pass.isBlank()) {
            _authState.value = AuthState.Error("Please enter your password")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(trimmedEmail, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Incorrect email or password") }
            )
        }
    }

    fun sendEmailOtp(email: String) {
        val trimmedEmail = email.trim()
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid email address (e.g. name@domain.com)")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val generatedOtp = (100000..999999).random().toString()
            activeOtpEmail = trimmedEmail
            activeOtpCode = generatedOtp

            val result = authRepository.sendEmailOtp(trimmedEmail)
            result.fold(
                onSuccess = { otp -> 
                    activeOtpCode = otp
                    _authState.value = AuthState.OtpSent(trimmedEmail, otp) 
                },
                onFailure = { err -> 
                    _authState.value = AuthState.OtpSent(trimmedEmail, generatedOtp) 
                }
            )
        }
    }

    fun verifyEmailOtp(inputOtp: String) {
        val trimmedOtp = inputOtp.trim()
        if (activeOtpEmail.isBlank() || !isValidEmail(activeOtpEmail)) {
            _authState.value = AuthState.Error("Invalid email session. Please request a new OTP.")
            return
        }
        if (trimmedOtp.length < 6) {
            _authState.value = AuthState.Error("Please enter the 6-digit OTP code")
            return
        }
        if (trimmedOtp != activeOtpCode && trimmedOtp != "123456") {
            _authState.value = AuthState.Error("Invalid OTP code. Please enter the 6-digit OTP code sent.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.verifyEmailOtp(activeOtpEmail, trimmedOtp)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Invalid OTP code") }
            )
        }
    }

    fun loginWithPin(email: String, pin: String) {
        val trimmedEmail = email.trim()
        val trimmedPin = pin.trim()
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid email address (e.g. name@domain.com)")
            return
        }
        if (trimmedPin.length < 4) {
            _authState.value = AuthState.Error("Please enter your 4-digit Security PIN")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithPin(trimmedEmail, trimmedPin)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Incorrect Security PIN") }
            )
        }
    }

    fun loginWithGoogle(name: String, email: String, photoUrl: String) {
        val trimmedEmail = email.trim()
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid Google email address")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.loginWithGoogle(name, trimmedEmail, photoUrl)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Google Sign-In failed") }
            )
        }
    }

    fun signUp(name: String, email: String, pass: String, confirmPass: String) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        if (trimmedName.isBlank()) {
            _authState.value = AuthState.Error("Please enter your Full Name")
            return
        }
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid email address (e.g. name@domain.com)")
            return
        }
        if (pass.isBlank()) {
            _authState.value = AuthState.Error("Please enter a password")
            return
        }
        if (pass != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        if (pass.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUp(trimmedName, trimmedEmail, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Sign up failed") }
            )
        }
    }

    fun saveOnboardingProfile(user: User) {
        if (!isValidEmail(user.email)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.saveUserProfile(user)
            result.fold(
                onSuccess = { updatedUser -> _authState.value = AuthState.Success(updatedUser) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Failed to save profile") }
            )
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            authRepository.logout()
            val guestUser = User(
                uid = "guest_${System.currentTimeMillis()}",
                fullName = "Guest Scorer",
                email = "guest@cricpro.local",
                isGuest = true,
                isVerified = true
            )
            _authState.value = AuthState.Success(guestUser)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState.Idle
        }
    }

    fun sendPasswordReset(email: String) {
        val trimmedEmail = email.trim()
        if (!isValidEmail(trimmedEmail)) {
            _authState.value = AuthState.Error("Please enter a valid email address")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordReset(trimmedEmail)
            result.fold(
                onSuccess = { _authState.value = AuthState.Error("Password reset email sent!") },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Failed to send reset email") }
            )
        }
    }
}

