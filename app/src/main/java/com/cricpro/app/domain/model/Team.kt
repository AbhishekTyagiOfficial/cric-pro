package com.cricpro.app.domain.model

data class Team(
    val teamId: String = "",
    val teamName: String = "",
    val teamLogo: String = "",
    val ownerId: String = "",
    val captainId: String? = null,
    val viceCaptainId: String? = null,
    val players: List<Player> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
