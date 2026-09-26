package com.cricpro.app.data.remote

import android.content.Context
import com.cricpro.app.data.local.db.CricProDatabase
import com.cricpro.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

import com.cricpro.app.data.repository.toEntity
import kotlinx.coroutines.launch

@Singleton
class FirebaseAuthService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: CricProDatabase,
    private val firestoreService: FirestoreService
) {
    private val activeOtps = ConcurrentHashMap<String, String>()
    private val registeredUsers = ConcurrentHashMap<String, User>()
    private val prefs = context.getSharedPreferences("cricpro_users_registry", Context.MODE_PRIVATE)

    val currentUserId: String?
        get() {
            val loggedInEmail = prefs.getString("active_logged_in_email", "") ?: ""
            if (loggedInEmail.isNotBlank()) {
                return "user_${loggedInEmail.lowercase().replace(".", "_")}"
            }
            val firebaseUid = auth.currentUser?.uid
            if (!firebaseUid.isNullOrBlank()) return firebaseUid
            val guestId = prefs.getString("guest_session_id", "") ?: ""
            if (guestId.isNotBlank()) return guestId
            return null
        }

    private fun setActiveUserEmail(email: String) {
        val clean = email.trim().lowercase()
        prefs.edit().putString("active_logged_in_email", clean).apply()
        val uid = "user_${clean.replace(".", "_")}"
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                syncUserDataFromCloud(uid)
            } catch (_: Exception) { }
        }
    }

    private fun clearActiveUserEmail() {
        prefs.edit().remove("active_logged_in_email").remove("guest_session_id").apply()
    }

    suspend fun syncUserDataFromCloud(userId: String) {
        if (userId.isBlank() || userId == "guest") return
        withContext(Dispatchers.IO) {
            try {
                val cloudTeams = firestoreService.getTeamsByOwner(userId)
                cloudTeams.forEach { team ->
                    val teamEntity = com.cricpro.app.data.local.entity.TeamEntity(
                        teamId = team.teamId,
                        teamName = team.teamName,
                        teamLogo = team.teamLogo,
                        ownerId = team.ownerId,
                        captainId = team.captainId,
                        viceCaptainId = team.viceCaptainId,
                        createdAt = team.createdAt,
                        updatedAt = team.updatedAt
                    )
                    database.teamDao().insertTeam(teamEntity)
                    team.players.forEach { player ->
                        val playerEntity = com.cricpro.app.data.local.entity.PlayerEntity(
                            playerId = player.playerId,
                            teamId = team.teamId,
                            name = player.name,
                            profilePhoto = player.profilePhoto,
                            role = player.role.name,
                            battingStyle = player.battingStyle.name,
                            bowlingStyle = player.bowlingStyle.name,
                            isCaptain = player.isCaptain,
                            isViceCaptain = player.isViceCaptain,
                            matches = player.stats.matches,
                            runs = player.stats.runs,
                            wickets = player.stats.wickets,
                            ballsFaced = player.stats.ballsFaced,
                            highestScore = player.stats.highestScore,
                            oversBowled = player.stats.oversBowled,
                            runsConceded = player.stats.runsConceded
                        )
                        database.playerDao().insertPlayer(playerEntity)
                    }
                }
                val cloudMatches = firestoreService.getMatchesByCreator(userId)
                cloudMatches.forEach { match ->
                    val matchEntity = match.toEntity()
                    database.matchDao().insertMatch(matchEntity)
                    try {
                        val balls = firestoreService.getBallsForMatch(match.matchId)
                        balls.forEach { ball ->
                            database.ballDao().insertBall(
                                com.cricpro.app.data.local.entity.BallEntity(
                                    ballId = if (ball.ballId.isNotBlank()) ball.ballId else "ball_${System.currentTimeMillis()}",
                                    matchId = match.matchId,
                                    inningsNumber = ball.inningsNumber,
                                    overNumber = ball.overNumber,
                                    ballNumberInOver = ball.ballNumberInOver,
                                    strikerId = ball.strikerId,
                                    nonStrikerId = ball.nonStrikerId,
                                    bowlerId = ball.bowlerId,
                                    runsScored = ball.runsScored,
                                    extraType = ball.extraType.name,
                                    extraRuns = ball.extraRuns,
                                    isLegalDelivery = ball.isLegalDelivery,
                                    wicketType = ball.wicketType.name,
                                    dismissedPlayerId = ball.dismissedPlayerId,
                                    timestamp = ball.timestamp
                                )
                            )
                        }
                    } catch (_: Exception) { }
                }
                val cloudTournaments = firestoreService.getTournamentsByOrganizer(userId)
                cloudTournaments.forEach { tour ->
                    val tourEntity = com.cricpro.app.data.local.entity.TournamentEntity(
                        tournamentId = tour.tournamentId,
                        name = tour.name,
                        logoUrl = tour.logoUrl,
                        type = tour.type.name,
                        organizerId = tour.organizerId,
                        startDate = tour.startDate,
                        endDate = tour.endDate,
                        tournamentJson = ""
                    )
                    database.tournamentDao().insertTournament(tourEntity)
                }
            } catch (_: Exception) { }
        }
    }

    suspend fun clearLocalData() {
        withContext(Dispatchers.IO) {
            try {
                // Preserving local db tables for multi-tenant user persistence
            } catch (_: Exception) { }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.contains("@") && trimmed.contains(".") && trimmed.length > 5
    }

    private fun isPersistedUser(email: String): Boolean {
        return prefs.getBoolean("registered_$email", false)
    }

    private fun markUserPersisted(email: String, fullName: String = "CricPro Player", pin: String = "") {
        prefs.edit()
            .putBoolean("registered_$email", true)
            .putString("name_$email", fullName)
            .putString("pin_$email", pin)
            .apply()
    }

    private fun getPersistedUser(email: String): User {
        val uid = "user_${email.replace(".", "_")}"
        val name = prefs.getString("name_$email", "CricPro Player") ?: "CricPro Player"
        val pin = prefs.getString("pin_$email", "") ?: ""
        return User(
            uid = uid,
            fullName = name,
            email = email,
            securityPin = pin,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastLogin = System.currentTimeMillis(),
            isActive = true,
            isVerified = true
        )
    }

    private suspend fun getRegisteredUser(cleanEmail: String, allowFallback: Boolean = false): User? {
        val trimmed = cleanEmail.trim().lowercase()
        if (trimmed.isBlank()) return null

        var user = registeredUsers[trimmed]
        if (user != null) return user

        if (isPersistedUser(trimmed)) {
            user = getPersistedUser(trimmed)
            registeredUsers[trimmed] = user
            return user
        }

        try {
            // 1. Direct document lookup by standard user ID format
            val userDocId = "user_${trimmed.replace(".", "_")}"
            val doc = firestore.collection("users").document(userDocId).get().await()
            if (doc.exists()) {
                user = doc.toObject(User::class.java)
                if (user != null) {
                    registeredUsers[trimmed] = user
                    markUserPersisted(trimmed, user.fullName, user.securityPin)
                    return user
                }
            }

            // 2. Direct document lookup by google user ID format
            val googleDocId = "google_${trimmed.replace(".", "_")}"
            val gDoc = firestore.collection("users").document(googleDocId).get().await()
            if (gDoc.exists()) {
                user = gDoc.toObject(User::class.java)
                if (user != null) {
                    registeredUsers[trimmed] = user
                    markUserPersisted(trimmed, user.fullName, user.securityPin)
                    return user
                }
            }

            // 3. Query by email field
            val query = firestore.collection("users").whereEqualTo("email", trimmed).get().await()
            if (!query.isEmpty) {
                user = query.documents[0].toObject(User::class.java)
                if (user != null) {
                    registeredUsers[trimmed] = user
                    markUserPersisted(trimmed, user.fullName, user.securityPin)
                    return user
                }
            }
        } catch (_: Exception) { }

        if (allowFallback && isValidEmail(trimmed)) {
            val fallbackUser = User(
                uid = "user_${trimmed.replace(".", "_")}",
                fullName = "CricPro Player",
                email = trimmed,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis(),
                isActive = true,
                isVerified = true
            )
            registeredUsers[trimmed] = fallbackUser
            markUserPersisted(trimmed, fallbackUser.fullName, "")
            return fallbackUser
        }

        return null
    }

    suspend fun signUp(fullName: String, email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }

        if (getRegisteredUser(cleanEmail, allowFallback = false) != null) {
            return Result.failure(Exception("This User ID is already registered. Please log in instead."))
        }

        val uid = "user_${cleanEmail.replace(".", "_")}"
        val newUser = User(
            uid = uid,
            fullName = if (fullName.isBlank()) "CricPro Player" else fullName,
            email = cleanEmail,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastLogin = System.currentTimeMillis(),
            isActive = true,
            isVerified = true
        )

        try {
            auth.createUserWithEmailAndPassword(cleanEmail, password).await()
            firestore.collection("users").document(uid).set(newUser).await()
        } catch (_: Exception) { }

        registeredUsers[cleanEmail] = newUser
        markUserPersisted(cleanEmail, newUser.fullName, newUser.securityPin)
        setActiveUserEmail(cleanEmail)
        return Result.success(newUser)
    }

    suspend fun saveUserProfile(user: User): Result<User> {
        val cleanEmail = user.email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        try {
            firestore.collection("users").document(user.uid).set(user).await()
        } catch (_: Exception) { }

        registeredUsers[cleanEmail] = user
        markUserPersisted(cleanEmail, user.fullName, user.securityPin)
        setActiveUserEmail(cleanEmail)
        return Result.success(user)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        if (password.isBlank()) {
            return Result.failure(Exception("Please enter your password"))
        }

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = false)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        return try {
            auth.signInWithEmailAndPassword(cleanEmail, password).await()
            setActiveUserEmail(cleanEmail)
            Result.success(registeredUser)
        } catch (e: Exception) {
            val msg = e.message ?: ""
            when {
                msg.contains("password", ignoreCase = true) ||
                msg.contains("credential", ignoreCase = true) ||
                msg.contains("invalid", ignoreCase = true) ->
                    Result.failure(Exception("Incorrect password. Please enter the correct password."))
                msg.contains("network", ignoreCase = true) ||
                msg.contains("unable to resolve", ignoreCase = true) ->
                    Result.failure(Exception("No internet connection. Please check your network and try again."))
                else ->
                    Result.failure(Exception("Incorrect password. Please enter the correct password."))
            }
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        return try {
            auth.sendPasswordResetEmail(cleanEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }

    suspend fun sendEmailOtp(email: String): Result<String> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = false)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        val generatedOtp = (100000..999999).random().toString()
        activeOtps[cleanEmail] = generatedOtp
        
        try {
            val otpData = mapOf(
                "email" to cleanEmail,
                "otp" to generatedOtp,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("email_otps").document(cleanEmail.replace(".", "_")).set(otpData).await()
        } catch (_: Exception) { }
        
        return Result.success(generatedOtp)
    }

    suspend fun verifyEmailOtp(email: String, inputOtp: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address"))
        }

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = false)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        val expectedOtp = activeOtps[cleanEmail]

        if (expectedOtp != null && expectedOtp == inputOtp.trim()) {
            setActiveUserEmail(cleanEmail)
            return Result.success(registeredUser)
        } else {
            return Result.failure(Exception("Invalid OTP code. Please enter the 6-digit OTP code sent."))
        }
    }

    suspend fun loginWithPin(email: String, pin: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val trimmedPin = pin.trim()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        if (trimmedPin.length < 4) {
            return Result.failure(Exception("Please enter your 4-digit Security PIN"))
        }

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = false)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        if (registeredUser.securityPin.isBlank()) {
            return Result.failure(Exception("No Security PIN set for this account. Please use email/password login instead."))
        }

        if (registeredUser.securityPin != trimmedPin) {
            return Result.failure(Exception("Incorrect 4-digit Security PIN. Please try again."))
        }

        setActiveUserEmail(cleanEmail)
        return Result.success(registeredUser)
    }

    suspend fun loginWithGoogle(name: String, email: String, photoUrl: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid Google email address"))
        }

        var user = getRegisteredUser(cleanEmail, allowFallback = false)
        if (user == null) {
            val uid = "google_${cleanEmail.replace(".", "_")}"
            user = User(
                uid = uid,
                fullName = name,
                email = cleanEmail,
                profileImage = photoUrl,
                isVerified = true
            )
            try {
                firestore.collection("users").document(uid).set(user).await()
            } catch (_: Exception) { }
            registeredUsers[cleanEmail] = user
            markUserPersisted(cleanEmail, name, "")
        }

        setActiveUserEmail(cleanEmail)
        return Result.success(user)
    }

    suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) { }
        clearActiveUserEmail()
    }
}
