package com.cricpro.app.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImage: String = "",
    val role: String = "user", // "user" or "admin"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val isVerified: Boolean = false
)
