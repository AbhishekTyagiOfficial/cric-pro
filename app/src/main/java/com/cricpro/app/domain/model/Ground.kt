package com.cricpro.app.domain.model

data class Ground(
    val groundId: String = "",
    val name: String = "",
    val location: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val creatorId: String = ""
)

data class NotificationItem(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "GENERAL", // MATCH, TOURNAMENT, TEAM, GENERAL
    val targetId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
