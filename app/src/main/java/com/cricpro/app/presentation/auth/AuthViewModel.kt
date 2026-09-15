package com.cricpro.app.presentation.auth

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
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and Password cannot be empty")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Login failed") }
            )
        }
    }

    fun signUp(name: String, email: String, pass: String, confirmPass: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
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
            val result = authRepository.signUp(name, email, pass)
            result.fold(
                onSuccess = { user -> _authState.value = AuthState.Success(user) },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Sign up failed") }
            )
        }
    }

    fun continueAsGuest() {
        val guestUser = User(
            uid = "guest_${System.currentTimeMillis()}",
            fullName = "Guest Scorer",
            email = "guest@cricpro.local",
            isVerified = true
        )
        _authState.value = AuthState.Success(guestUser)
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordReset(email)
            result.fold(
                onSuccess = { _authState.value = AuthState.Error("Password reset email sent!") },
                onFailure = { err -> _authState.value = AuthState.Error(err.localizedMessage ?: "Failed to send reset email") }
            )
        }
    }
}
