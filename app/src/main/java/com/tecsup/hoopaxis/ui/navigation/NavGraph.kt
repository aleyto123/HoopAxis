package com.tecsup.hoopaxis.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.ui.screens.ChaptersScreen
import com.tecsup.hoopaxis.ui.screens.DashboardScreen
import com.tecsup.hoopaxis.ui.screens.ProfileScreen
import com.tecsup.hoopaxis.ui.screens.RuleDetailScreen
import com.tecsup.hoopaxis.ui.screens.RulesScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Rules : Screen("rules")
    object Chapters : Screen("chapters")
    object Profile : Screen("profile")
    object RuleDetail : Screen("rule_detail/{ruleId}") {
        fun createRoute(ruleId: Int) = "rule_detail/$ruleId"
    }
}

@Composable
fun HoopAxisNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val user by repository.currentUser.collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            // Pantalla de carga silenciosa que verifica la persistencia
            LaunchedEffect(user) {
                if (user != null && user?.isLoggedIn == true) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    // Solo navegamos a Login si estamos seguros de que no hay usuario (tras un breve delay o carga)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }
        composable(Screen.Login.route) {
            com.tecsup.hoopaxis.ui.screens.LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.RuleDetail.createRoute(ruleId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route)
                },
                onNavigateToChapters = {
                    navController.navigate(Screen.Chapters.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Rules.route) {
            RulesScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.RuleDetail.createRoute(ruleId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route) {
                        popUpTo(Screen.Rules.route) { inclusive = true }
                    }
                },
                onNavigateToChapters = {
                    navController.navigate(Screen.Chapters.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Chapters.route) {
            ChaptersScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.RuleDetail.createRoute(ruleId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route)
                },
                onNavigateToChapters = {
                    navController.navigate(Screen.Chapters.route) {
                        popUpTo(Screen.Chapters.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route)
                },
                onNavigateToChapters = {
                    navController.navigate(Screen.Chapters.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.RuleDetail.route,
            arguments = listOf(navArgument("ruleId") { type = NavType.IntType })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getInt("ruleId") ?: 0
            RuleDetailScreen(
                ruleId = ruleId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
