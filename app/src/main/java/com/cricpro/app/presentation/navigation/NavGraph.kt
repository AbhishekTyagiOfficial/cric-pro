package com.cricpro.app.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cricpro.app.presentation.auth.*
import com.cricpro.app.presentation.home.HomeScreen
import com.cricpro.app.presentation.match.*
import com.cricpro.app.presentation.search.SearchScreen
import com.cricpro.app.presentation.settings.AdminDashboardScreen
import com.cricpro.app.presentation.settings.NotificationsScreen
import com.cricpro.app.presentation.settings.SettingsScreen
import com.cricpro.app.presentation.team.TeamDetailScreen
import com.cricpro.app.presentation.team.TeamListScreen
import com.cricpro.app.presentation.tournament.TournamentListScreen

@Composable
fun CricProNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    var showAuthBottomSheet by remember { mutableStateOf(false) }
    var pendingGatedAction by remember { mutableStateOf("") }
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    fun navigateOrGate(actionName: String, route: String) {
        pendingGatedAction = actionName
        pendingRoute = route
        showAuthBottomSheet = true
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                onNavigateToEmailOtp = { navController.navigate(Screen.EmailOtpVerification.route) }
            )
        }

        composable(Screen.EmailOtpVerification.route) {
            EmailOtpVerificationScreen(
                onVerificationSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.SignUp.route) { inclusive = true } } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreateMatch = { navigateOrGate("Create & Score Match", Screen.CreateMatch.route) },
                onNavigateToTeams = { navController.navigate(Screen.Teams.route) },
                onNavigateToTournaments = { navController.navigate(Screen.Tournaments.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onMatchClick = { matchId -> navController.navigate(Screen.LiveScoring.createRoute(matchId)) }
            )
        }

        composable(Screen.Teams.route) {
            TeamListScreen(onNavigateToTeamDetail = { teamId -> navController.navigate(Screen.TeamDetail.createRoute(teamId)) })
        }

        composable(Screen.TeamDetail.route) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId") ?: ""
            TeamDetailScreen(teamId = teamId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.CreateMatch.route) {
            CreateMatchScreen(
                onMatchCreated = { matchId ->
                    navController.navigate(Screen.Toss.createRoute(matchId)) {
                        popUpTo(Screen.CreateMatch.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Toss.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            TossScreen(
                matchId = matchId,
                onTossComplete = {
                    navController.navigate(Screen.LiveScoring.createRoute(matchId)) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            )
        }

        composable(Screen.LiveScoring.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            LiveScoringScreen(
                matchId = matchId,
                onNavigateToHome = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onNavigateToScorecard = { navController.navigate(Screen.Scorecard.createRoute(matchId)) },
                onNavigateToAnalytics = { navController.navigate(Screen.MatchAnalytics.createRoute(matchId)) }
            )
        }

        composable(Screen.Scorecard.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            ScorecardScreen(matchId = matchId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.MatchAnalytics.route) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchAnalyticsScreen(matchId = matchId, onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Tournaments.route) {
            TournamentListScreen(onNavigateToTournamentDetail = { tourId -> navController.navigate(Screen.Tournaments.route) })
        }

        composable(Screen.Search.route) {
            SearchScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(onNavigateBack = { navController.popBackStack() })
        }
    }

    if (showAuthBottomSheet) {
        AuthBottomSheet(
            actionName = pendingGatedAction,
            onDismiss = { showAuthBottomSheet = false },
            onNavigateToLogin = {
                showAuthBottomSheet = false
                navController.navigate(Screen.Login.route)
            },
            onNavigateToSignUp = {
                showAuthBottomSheet = false
                navController.navigate(Screen.SignUp.route)
            },
            onGoogleSignIn = {
                showAuthBottomSheet = false
                pendingRoute?.let { navController.navigate(it) }
            }
        )
    }
}
