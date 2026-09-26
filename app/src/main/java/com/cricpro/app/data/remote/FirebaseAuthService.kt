package com.cricpro.app.data.remote

import android.content.Context
import com.cricpro.app.data.local.db.CricProDatabase
import com.cricpro.app.data.repository.toEntity
import com.cricpro.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    data class OtpInfo(
        val code: String,
        val createdAt: Long = System.currentTimeMillis(),
        var attempts: Int = 0
    )

    private val activeOtps = ConcurrentHashMap<String, OtpInfo>()
    private val pinAttempts = ConcurrentHashMap<String, Pair<Int, Long>>()
    private val otpResendCooldown = ConcurrentHashMap<String, Long>()
    private val registeredUsers = ConcurrentHashMap<String, User>()
    private val prefs = context.getSharedPreferences("cricpro_users_registry", Context.MODE_PRIVATE)

    val currentUserId: String?
        get() {
            val storedUid = prefs.getString("active_user_uid", "") ?: ""
            if (storedUid.isNotBlank()) return storedUid
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

    private fun setActiveUserEmail(email: String, userUid: String? = null) {
        val clean = email.trim().lowercase()
        val uid = userUid ?: "user_${clean.replace(".", "_")}"
        prefs.edit()
            .putString("active_logged_in_email", clean)
            .putString("active_user_uid", uid)
            .apply()
        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
            try {
                syncUserDataFromCloud(uid)
            } catch (_: Exception) { }
        }
    }

    private fun clearActiveUserEmail() {
        prefs.edit()
            .remove("active_logged_in_email")
            .remove("active_user_uid")
            .remove("guest_session_id")
            .apply()
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
                // Preserved local db records per user for seamless offline & re-login access
            } catch (_: Exception) { }
        }
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

    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        return trimmed.contains("@") && trimmed.contains(".") && trimmed.length > 5
    }

    private fun isPersistedUser(email: String): Boolean {
        return prefs.getBoolean("registered_$email", false)
    }

    private fun markUserPersisted(email: String, fullName: String = "CricPro Player", pin: String = "", password: String = "") {
        val editor = prefs.edit()
            .putBoolean("registered_$email", true)
            .putString("name_$email", fullName)
        if (pin.isNotBlank()) {
            editor.putString("pin_$email", pin)
        }
        if (password.isNotBlank()) {
            editor.putString("pass_$email", password)
        }
        editor.apply()
    }

    private fun getPersistedPassword(email: String): String {
        return prefs.getString("pass_$email", "") ?: ""
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

        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
            } catch (_: Exception) { }
        }

        var firestoreFailed = false
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
        } catch (_: Exception) {
            firestoreFailed = true
        }

        if ((allowFallback || firestoreFailed) && isValidEmail(trimmed)) {
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
            if (!firestoreFailed) {
                markUserPersisted(trimmed, fallbackUser.fullName, "")
            }
            return fallbackUser
        }

        // 4. Fallback for valid existing user email during login to prevent blocking access
        if (allowFallback && isValidEmail(trimmed)) {
            val uid = "user_${trimmed.replace(".", "_")}"
            val fallbackUser = User(
                uid = uid,
                fullName = trimmed.substringBefore("@").replace(".", " "),
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

    suspend fun signUp(fullName: String, email: String, password: String, securityPin: String = ""): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val trimmedPass = password.trim()
        val trimmedPin = securityPin.trim()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        if (trimmedPass.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        if (trimmedPin.isNotBlank() && (trimmedPin.length != 4 || !trimmedPin.all { it.isDigit() })) {
            return Result.failure(Exception("Security PIN must be exactly 4 numeric digits"))
        }

        if (isPersistedUser(cleanEmail)) {
            return Result.failure(Exception("This email is already registered. Please log in instead."))
        }

        val uid = "user_${cleanEmail.replace(".", "_")}"
        val newUser = User(
            uid = uid,
            fullName = if (fullName.isBlank()) "CricPro Player" else fullName,
            email = cleanEmail,
            securityPin = trimmedPin,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            lastLogin = System.currentTimeMillis(),
            isActive = true,
            isVerified = true
        )

        // Create Firebase Auth account
        try {
            auth.createUserWithEmailAndPassword(cleanEmail, trimmedPass).await()
        } catch (e: Exception) {
            val msg = e.message ?: ""
            if (msg.contains("email address is already in use", ignoreCase = true) ||
                msg.contains("EMAIL_EXISTS", ignoreCase = true)) {
                return Result.failure(Exception("This email is already registered. Please log in instead."))
            }
            if (msg.contains("weak password", ignoreCase = true) ||
                msg.contains("WEAK_PASSWORD", ignoreCase = true)) {
                return Result.failure(Exception("Password is too weak. Please use at least 6 characters."))
            }
        }

        // Save to Firestore
        try {
            firestore.collection("users").document(uid).set(newUser).await()
        } catch (_: Exception) { }

        registeredUsers[cleanEmail] = newUser
        markUserPersisted(cleanEmail, newUser.fullName, newUser.securityPin, trimmedPass)
        setActiveUserEmail(cleanEmail, uid)
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
        setActiveUserEmail(cleanEmail, user.uid)
        return Result.success(user)
    }

    suspend fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val trimmedPass = password.trim()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        if (trimmedPass.isBlank()) {
            return Result.failure(Exception("Please enter your password"))
        }

        val storedPass = getPersistedPassword(cleanEmail)

        // Try Firebase Auth sign-in
        return try {
            auth.signInWithEmailAndPassword(cleanEmail, trimmedPass).await()
            val registeredUser = getRegisteredUser(cleanEmail, allowFallback = true)
                ?: User(
                    uid = "user_${cleanEmail.replace(".", "_")}",
                    fullName = "CricPro Player",
                    email = cleanEmail,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    lastLogin = System.currentTimeMillis(),
                    isActive = true,
                    isVerified = true
                )
            registeredUsers[cleanEmail] = registeredUser
            markUserPersisted(cleanEmail, registeredUser.fullName, registeredUser.securityPin, trimmedPass)
            setActiveUserEmail(cleanEmail, registeredUser.uid)
            Result.success(registeredUser)
        } catch (e: Exception) {
            val msg = e.message ?: ""

            // Incorrect password from Firebase Auth
            if (e is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ||
                msg.contains("password", ignoreCase = true) ||
                msg.contains("credential", ignoreCase = true) ||
                msg.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true)) {
                return Result.failure(Exception("Incorrect password. Please enter the correct password."))
            }

            // User not found in Firebase Auth
            if (e is com.google.firebase.auth.FirebaseAuthInvalidUserException ||
                msg.contains("no user record", ignoreCase = true) ||
                msg.contains("user may have been deleted", ignoreCase = true) ||
                msg.contains("USER_NOT_FOUND", ignoreCase = true)) {

                if (isPersistedUser(cleanEmail)) {
                    if (storedPass.isNotBlank()) {
                        if (storedPass == trimmedPass) {
                            val localUser = getPersistedUser(cleanEmail)
                            registeredUsers[cleanEmail] = localUser
                            setActiveUserEmail(cleanEmail, localUser.uid)
                            return Result.success(localUser)
                        } else {
                            return Result.failure(Exception("Incorrect password. Please enter the correct password."))
                        }
                    }
                }
                return Result.failure(Exception("User ID not registered. Please sign up to create an account."))
            }

            // Fallback for Firebase configuration/network issues
            if (isPersistedUser(cleanEmail)) {
                if (storedPass.isNotBlank() && storedPass != trimmedPass) {
                    return Result.failure(Exception("Incorrect password. Please enter the correct password."))
                }
                val localUser = getPersistedUser(cleanEmail)
                registeredUsers[cleanEmail] = localUser
                setActiveUserEmail(cleanEmail, localUser.uid)
                return Result.success(localUser)
            }

            Result.failure(Exception("Login failed. Please check your credentials and try again."))
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

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = true)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        val now = System.currentTimeMillis()
        val lastResend = otpResendCooldown[cleanEmail] ?: 0L
        if (now - lastResend < 60_000) {
            val remainingSecs = ((60_000 - (now - lastResend)) / 1000).coerceAtLeast(1)
            return Result.failure(Exception("Please wait $remainingSecs seconds before requesting a new OTP."))
        }

        val generatedOtp = (100000..999999).random().toString()
        activeOtps[cleanEmail] = OtpInfo(code = generatedOtp, createdAt = now, attempts = 0)
        otpResendCooldown[cleanEmail] = now

        try {
            val otpData = mapOf(
                "email" to cleanEmail,
                "otp" to generatedOtp,
                "createdAt" to now,
                "expiresAt" to (now + 5 * 60 * 1000)
            )
            firestore.collection("email_otps").document(cleanEmail.replace(".", "_")).set(otpData).await()
        } catch (_: Exception) { }

        return Result.success(generatedOtp)
    }

    suspend fun verifyEmailOtp(email: String, inputOtp: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val trimmedOtp = inputOtp.trim()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address"))
        }
        if (trimmedOtp.length < 6 || !trimmedOtp.all { it.isDigit() }) {
            return Result.failure(Exception("Please enter a valid 6-digit numeric OTP code"))
        }

        val registeredUser = getRegisteredUser(cleanEmail, allowFallback = true)
            ?: return Result.failure(Exception("User ID not registered. Please sign up to create an account."))

        val otpInfo = activeOtps[cleanEmail]
            ?: return Result.failure(Exception("No active OTP found or OTP has expired. Please request a new OTP."))

        val now = System.currentTimeMillis()
        if (now - otpInfo.createdAt > 5 * 60 * 1000) { // 5 minutes expiration
            activeOtps.remove(cleanEmail)
            return Result.failure(Exception("OTP code has expired. Please request a new OTP."))
        }

        if (otpInfo.attempts >= 5) {
            activeOtps.remove(cleanEmail)
            return Result.failure(Exception("Too many incorrect attempts. Please request a new OTP."))
        }

        if (otpInfo.code == trimmedOtp) {
            activeOtps.remove(cleanEmail) // Invalidate OTP after successful verification (single-use)
            setActiveUserEmail(cleanEmail, registeredUser.uid)
            return Result.success(registeredUser)
        } else {
            otpInfo.attempts++
            val remainingAttempts = 5 - otpInfo.attempts
            return Result.failure(Exception("Invalid OTP code. $remainingAttempts attempt(s) remaining."))
        }
    }

    suspend fun loginWithPin(email: String, pin: String): Result<User> {
        val cleanEmail = email.trim().lowercase()
        val trimmedPin = pin.trim()
        if (!isValidEmail(cleanEmail)) {
            return Result.failure(Exception("Please enter a valid email address (e.g. name@domain.com)"))
        }
        if (trimmedPin.length < 4 || !trimmedPin.all { it.isDigit() }) {
            return Result.failure(Exception("Please enter a valid 4-digit numeric Security PIN"))
        }

        val now = System.currentTimeMillis()
        val (failedCount, lastTime) = pinAttempts[cleanEmail] ?: Pair(0, 0L)
        if (failedCount >= 5 && (now - lastTime < 5 * 60 * 1000)) {
            val remainingMins = (((5 * 60 * 1000) - (now - lastTime)) / 60000).coerceAtLeast(1)
            return Result.failure(Exception("Too many incorrect PIN attempts. Account locked for $remainingMins minute(s)."))
        }

        // Helper to evaluate PIN match with rate-limiting recording
        fun checkPinMatch(expectedPin: String, userToLogin: User): Result<User> {
            if (expectedPin.isBlank()) {
                return Result.failure(Exception("No Security PIN set for this account. Please use email/password login instead."))
            }
            if (expectedPin == trimmedPin) {
                pinAttempts.remove(cleanEmail)
                setActiveUserEmail(cleanEmail, userToLogin.uid)
                return Result.success(userToLogin)
            } else {
                val newCount = failedCount + 1
                pinAttempts[cleanEmail] = Pair(newCount, now)
                if (newCount >= 5) {
                    return Result.failure(Exception("Too many incorrect PIN attempts. Account locked for 5 minutes."))
                }
                return Result.failure(Exception("Incorrect 4-digit Security PIN. Please try again."))
            }
        }

        // Step 1: Check in-memory cache
        val cachedUser = registeredUsers[cleanEmail]
        if (cachedUser != null) {
            return checkPinMatch(cachedUser.securityPin, cachedUser)
        }

        // Step 2: Check local SharedPreferences
        if (isPersistedUser(cleanEmail)) {
            val localUser = getPersistedUser(cleanEmail)
            registeredUsers[cleanEmail] = localUser
            return checkPinMatch(localUser.securityPin, localUser)
        }

        // Ensure active Firebase session so Firestore security rules pass
        if (auth.currentUser == null) {
            try {
                auth.signInAnonymously().await()
            } catch (_: Exception) { }
        }

        // Step 3: Fetch from Firestore directly (source of truth for PIN)
        try {
            var fsUser: User? = null

            // 3a. Standard user ID format
            val userDocId = "user_${cleanEmail.replace(".", "_")}"
            val doc = firestore.collection("users").document(userDocId).get().await()
            if (doc.exists()) {
                fsUser = doc.toObject(User::class.java)
            }

            // 3b. Google user ID format
            if (fsUser == null) {
                val googleDocId = "google_${cleanEmail.replace(".", "_")}"
                val gDoc = firestore.collection("users").document(googleDocId).get().await()
                if (gDoc.exists()) {
                    fsUser = gDoc.toObject(User::class.java)
                }
            }

            // 3c. Query by lowercase email field
            if (fsUser == null) {
                val query = firestore.collection("users").whereEqualTo("email", cleanEmail).get().await()
                if (!query.isEmpty) {
                    fsUser = query.documents[0].toObject(User::class.java)
                }
            }

            // 3d. Query by raw email field (casing fallback)
            if (fsUser == null && email.trim() != cleanEmail) {
                val queryRaw = firestore.collection("users").whereEqualTo("email", email.trim()).get().await()
                if (!queryRaw.isEmpty) {
                    fsUser = queryRaw.documents[0].toObject(User::class.java)
                }
            }

            if (fsUser != null) {
                registeredUsers[cleanEmail] = fsUser
                markUserPersisted(cleanEmail, fsUser.fullName, fsUser.securityPin)
                return checkPinMatch(fsUser.securityPin, fsUser)
            }

            // Check if account exists in Firebase Auth
            var isAuthRegistered = false
            try {
                val methods = auth.fetchSignInMethodsForEmail(cleanEmail).await()
                val signInMethods = methods.signInMethods
                if (!signInMethods.isNullOrEmpty()) {
                    isAuthRegistered = true
                }
            } catch (_: Exception) { }

            if (isAuthRegistered || isPersistedUser(cleanEmail)) {
                return Result.failure(Exception("No Security PIN set for this account. Please use email/password login instead."))
            }

            // Truly not registered anywhere
            return Result.failure(Exception("User ID not registered. Please sign up to create an account."))
        } catch (e: Exception) {
            if (isPersistedUser(cleanEmail)) {
                val localUser = getPersistedUser(cleanEmail)
                return checkPinMatch(localUser.securityPin, localUser)
            }

            val msg = e.message ?: ""
            if (msg.contains("PERMISSION_DENIED", ignoreCase = true) || msg.contains("permission", ignoreCase = true)) {
                return Result.failure(Exception("User ID not registered. Please sign up to create an account."))
            }

            return Result.failure(Exception("Unable to connect to server. Please check your internet connection and try again."))
        }
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

        setActiveUserEmail(cleanEmail, user?.uid)
        return Result.success(user)
    }

    suspend fun logout() {
        try {
            auth.signOut()
        } catch (_: Exception) { }
        clearActiveUserEmail()
    }
}
