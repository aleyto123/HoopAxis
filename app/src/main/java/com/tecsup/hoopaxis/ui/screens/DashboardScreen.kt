package com.tecsup.hoopaxis.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.model.Rule
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.CircularProgress
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel
import com.tecsup.hoopaxis.viewmodel.DashboardUiState

@Composable
fun DashboardScreen(
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRules: () -> Unit = {},
    onNavigateToArticles: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onNavigateToChapterLessonList: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { 
            BottomNavBar(
                currentRoute = "inicio",
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
            Spacer(modifier = Modifier.height(24.dp))
            Header(userName = uiState.user?.name ?: "Árbitro")
            
            if (uiState.user?.isAdmin == true) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                ) {
                    Icon(Icons.Default.Settings, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PANEL DE ADMINISTRADOR")
                }
            }
            
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = AppColors.Blue,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            ProgressCard(uiState)
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(
                rules = uiState.rules,
                onCategoryClick = onNavigateToDetail,
                onViewAllClick = onNavigateToRules
            )
            Spacer(modifier = Modifier.height(32.dp))
            ContinueStudyingSection(
                articles = uiState.allArticles.filter { it.progress > 0 && it.progress < 1 }.take(3),
                rules = uiState.rules,
                onArticleClick = { article ->
                    val rule = uiState.rules.find { it.id == article.ruleId }
                    onNavigateToChapterLessonList(
                        article.id, 
                        article.title, 
                        rule?.color?.removePrefix("#") ?: "C96BFF"
                    )
                },
                onViewAllClick = onNavigateToArticles
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun Header(userName: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Buenos días",
            style = MaterialTheme.typography.bodyMedium
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = userName,
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "🏀", fontSize = 28.sp)
        }
    }
}

@Composable
fun ProgressCard(uiState: DashboardUiState) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "TU PROGRESO GENERAL",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextSecondary,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reglamento FIBA 2026",
                    style = MaterialTheme.typography.headlineMedium
                )
                val completedArticles = uiState.allArticles.count { it.isCompleted }
                Text(
                    text = "$completedArticles de 50 artículos completados",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(text = "$completedArticles COMPLETADOS", color = AppColors.Green, icon = Icons.Default.Check)
                    val inProgress = uiState.allArticles.count { it.progress > 0 && it.progress < 1 }
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(text = "$inProgress EN CURSO", color = AppColors.Gold, icon = Icons.Default.Pause)
                }
            }
            Box(modifier = Modifier.padding(top = 10.dp, start = 8.dp)) {
                val overallProgress = if (uiState.allArticles.isEmpty()) 0f 
                                      else uiState.allArticles.sumOf { it.progress.toDouble() }.toFloat() / uiState.allArticles.size
                CircularProgress(progress = overallProgress, categoryColor = AppColors.Purple, size = 64.dp)
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color, icon: ImageVector) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(50))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text.uppercase(),
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}


@Composable
fun CategorySection(
    rules: List<Rule>,
    onCategoryClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "8 REGLAS PRINCIPALES",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary
            )
            TextButton(onClick = onViewAllClick) {
                Text(text = "Ver todas →", color = AppColors.Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        rules.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEachIndexed { index, item ->
                    val color = Color(android.graphics.Color.parseColor(item.color))
                    CategoryCard(
                        rule = item,
                        categoryColor = color,
                        modifier = Modifier.weight(1f),
                        onCategoryClick = onCategoryClick
                    )
                    if (index < rowItems.size - 1) {
                        Spacer(modifier = Modifier.width(18.dp))
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
fun CategoryCard(
    rule: Rule,
    categoryColor: Color,
    modifier: Modifier = Modifier,
    onCategoryClick: (String) -> Unit
) {
    GlassCard(
        modifier = modifier
            .height(175.dp)
            .clickable { onCategoryClick(rule.id) },
        categoryColor = categoryColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = rule.emoji, fontSize = 24.sp)
                }
                CircularProgress(progress = rule.progress, categoryColor = categoryColor)
            }
            
            Column {
                Text(
                    text = rule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${rule.chaptersCount} artículos",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ContinueStudyingSection(
    articles: List<Article>,
    rules: List<Rule>,
    onArticleClick: (Article) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONTINÚA ESTUDIANDO",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary,
                letterSpacing = 1.2.sp
            )
            TextButton(onClick = onViewAllClick) {
                Text(text = "Todos →", color = AppColors.Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        if (articles.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Aún no has empezado ningún artículo. ¡Comienza ahora!",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            articles.forEach { article ->
                val rule = rules.find { it.id == article.ruleId }
                ContinueArticleCard(
                    article = article,
                    ruleName = rule?.title ?: "",
                    ruleColor = Color(android.graphics.Color.parseColor(rule?.color ?: "#C96BFF")),
                    onClick = { onArticleClick(article) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ContinueArticleCard(
    article: Article,
    ruleName: String,
    ruleColor: Color,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = article.emoji, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "${article.articleNumber} — ${article.title}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = ruleName,
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.TextSecondary
                    )
                }
            }
            CircularProgress(progress = article.progress, categoryColor = ruleColor, size = 44.dp)
        }
    }
}
