package com.tecsup.hoopaxis.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.User
import com.tecsup.hoopaxis.ui.screens.*
import com.tecsup.hoopaxis.viewmodel.AuthViewModel
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

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
    object Admin : Screen("admin_panel")
}

@Composable
fun HoopAxisNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val user by repository.currentUser.collectAsState(initial = null)
    
    val authViewModel: AuthViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repository) as T
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }
            LaunchedEffect(user) {
                if (user != null && user?.isLoggedIn == true) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    // Si Room no tiene usuario, verificamos Firebase para restaurar la sesión
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val restoredUser = User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "Árbitro",
                            email = firebaseUser.email ?: "",
                            isLoggedIn = true
                        )
                        repository.login(restoredUser)
                        // Al guardar en el repositorio, 'user' se actualizará y el LaunchedEffect se disparará de nuevo
                    } else {
                        // No hay sesión en Firebase tampoco
                        kotlinx.coroutines.delay(800) // Un poco más de tiempo para el Splash
                        if (user == null) {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
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
                    navController.navigate(Screen.Chapters.createRoute("all")) 
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                },
                onNavigateToChapterLessonList = { chapterId, title, color ->
                    navController.navigate(Screen.LessonList.createRoute(chapterId, title, color))
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
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
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
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
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
                    navController.navigate(Screen.Chapters.createRoute("all"))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
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
        
        composable(Screen.Admin.route) {
            AdminPanelScreen(onBack = { navController.popBackStack() })
        }
    }
}
