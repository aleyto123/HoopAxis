package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.data.repository.UserRepository
import com.tecsup.hoopaxis.ui.components.AdBannerComponent
import com.tecsup.hoopaxis.ui.theme.AppColors
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    navController: NavController,
    chapterId: String?,
    chapterTitle: String?,
    ruleColorHex: String?
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )
    
    val articles by viewModel.articles.collectAsState()
    val ruleColor = Color(android.graphics.Color.parseColor("#${ruleColorHex ?: "C96BFF"}"))
    val isPro = UserRepository.isProUser

    LaunchedEffect(chapterId) {
        chapterId?.let { viewModel.selectChapter(it) }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally { it / 3 }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = chapterTitle ?: "Artículos",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ruleColor.copy(alpha = 0.18f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Capítulo ${chapterId?.removePrefix("c") ?: ""}",
                                    color = ruleColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                // Chapter Progress
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .fillMaxHeight()
                            .background(ruleColor, RoundedCornerShape(50))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(articles) { article ->
                        val isLocked = article.isProOnly && !isPro
                        ArticleCard(
                            article = article,
                            ruleColor = ruleColor,
                            isLocked = isLocked,
                            onClick = {
                                if (isLocked) {
                                    navController.navigate("pro_screen")
                                } else {
                                    navController.navigate("lesson/${article.id}/${ruleColorHex}")
                                }
                            }
                        )
                    }

                    if (!isPro) {
                        item {
                            AdBannerComponent(onClick = { navController.navigate("pro_screen") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleCard(
    article: Article,
    ruleColor: Color,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = ruleColor.copy(alpha = 0.3f),
                ambientColor = ruleColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = if (isLocked) 0.05f else 0.10f),
                        Color.White.copy(alpha = if (isLocked) 0.03f else 0.05f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = if (isLocked) Color(0xFFFFD166).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.13f),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.drawWithContent {
                drawContent()
                if (isLocked) {
                    drawRect(color = Color.Black.copy(alpha = 0.35f))
                }
            }
        ) {
            // Left Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLocked) Color(0xFFFFD166).copy(alpha = 0.15f) else ruleColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFFFFD166), modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        text = "${article.articleNumber.last()}", // Demo showing last digit
                        color = ruleColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Center Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = article.articleNumber,
                    color = ruleColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isLocked) {
                    Text(
                        text = "Solo Pro · Desbloquear →",
                        color = Color(0xFFFFD166),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right Icon
            if (isLocked) {
                Icon(Icons.Default.Star, null, tint = Color(0xFFFFD166), modifier = Modifier.size(18.dp))
            } else {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White.copy(alpha = 0.25f))
            }
        }
    }
}
