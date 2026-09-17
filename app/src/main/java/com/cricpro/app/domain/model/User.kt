package com.cricpro.app.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImage: String = "",
    val role: String = "user", // "user" or "admin"
    val primaryRole: String = "Batter", // Batter, Bowler, All-Rounder, Wicket-Keeper
    val battingStyle: String = "Right-Hand Bat",
    val bowlingStyle: String = "Right-arm Medium",
    val city: String = "",
    val state: String = "",
    val securityPin: String = "",
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val isVerified: Boolean = false
)
