package com.cricpro.app.data.repository

import com.cricpro.app.data.remote.FirebaseAuthService
import com.cricpro.app.domain.model.User
import com.cricpro.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                trySend(
                    User(
                        uid = firebaseUser.uid,
                        fullName = firebaseUser.displayName ?: "User",
                        email = firebaseUser.email ?: "",
                        isVerified = firebaseUser.isEmailVerified
                    )
                )
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(fullName: String, email: String, password: String): Result<User> {
        return authService.signUp(fullName, email, password)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return authService.login(email, password)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return authService.sendPasswordReset(email)
    }

    override suspend fun logout() {
        authService.logout()
    }

    override suspend fun getUserProfile(uid: String): Result<User> {
        return Result.success(User(uid = uid))
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return authService.saveUserProfile(user).map { Unit }
    }

    override suspend fun saveUserProfile(user: User): Result<User> {
        return authService.saveUserProfile(user)
    }

    override suspend fun sendEmailOtp(email: String): Result<String> {
        return authService.sendEmailOtp(email)
    }

    override suspend fun verifyEmailOtp(email: String, otp: String): Result<User> {
        return authService.verifyEmailOtp(email, otp)
    }

    override suspend fun loginWithPin(email: String, pin: String): Result<User> {
        return authService.loginWithPin(email, pin)
    }

    override suspend fun loginWithGoogle(name: String, email: String, photoUrl: String): Result<User> {
        return authService.loginWithGoogle(name, email, photoUrl)
    }
}
