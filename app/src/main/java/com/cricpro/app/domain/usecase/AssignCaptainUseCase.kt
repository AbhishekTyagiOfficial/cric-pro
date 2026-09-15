package com.cricpro.app.domain.usecase

import com.cricpro.app.domain.repository.TeamRepository
import javax.inject.Inject

class AssignCaptainUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamId: String, playerId: String): Result<Unit> {
        return teamRepository.assignCaptain(teamId, playerId)
    }
}

class AssignViceCaptainUseCase @Inject constructor(
    private val teamRepository: TeamRepository
) {
    suspend operator fun invoke(teamId: String, playerId: String): Result<Unit> {
        return teamRepository.assignViceCaptain(teamId, playerId)
    }
}
