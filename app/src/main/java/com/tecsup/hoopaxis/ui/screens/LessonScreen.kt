package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.Article
import com.tecsup.hoopaxis.ui.theme.AppColors
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: String?,
    ruleColorHex: String?
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )

    var article by remember { mutableStateOf<Article?>(null) }
    val ruleColor = Color(android.graphics.Color.parseColor("#${ruleColorHex ?: "C96BFF"}"))
    
    val scrollState = rememberScrollState()

    // Flag para saber si el usuario ya pasó el quiz de esta lección
    val quizPassedState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("quiz_passed", false)
        ?.collectAsState()
    
    val quizPassed = quizPassedState?.value ?: false

    LaunchedEffect(lessonId) {
        lessonId?.let { article = viewModel.getArticle(it) }
    }

    LaunchedEffect(quizPassed) {
        if (quizPassed) {
            article?.let {
                viewModel.completeArticle(it)
            }
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInHorizontally { it / 3 }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Artículo",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(ruleColor)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(article?.articleNumber ?: "", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
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
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState)
            ) {
                article?.let { art ->
                    // PARAPHRASE CARD
                    ParaphraseCard(ruleColor = ruleColor, article = art)

                    Spacer(modifier = Modifier.height(24.dp))

                    // FIBA EXTERNAL LINK BUTTON
                    FibaExternalLinkButton(
                        ruleColor = ruleColor,
                        onClick = {
                            uriHandler.openUri("https://about.fiba.basketball/es/services/resource-hub/downloads")
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTTOM ACTION (PREMIUM CHALLENGE BUTTON)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = if (quizPassed) AppColors.Purple else Color(0xFF00F2FF)
                            )
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = if (quizPassed) listOf(AppColors.Purple, AppColors.Pink)
                                             else listOf(Color(0xFF00F2FF), Color(0xFFC96BFF), Color(0xFFFF6B9D)) // Cyan to Purple to Pink
                                )
                            )
                            .border(
                                width = 2.dp,
                                color = if (quizPassed) Color.White.copy(alpha = 0.3f) else Color(0xFF00F2FF).copy(alpha = 0.5f),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable { 
                                if (quizPassed) {
                                    val currentNum = art.sortOrder
                                    if (currentNum < 50) {
                                        val nextId = "a${currentNum + 1}"
                                        navController.currentBackStackEntry?.savedStateHandle?.set("quiz_passed", false)
                                        navController.navigate("lesson/$nextId/$ruleColorHex") {
                                            popUpTo("lesson/${art.id}/$ruleColorHex") { inclusive = true }
                                        }
                                    } else {
                                        navController.popBackStack()
                                    }
                                } else {
                                    navController.navigate("quiz/${art.id}")
                                }
                            }, 
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (quizPassed) Icons.AutoMirrored.Rounded.ArrowForward else Icons.Rounded.ElectricBolt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (quizPassed) {
                                    if (art.sortOrder < 50) "SIGUIENTE LECCIÓN" else "FINALIZAR LECTURA"
                                } else {
                                    "¡DESBLOQUEAR SIGUIENTE NIVEL!"
                                }, 
                                color = Color.White, 
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ParaphraseCard(ruleColor: Color, article: Article) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(0.dp, RoundedCornerShape(28.dp), spotColor = ruleColor.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.05f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF50DC78).copy(alpha = 0.12f))
                        .border(1.dp, Color(0xFF50DC78).copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = Color(0xFF50DC78), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Parafraseo Pedagógico", color = Color(0xFF50DC78), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("© Safe", color = Color.White.copy(alpha = 0.28f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(article.emoji, fontSize = 40.sp)
            Text(article.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(article.articleNumber, color = ruleColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.08f))

            // SECCIÓN GENERAL: SUB-ARTÍCULOS EN CARDS INDIVIDUALES
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                article.keyPoints.forEach { sectionText ->
                    KeyPointCard(text = sectionText, color = ruleColor)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "RESUMEN DE PUNTOS CLAVE",
                color = ruleColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // TARJETA DE PUNTOS CLAVE CONCISA Y HERMOSA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ruleColor.copy(alpha = 0.12f))
                    .border(1.dp, ruleColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = article.paraphrase,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Esta es una explicación simplificada. Para conocer la redacción FIBA, consulta el artículo correspondiente.",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 11.sp,
                fontStyle = FontStyle.Italic,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun KeyPointCard(text: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.07f))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FibaExternalLinkButton(
    ruleColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ruleColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = ruleColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Consultar Reglamento FIBA",
                    color = ruleColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = "Ver recursos en fiba.basketball",
                    color = Color.White.copy(alpha = 0.38f),
                    fontSize = 10.sp
                )
            }

            Icon(
                imageVector = Icons.Default.OpenInNew,
                null,
                tint = ruleColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
