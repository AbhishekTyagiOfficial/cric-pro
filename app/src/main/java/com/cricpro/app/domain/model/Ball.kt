package com.cricpro.app.domain.model

import com.google.firebase.firestore.Exclude

enum class ExtraType {
    NONE,
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE
}

enum class WicketType {
    NONE,
    BOWLED,
    CAUGHT,
    LBW,
    RUN_OUT,
    STUMPED,
    HIT_WICKET,
    RETIRED_OUT
}

data class WagonWheelPoint(
    val angleDegrees: Float = 0f, // 0 to 360 degrees
    val distanceFraction: Float = 0f // 0.0 to 1.0 (distance from pitch center to boundary)
)

data class Ball(
    val ballId: String = "",
    val matchId: String = "",
    val inningsNumber: Int = 1,
    val overNumber: Int = 0, // 0-indexed over
    val ballNumberInOver: Int = 1, // 1 to 6 (for legal deliveries)
    val totalLegalBallsInInnings: Int = 0,
    val strikerId: String = "",
    val nonStrikerId: String = "",
    val bowlerId: String = "",
    val runsScored: Int = 0, // 0, 1, 2, 3, 4, 5, 6
    val extraType: ExtraType = ExtraType.NONE,
    val extraRuns: Int = 0, // e.g. 1 for wide/no-ball plus any extra runs
    val isLegalDelivery: Boolean = true,
    val wicketType: WicketType = WicketType.NONE,
    val dismissedPlayerId: String? = null,
    val fielderId: String? = null,
    val wagonWheel: WagonWheelPoint? = null,
    val commentary: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    @get:Exclude
    val totalRunsOnBall: Int
        get() = runsScored + extraRuns
}
