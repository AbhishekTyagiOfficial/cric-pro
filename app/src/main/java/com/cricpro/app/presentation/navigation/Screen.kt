package com.cricpro.app.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object EmailOtpVerification : Screen("email_otp")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Teams : Screen("teams")
    object TeamDetail : Screen("team_detail/{teamId}") {
        fun createRoute(teamId: String) = "team_detail/$teamId"
    }
    object PlayerProfile : Screen("player_profile/{playerId}") {
        fun createRoute(playerId: String) = "player_profile/$playerId"
    }
    object CreateMatch : Screen("create_match")
    object Toss : Screen("toss/{matchId}") {
        fun createRoute(matchId: String) = "toss/$matchId"
    }
    object LiveScoring : Screen("live_scoring/{matchId}") {
        fun createRoute(matchId: String) = "live_scoring/$matchId"
    }
    object Scorecard : Screen("scorecard/{matchId}") {
        fun createRoute(matchId: String) = "scorecard/$matchId"
    }
    object MatchAnalytics : Screen("analytics/{matchId}") {
        fun createRoute(matchId: String) = "analytics/$matchId"
    }
    object Tournaments : Screen("tournaments")
    object TournamentDetail : Screen("tournament_detail/{tournamentId}") {
        fun createRoute(tournamentId: String) = "tournament_detail/$tournamentId"
    }
    object Search : Screen("search")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object AdminDashboard : Screen("admin_dashboard")
}
