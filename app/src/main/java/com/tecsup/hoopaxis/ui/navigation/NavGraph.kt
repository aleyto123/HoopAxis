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
    object Chapters : Screen("chapters/{ruleId}") {
        fun createRoute(ruleId: String) = "chapters/$ruleId"
    }
    object Profile : Screen("profile")
    object RuleDetail : Screen("rule_detail/{ruleId}") {
        fun createRoute(ruleId: String) = "rule_detail/$ruleId"
    }
    object LessonList : Screen("lesson_list/{chapterId}/{chapterTitle}/{ruleColor}") {
        fun createRoute(chapterId: String, chapterTitle: String, ruleColor: String) = 
            "lesson_list/$chapterId/$chapterTitle/$ruleColor"
    }
    object Lesson : Screen("lesson/{lessonId}/{ruleColor}") {
        fun createRoute(lessonId: String, ruleColor: String) = "lesson/$lessonId/$ruleColor"
    }
    object Quiz : Screen("quiz")
    object QuizResults : Screen("quiz_results/{score}/{total}") {
        fun createRoute(score: Int, total: Int) = "quiz_results/$score/$total"
    }
    object Pro : Screen("pro_screen")
    object PaymentSuccess : Screen("payment_success")
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
            LaunchedEffect(user) {
                if (user != null && user?.isLoggedIn == true) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
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
                    navController.navigate(Screen.Chapters.createRoute(ruleId))
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
                    // For global chapters view, we might need a default or another screen
                    navController.navigate(Screen.Chapters.createRoute("all")) 
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Rules.route) {
            RulesScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.Chapters.createRoute(ruleId))
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
                    navController.navigate(Screen.Chapters.createRoute("all"))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        composable(Screen.Chapters.route) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId")
            ChaptersScreen(
                ruleId = ruleId,
                onNavigateToDetail = { chapterId, title, color ->
                    navController.navigate(Screen.LessonList.createRoute(chapterId, title, color))
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
                    navController.navigate(Screen.Chapters.createRoute("all")) {
                        popUpTo(Screen.Chapters.route) { inclusive = true }
                    }
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onBack = { navController.popBackStack() }
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
            arguments = listOf(navArgument("ruleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId") ?: ""
            RuleDetailScreen(
                ruleId = ruleId,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.LessonList.route) { backStackEntry ->
            val chapterId = backStackEntry.arguments?.getString("chapterId")
            val chapterTitle = backStackEntry.arguments?.getString("chapterTitle")
            val ruleColor = backStackEntry.arguments?.getString("ruleColor")
            com.tecsup.hoopaxis.ui.screens.LessonListScreen(navController, chapterId, chapterTitle, ruleColor)
        }
        
        composable(Screen.Lesson.route) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            val ruleColor = backStackEntry.arguments?.getString("ruleColor")
            com.tecsup.hoopaxis.ui.screens.LessonScreen(navController, lessonId, ruleColor)
        }
        
        composable(Screen.Quiz.route) {
            com.tecsup.hoopaxis.ui.screens.QuizScreen(navController)
        }
        
        composable(Screen.QuizResults.route) { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")
            val total = backStackEntry.arguments?.getString("total")
            com.tecsup.hoopaxis.ui.screens.QuizResultsScreen(navController, score, total)
        }
        
        composable(Screen.Pro.route) {
            com.tecsup.hoopaxis.ui.screens.ProScreen(navController)
        }
        
        composable(Screen.PaymentSuccess.route) {
            com.tecsup.hoopaxis.ui.screens.PaymentSuccessScreen(navController)
        }
    }
}
