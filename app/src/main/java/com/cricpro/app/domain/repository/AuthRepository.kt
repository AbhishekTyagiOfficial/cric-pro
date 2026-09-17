package com.cricpro.app.domain.repository

import com.cricpro.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signUp(fullName: String, email: String, password: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
    suspend fun getUserProfile(uid: String): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun saveUserProfile(user: User): Result<User>
    suspend fun sendEmailOtp(email: String): Result<String>
    suspend fun verifyEmailOtp(email: String, otp: String): Result<User>
    suspend fun loginWithPin(email: String, pin: String): Result<User>
    suspend fun loginWithGoogle(name: String, email: String, photoUrl: String): Result<User>
}
