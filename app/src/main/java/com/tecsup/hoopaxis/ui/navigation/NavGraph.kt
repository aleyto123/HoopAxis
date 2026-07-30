package com.tecsup.hoopaxis.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    object Articles : Screen("articulos/{ruleId}") {
        fun createRoute(ruleId: String) = "articulos/$ruleId"
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
    object Quiz : Screen("quiz/{articleId}") {
        fun createRoute(articleId: String) = "quiz/$articleId"
    }
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
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            LaunchedEffect(Unit) {
                // Sincronización remota única al iniciar la aplicación
                repository.syncFromRemote()
            }

            LaunchedEffect(user) {
                if (user != null && user?.isLoggedIn == true) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val restoredUser = User(
                            id = firebaseUser.uid,
                            name = firebaseUser.displayName ?: "Árbitro",
                            email = firebaseUser.email ?: "",
                            isLoggedIn = true
                        )
                        repository.login(restoredUser)
                    } else {
                        kotlinx.coroutines.delay(800)
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
            LoginScreen(
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
                    navController.navigate(Screen.Articles.createRoute(ruleId))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route)
                },
                onNavigateToArticles = {
                    navController.navigate(Screen.Articles.createRoute("all")) 
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                },
                onNavigateToChapterLessonList = { articleId, _, color ->
                    navController.navigate(Screen.Lesson.createRoute(articleId, color))
                }
            )
        }
        composable(Screen.Rules.route) {
            RulesScreen(
                onNavigateToDetail = { ruleId ->
                    navController.navigate(Screen.Articles.createRoute(ruleId))
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
                onNavigateToArticles = {
                    navController.navigate(Screen.Articles.createRoute("all"))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToAdmin = {
                    navController.navigate(Screen.Admin.route)
                }
            )
        }
        composable(Screen.Articles.route) { backStackEntry ->
            val ruleId = backStackEntry.arguments?.getString("ruleId")
            ArticlesScreen(
                ruleId = ruleId,
                onNavigateToDetail = { articleId, _, color ->
                    navController.navigate(Screen.Lesson.createRoute(articleId, color))
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToRules = {
                    navController.navigate(Screen.Rules.route)
                },
                onNavigateToArticles = {
                    navController.navigate(Screen.Articles.createRoute("all")) {
                        popUpTo(Screen.Articles.route) { inclusive = true }
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
                onNavigateToArticles = {
                    navController.navigate(Screen.Articles.createRoute("all"))
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
            LessonListScreen(navController, chapterId, chapterTitle, ruleColor)
        }
        
        composable(Screen.Lesson.route) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId")
            val ruleColor = backStackEntry.arguments?.getString("ruleColor")
            LessonScreen(navController, lessonId, ruleColor)
        }
        
        composable(Screen.Quiz.route) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getString("articleId")
            QuizScreen(navController, articleId)
        }
        
        composable(Screen.QuizResults.route) { backStackEntry ->
            val score = backStackEntry.arguments?.getString("score")
            val total = backStackEntry.arguments?.getString("total")
            QuizResultsScreen(navController, score, total)
        }
        
        composable(Screen.Admin.route) {
            AdminPanelScreen(onBack = { navController.popBackStack() })
        }
    }
}
