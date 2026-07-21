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
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.tecsup.hoopaxis.data.model.Chapter
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.CircularProgress
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@Composable
fun ChaptersScreen(
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
                currentRoute = "capitulos",
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
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "NIVEL 2 — TODOS LOS MÓDULOS",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.TextSecondary,
                letterSpacing = 1.sp
            )
            
            Text(
                text = "${uiState.allChapters.size} Capítulos",
                style = MaterialTheme.typography.displayLarge
            )
            
            Text(
                text = "Biblioteca completa del Reglamento FIBA 2026",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            uiState.allChapters.forEach { chapter ->
                ChapterCard(
                    chapter = chapter,
                    onClick = { if (!chapter.isLocked) onNavigateToDetail(chapter.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            PublicityBanner()
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ChapterCard(chapter: Chapter, onClick: () -> Unit) {
    val color = when(chapter.ruleId % 4) {
        0 -> AppColors.Purple
        1 -> AppColors.Pink
        2 -> AppColors.Blue
        else -> AppColors.Gold
    }
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        categoryColor = color
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Estado de progreso
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                if (chapter.progress >= 1.0f) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.18f))
                            .border(1.dp, color.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    CircularProgress(progress = chapter.progress, categoryColor = color, size = 52.dp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(color.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "R${chapter.ruleId}-C${chapter.chapterNumber}",
                            color = color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    if (chapter.isLocked) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = AppColors.TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "${chapter.categoryName} · ${chapter.lessonsCount} lecciones",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = AppColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
