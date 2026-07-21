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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.Notifications
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
import com.tecsup.hoopaxis.data.model.RecentChapter
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.CircularProgress
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToRules: () -> Unit = {},
    onNavigateToChapters: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
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
                onChaptersClick = onNavigateToChapters,
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
            
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = AppColors.Blue,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            ProgressCard()
            Spacer(modifier = Modifier.height(24.dp))
            ProBanner()
            Spacer(modifier = Modifier.height(24.dp))
            CategorySection(
                categories = uiState.categories,
                onCategoryClick = onNavigateToDetail
            )
            Spacer(modifier = Modifier.height(32.dp))
            ContinueStudyingSection(uiState.recentChapters)
            Spacer(modifier = Modifier.height(32.dp))
            PublicityBanner()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun Header(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
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
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProgressCard() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                Text(
                    text = "3 de 16 capítulos completados",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    StatusBadge(text = "3 COMPLETADOS", color = AppColors.Green, icon = Icons.Default.Check)
                    Spacer(modifier = Modifier.width(10.dp))
                    StatusBadge(text = "4 EN CURSO", color = AppColors.Gold, icon = Icons.Default.Pause)
                }
            }
            CircularProgress(progress = 0.31f, categoryColor = AppColors.Purple)
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color, icon: ImageVector) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.18f), RoundedCornerShape(50))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun ProBanner() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AppColors.Gold.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = AppColors.Gold,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modo Árbitro Pro",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "Artículos FIBA oficiales · Sin anuncios · Exámenes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Gold)
                    .clickable { }
                    .padding(horizontal = 22.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "VER", color = AppColors.Background, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun CategorySection(
    categories: List<RuleCategory>,
    onCategoryClick: (Int) -> Unit
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
            TextButton(onClick = { }) {
                Text(text = "Ver todas →", color = AppColors.Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        categories.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowItems.forEachIndexed { index, item ->
                    val color = when(item.id % 4) {
                        0 -> AppColors.Purple
                        1 -> AppColors.Pink
                        2 -> AppColors.Blue
                        else -> AppColors.Gold
                    }
                    CategoryCard(
                        item = item,
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
    item: RuleCategory,
    categoryColor: Color,
    modifier: Modifier = Modifier,
    onCategoryClick: (Int) -> Unit
) {
    GlassCard(
        modifier = modifier
            .height(160.dp)
            .clickable { onCategoryClick(item.id) },
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
                    Text(text = item.iconEmoji, fontSize = 24.sp)
                }
                CircularProgress(progress = item.progress, categoryColor = categoryColor)
            }
            
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.chaptersCount} capítulos",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ContinueStudyingSection(recentChapters: List<RecentChapter>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CONTINÚA ESTUDIANDO",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary
            )
            TextButton(onClick = { }) {
                Text(text = "Todos →", color = AppColors.Purple, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        if (recentChapters.isEmpty()) {
            Text(
                text = "Inicia una lección para ver tu progreso",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            recentChapters.forEach { chapter ->
                RecentChapterCard(chapter)
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun RecentChapterCard(chapter: RecentChapter) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = chapter.iconEmoji, fontSize = 22.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = chapter.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Text(
                        text = chapter.categoryName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            CircularProgress(progress = chapter.progress, categoryColor = AppColors.Purple)
        }
    }
}

@Composable
fun PublicityBanner() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PUBLICIDAD",
                    style = MaterialTheme.typography.labelSmall,
                    color = AppColors.TextMuted
                )
                Text(
                    text = "Elimina los anuncios con Árbitro Pro →",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = AppColors.Gold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
