package com.tecsup.hoopaxis.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tecsup.hoopaxis.ui.screens.DashboardScreen
import com.tecsup.hoopaxis.ui.screens.RuleDetailScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object RuleDetail : Screen("rule_detail/{ruleId}") {
        fun createRoute(ruleId: Int) = "rule_detail/$ruleId"
    }
}

@Composable
fun HoopAxisNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.RuleDetail.createRoute(ruleId))
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
