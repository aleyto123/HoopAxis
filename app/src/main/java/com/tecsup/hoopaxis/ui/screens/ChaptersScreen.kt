package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.CircularProgress
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@Composable
fun ArticlesScreen(
    ruleId: String?,
    onNavigateToDetail: (String, String, String) -> Unit = { _, _, _ -> },
    onNavigateToHome: () -> Unit = {},
    onNavigateToRules: () -> Unit = {},
    onNavigateToArticles: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val articles = uiState.filteredArticles
    
    val rule = uiState.rules.find { it.id == ruleId }

    LaunchedEffect(ruleId) {
        ruleId?.let { viewModel.selectRule(it) }
    }

    Crossfade(targetState = uiState.isLoading, animationSpec = tween(300)) { loading ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingPulse()
            }
        } else {
            Scaffold(
                bottomBar = { 
                    BottomNavBar(
                        currentRoute = "articulos",
                        onHomeClick = onNavigateToHome,
                        onRulesClick = onNavigateToRules,
                        onArticlesClick = onNavigateToArticles,
                        onProfileClick = onNavigateToProfile
                    ) 
                },
                containerColor = Color.Transparent
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(
                                text = if (ruleId == "all" || ruleId == null) "TODOS LOS ARTÍCULOS" else "REGLA ${rule?.number ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                color = AppColors.TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = if (ruleId == "all" || ruleId == null) "${articles.size} Artículos" else rule?.title ?: "Artículos",
                                style = MaterialTheme.typography.displayLarge
                            )
                        }
                    }

                    if (uiState.user?.isAdmin == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                        ) {
                            Icon(Icons.Default.Settings, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ADMINISTRAR")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    articles.forEach { article ->
                        val articleColor = Color(android.graphics.Color.parseColor(article.color))
                        ChaptersArticleCard(
                            article = article,
                            ruleColor = articleColor,
                            onClick = { 
                                onNavigateToDetail(article.id, article.title, article.color.removePrefix("#"))
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


@Composable
private fun ChaptersArticleCard(article: Article, ruleColor: Color, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        categoryColor = ruleColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado Izquierdo: Badge con número (Estilo pantalla Reglas)
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ruleColor.copy(alpha = 0.18f))
                    .border(1.dp, ruleColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A${article.sortOrder}",
                    color = ruleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Centro: Información
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = article.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Badge de "Artículo X"
                Text(
                    text = article.articleNumber,
                    color = ruleColor,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ruleColor.copy(0.18f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Lado Derecho: Progreso Circular (Estilo pantalla Reglas)
            CircularProgress(progress = article.progress, categoryColor = ruleColor)
        }
    }
}
