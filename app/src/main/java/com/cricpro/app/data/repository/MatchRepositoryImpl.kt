package com.cricpro.app.data.repository

import com.cricpro.app.data.local.dao.BallDao
import com.cricpro.app.data.local.dao.MatchDao
import com.cricpro.app.data.local.entity.BallEntity
import com.cricpro.app.data.local.entity.MatchEntity
import com.cricpro.app.data.remote.FirestoreService
import com.cricpro.app.domain.model.*
import com.cricpro.app.domain.repository.MatchRepository
import com.cricpro.app.domain.repository.ScoringRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

import com.cricpro.app.data.remote.FirebaseAuthService

class MatchRepositoryImpl @Inject constructor(
    private val matchDao: MatchDao,
    private val firestoreService: FirestoreService,
    private val authService: FirebaseAuthService
) : MatchRepository {

    private fun isMatchOwnedByUser(creatorId: String, currentUid: String): Boolean {
        if (creatorId.isBlank() || creatorId == "guest" || currentUid == "guest") return true
        if (creatorId == currentUid) return true
        val cleanCurrent = currentUid.lowercase().removePrefix("user_").removePrefix("google_")
        val cleanCreator = creatorId.lowercase().removePrefix("user_").removePrefix("google_")
        if (cleanCurrent == cleanCreator) return true
        val restoredCurrent = cleanCurrent.replace("_", ".")
        val restoredCreator = cleanCreator.replace("_", ".")
        if (restoredCurrent == restoredCreator || restoredCurrent.startsWith(restoredCreator) || restoredCreator.startsWith(restoredCurrent)) return true
        return false
    }

    override fun getRecentMatches(): Flow<List<Match>> {
        val currentUid = authService.currentUserId ?: "guest"
        return matchDao.getMatches().map { list ->
            list.filter { isMatchOwnedByUser(it.creatorId, currentUid) }.map { it.toDomain() }
        }
    }

    override fun getUpcomingMatches(): Flow<List<Match>> {
        val currentUid = authService.currentUserId ?: "guest"
        return matchDao.getMatches().map { list ->
            list.filter { isMatchOwnedByUser(it.creatorId, currentUid) && it.status == "SCHEDULED" }.map { it.toDomain() }
        }
    }

    override fun getCompletedMatches(): Flow<List<Match>> {
        val currentUid = authService.currentUserId ?: "guest"
        return matchDao.getCompletedMatches().map { list ->
            list.filter { isMatchOwnedByUser(it.creatorId, currentUid) }.map { it.toDomain() }
        }
    }

    override fun getMatchById(matchId: String): Flow<Match?> {
        return matchDao.getMatchById(matchId).map { it?.toDomain() }
    }

    override suspend fun createMatch(match: Match): Result<String> {
        return try {
            val matchId = if (match.matchId.isNotBlank()) match.matchId else "match_${System.currentTimeMillis()}"
            val currentCreator = match.creatorId.ifBlank { authService.currentUserId ?: "guest" }
            val finalMatch = match.copy(
                matchId = matchId,
                creatorId = currentCreator,
                firstInnings = Innings(inningsNumber = 1, battingTeamId = match.teamA.teamId, bowlingTeamId = match.teamB.teamId),
                secondInnings = Innings(inningsNumber = 2, battingTeamId = match.teamB.teamId, bowlingTeamId = match.teamA.teamId)
            )
            matchDao.insertMatch(finalMatch.toEntity())
            try { kotlinx.coroutines.withTimeoutOrNull(2000) { firestoreService.saveMatch(finalMatch) } } catch (e: Exception) {}
            Result.success(matchId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchToss(matchId: String, tossWinnerId: String, decision: TossDecision): Result<Unit> {
        return try {
            val matchEntity = matchDao.getMatchByIdDirect(matchId) ?: return Result.failure(Exception("Match not found"))
            val currentMatch = matchEntity.toDomain()

            val isWinnerTeamA = tossWinnerId == currentMatch.teamA.teamId ||
                (currentMatch.teamA.teamName.isNotBlank() && tossWinnerId.equals(currentMatch.teamA.teamName, ignoreCase = true))

            val isBattingTeamA = if (decision == TossDecision.BAT) isWinnerTeamA else !isWinnerTeamA

            val battingTeamId = if (isBattingTeamA) currentMatch.teamA.teamId else currentMatch.teamB.teamId
            val bowlingTeamId = if (isBattingTeamA) currentMatch.teamB.teamId else currentMatch.teamA.teamId

            val updatedMatch = currentMatch.copy(
                tossWinnerId = tossWinnerId,
                tossDecision = decision,
                status = MatchStatus.IN_PROGRESS,
                firstInnings = Innings(inningsNumber = 1, battingTeamId = battingTeamId, bowlingTeamId = bowlingTeamId),
                secondInnings = Innings(inningsNumber = 2, battingTeamId = bowlingTeamId, bowlingTeamId = battingTeamId),
                updatedAt = System.currentTimeMillis()
            )

            matchDao.insertMatch(updatedMatch.toEntity())
            try { kotlinx.coroutines.withTimeoutOrNull(2000) { firestoreService.saveMatch(updatedMatch) } } catch (e: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlayingXI(matchId: String, playingXIA: List<String>, playingXIB: List<String>): Result<Unit> {
        return try {
            val matchEntity = matchDao.getMatchByIdDirect(matchId) ?: return Result.failure(Exception("Match not found"))
            val currentMatch = matchEntity.toDomain()
            val updated = currentMatch.copy(playingXI_A = playingXIA, playingXI_B = playingXIB, updatedAt = System.currentTimeMillis())
            matchDao.insertMatch(updated.toEntity())
            firestoreService.saveMatch(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateMatchStatus(matchId: String, status: String): Result<Unit> {
        return try {
            val matchEntity = matchDao.getMatchByIdDirect(matchId) ?: return Result.failure(Exception("Match not found"))
            val currentMatch = matchEntity.toDomain()
            val mStatus = try { MatchStatus.valueOf(status) } catch (e: Exception) { MatchStatus.IN_PROGRESS }
            val updated = currentMatch.copy(status = mStatus, updatedAt = System.currentTimeMillis())
            matchDao.insertMatch(updated.toEntity())
            firestoreService.saveMatch(updated)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMatches(query: String): Flow<List<Match>> {
        return matchDao.getMatches().map { list ->
            list.filter { it.title.contains(query, ignoreCase = true) || it.groundName.contains(query, ignoreCase = true) }
                .map { it.toDomain() }
        }
    }
}

fun Innings.toJsonString(): String {
    val targetStr = if (target != null) "\"target\":$target," else ""
    return "{\"inningsNumber\":$inningsNumber,\"battingTeamId\":\"$battingTeamId\",\"bowlingTeamId\":\"$bowlingTeamId\",\"totalRuns\":$totalRuns,\"wickets\":$wickets,\"legalBallsBowled\":$legalBallsBowled,$targetStr\"isCompleted\":$isCompleted}"
}

fun parseInningsFromJson(jsonStr: String): Innings? {
    if (jsonStr.isBlank()) return null
    return try {
        val root = JSONObject(jsonStr)
        Innings(
            inningsNumber = root.optInt("inningsNumber", 1),
            battingTeamId = root.optString("battingTeamId", ""),
            bowlingTeamId = root.optString("bowlingTeamId", ""),
            totalRuns = root.optInt("totalRuns", 0),
            wickets = root.optInt("wickets", 0),
            legalBallsBowled = root.optInt("legalBallsBowled", 0),
            target = if (root.has("target") && !root.isNull("target")) root.optInt("target") else null,
            isCompleted = root.optBoolean("isCompleted", false)
        )
    } catch (_: Exception) {
        fun extractInt(key: String): Int? {
            val regex = "\"$key\":\\s*(\\d+)".toRegex()
            return regex.find(jsonStr)?.groupValues?.get(1)?.toIntOrNull()
        }
        fun extractString(key: String): String {
            val regex = "\"$key\":\\s*\"([^\"]*)\"".toRegex()
            return regex.find(jsonStr)?.groupValues?.get(1) ?: ""
        }
        fun extractBool(key: String): Boolean {
            val regex = "\"$key\":\\s*(true|false)".toRegex()
            return regex.find(jsonStr)?.groupValues?.get(1)?.toBooleanStrictOrNull() ?: false
        }

        Innings(
            inningsNumber = extractInt("inningsNumber") ?: 1,
            battingTeamId = extractString("battingTeamId"),
            bowlingTeamId = extractString("bowlingTeamId"),
            totalRuns = extractInt("totalRuns") ?: 0,
            wickets = extractInt("wickets") ?: 0,
            legalBallsBowled = extractInt("legalBallsBowled") ?: 0,
            target = extractInt("target"),
            isCompleted = extractBool("isCompleted")
        )
    }
}

fun Match.toEntity(): MatchEntity {
    val inn1Str = firstInnings?.toJsonString() ?: "{}"
    val inn2Str = secondInnings?.toJsonString() ?: "{}"
    val sStr = currentStrikerId ?: ""
    val nsStr = currentNonStrikerId ?: ""
    val bStr = currentBowlerId ?: ""
    val fullJson = "{\"firstInnings\":$inn1Str,\"secondInnings\":$inn2Str,\"striker\":\"$sStr\",\"nonStriker\":\"$nsStr\",\"bowler\":\"$bStr\"}"

    return MatchEntity(
        matchId = matchId,
        tournamentId = tournamentId,
        title = title,
        matchType = matchType.name,
        totalOvers = totalOvers,
        groundName = groundName,
        matchDate = matchDate,
        teamAId = teamA.teamId,
        teamAName = teamA.teamName,
        teamBId = teamB.teamId,
        teamBName = teamB.teamName,
        tossWinnerId = tossWinnerId,
        tossDecision = tossDecision?.name,
        status = status.name,
        currentInningsNumber = currentInningsNumber,
        resultMessage = resultMessage,
        winnerTeamId = winnerTeamId,
        creatorId = creatorId,
        scorerId = scorerId,
        matchJson = fullJson
    )
}

fun MatchEntity.toDomain(): Match {
    val isWinnerTeamA = tossWinnerId == teamAId || (teamAName.isNotBlank() && tossWinnerId.equals(teamAName, ignoreCase = true))
    val isBattingTeamA = if (tossDecision?.uppercase() == "BAT") isWinnerTeamA else !isWinnerTeamA

    val battingA = if (isBattingTeamA) teamAId else teamBId
    val bowlingA = if (isBattingTeamA) teamBId else teamAId

    var inn1: Innings? = null
    var inn2: Innings? = null
    var striker: String? = null
    var nonStriker: String? = null
    var bowler: String? = null

    if (matchJson.isNotBlank()) {
        try {
            val root = JSONObject(matchJson)
            if (root.has("firstInnings") && !root.isNull("firstInnings")) {
                val inn1Obj = root.optJSONObject("firstInnings")
                if (inn1Obj != null) inn1 = parseInningsFromJson(inn1Obj.toString())
            }
            if (root.has("secondInnings") && !root.isNull("secondInnings")) {
                val inn2Obj = root.optJSONObject("secondInnings")
                if (inn2Obj != null) inn2 = parseInningsFromJson(inn2Obj.toString())
            }
            striker = root.optString("striker", "").ifEmpty { null }
            nonStriker = root.optString("nonStriker", "").ifEmpty { null }
            bowler = root.optString("bowler", "").ifEmpty { null }
        } catch (_: Exception) {
            val inn1Regex = "\"firstInnings\":(\\{[^}]*\\})".toRegex()
            val inn2Regex = "\"secondInnings\":(\\{[^}]*\\})".toRegex()
            val strikerRegex = "\"striker\":\"([^\"]*)\"".toRegex()
            val nonStrikerRegex = "\"nonStriker\":\"([^\"]*)\"".toRegex()
            val bowlerRegex = "\"bowler\":\"([^\"]*)\"".toRegex()

            val inn1Match = inn1Regex.find(matchJson)?.groupValues?.get(1)
            val inn2Match = inn2Regex.find(matchJson)?.groupValues?.get(1)

            if (inn1Match != null && inn1Match != "{}") inn1 = parseInningsFromJson(inn1Match)
            if (inn2Match != null && inn2Match != "{}") inn2 = parseInningsFromJson(inn2Match)

            striker = strikerRegex.find(matchJson)?.groupValues?.get(1)?.ifEmpty { null }
            nonStriker = nonStrikerRegex.find(matchJson)?.groupValues?.get(1)?.ifEmpty { null }
            bowler = bowlerRegex.find(matchJson)?.groupValues?.get(1)?.ifEmpty { null }
        }
    }

    if (inn1 == null) {
        inn1 = Innings(1, battingA, bowlingA)
    } else if (inn1.battingTeamId.isBlank()) {
        inn1 = inn1.copy(battingTeamId = battingA, bowlingTeamId = bowlingA)
    }

    if (inn2 == null) {
        inn2 = Innings(2, bowlingA, battingA)
    } else if (inn2.battingTeamId.isBlank()) {
        inn2 = inn2.copy(battingTeamId = bowlingA, bowlingTeamId = battingA)
    }

    return Match(
        matchId = matchId,
        tournamentId = tournamentId,
        title = title,
        matchType = try { MatchType.valueOf(matchType) } catch (e: Exception) { MatchType.T20 },
        totalOvers = totalOvers,
        groundName = groundName,
        matchDate = matchDate,
        teamA = Team(teamId = teamAId, teamName = teamAName),
        teamB = Team(teamId = teamBId, teamName = teamBName),
        tossWinnerId = tossWinnerId,
        tossDecision = tossDecision?.let { try { TossDecision.valueOf(it) } catch (e: Exception) { null } },
        status = try { MatchStatus.valueOf(status) } catch (e: Exception) { MatchStatus.SCHEDULED },
        currentInningsNumber = currentInningsNumber,
        firstInnings = inn1,
        secondInnings = inn2,
        currentStrikerId = striker,
        currentNonStrikerId = nonStriker,
        currentBowlerId = bowler,
        resultMessage = resultMessage,
        winnerTeamId = winnerTeamId,
        creatorId = creatorId,
        scorerId = scorerId
    )
}

class ScoringRepositoryImpl @Inject constructor(
    private val ballDao: BallDao,
    private val matchDao: MatchDao,
    private val firestoreService: FirestoreService
) : ScoringRepository {

    override fun getBallsForMatch(matchId: String, inningsNumber: Int): Flow<List<Ball>> {
        return ballDao.getBallsForInnings(matchId, inningsNumber).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun scoreBall(matchId: String, ball: Ball): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val ballEntity = ball.toEntity(matchId)
            ballDao.insertBall(ballEntity)
            try {
                firestoreService.saveBall(matchId, ball)
            } catch (ignored: Exception) {
                // Graceful offline fallback
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun undoLastBall(matchId: String, inningsNumber: Int): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ballDao.deleteLastBall(matchId, inningsNumber)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editBall(matchId: String, updatedBall: Ball): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ballDao.insertBall(updatedBall.toEntity(matchId))
            try {
                firestoreService.saveBall(matchId, updatedBall)
            } catch (ignored: Exception) {}
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteBall(matchId: String, ballId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            ballDao.deleteBall(ballId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncInningsState(matchId: String, match: Match): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            matchDao.insertMatch(match.toEntity())
            try {
                firestoreService.saveMatch(match)
            } catch (ignored: Exception) {
                // Graceful offline fallback
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun BallEntity.toDomain(): Ball {
        return Ball(
            ballId = ballId,
            matchId = matchId,
            inningsNumber = inningsNumber,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            strikerId = strikerId,
            nonStrikerId = nonStrikerId,
            bowlerId = bowlerId,
            runsScored = runsScored,
            extraType = try { ExtraType.valueOf(extraType) } catch (e: Exception) { ExtraType.NONE },
            extraRuns = extraRuns,
            isLegalDelivery = isLegalDelivery,
            wicketType = try { WicketType.valueOf(wicketType) } catch (e: Exception) { WicketType.NONE },
            dismissedPlayerId = dismissedPlayerId,
            timestamp = timestamp
        )
    }

    private fun Ball.toEntity(mId: String): BallEntity {
        return BallEntity(
            ballId = if (ballId.isNotBlank()) ballId else "ball_${System.currentTimeMillis()}",
            matchId = mId,
            inningsNumber = inningsNumber,
            overNumber = overNumber,
            ballNumberInOver = ballNumberInOver,
            strikerId = strikerId,
            nonStrikerId = nonStrikerId,
            bowlerId = bowlerId,
            runsScored = runsScored,
            extraType = extraType.name,
            extraRuns = extraRuns,
            isLegalDelivery = isLegalDelivery,
            wicketType = wicketType.name,
            dismissedPlayerId = dismissedPlayerId,
            timestamp = timestamp
        )
    }
}
