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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

enum class OfficialState { IDLE, LOADING, SHOWN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    navController: NavController,
    lessonId: String?,
    ruleColorHex: String?
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )

    var article by remember { mutableStateOf<Article?>(null) }
    val ruleColor = Color(android.graphics.Color.parseColor("#${ruleColorHex ?: "C96BFF"}"))
    var officialTextState by remember { mutableStateOf(OfficialState.IDLE) }
    
    val scrollState = rememberScrollState()

    LaunchedEffect(lessonId) {
        lessonId?.let { article = viewModel.getArticle(it) }
    }

    LaunchedEffect(officialTextState) {
        if (officialTextState == OfficialState.LOADING) {
            delay(1400)
            officialTextState = OfficialState.SHOWN
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
                                text = "Artículo Oficial",
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

                    // FIBA TOGGLE BUTTON
                    FibaToggleButton(
                        state = officialTextState,
                        ruleColor = ruleColor,
                        onToggle = {
                            officialTextState = if (officialTextState == OfficialState.SHOWN) OfficialState.IDLE else OfficialState.LOADING
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // LOADING STATE
                    AnimatedVisibility(visible = officialTextState == OfficialState.LOADING) {
                        LoadingOfficialDb(ruleColor = ruleColor)
                    }

                    // OFFICIAL TEXT CARD
                    AnimatedVisibility(visible = officialTextState == OfficialState.SHOWN) {
                        OfficialTextCard(ruleColor = ruleColor, article = art)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTTOM ACTION
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(AppColors.Purple, AppColors.Pink)
                                )
                            )
                            .clickable { navController.popBackStack() }, 
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Siguiente lección →", color = Color.White, fontWeight = FontWeight.Black)
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
            Text("🏀", fontSize = 40.sp)
            Text(article.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(article.articleNumber, color = ruleColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.08f))

            Text(
                text = article.paraphrase,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 15.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ruleColor.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                    .border(1.dp, ruleColor.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    "PUNTOS CLAVE",
                    color = ruleColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                article.keyPoints.forEach { point ->
                    KeyPoint(text = point, color = ruleColor)
                }
            }
        }
    }
}

@Composable
fun KeyPoint(text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FibaToggleButton(
    state: OfficialState,
    ruleColor: Color,
    onToggle: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when(state) {
            OfficialState.IDLE -> Color.White.copy(alpha = 0.06f)
            OfficialState.LOADING -> ruleColor.copy(alpha = 0.12f)
            OfficialState.SHOWN -> ruleColor.copy(alpha = 0.2f)
        },
        animationSpec = tween(300),
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        targetValue = when(state) {
            OfficialState.IDLE -> Color.White.copy(alpha = 0.15f)
            OfficialState.LOADING -> ruleColor.copy(alpha = 0.35f)
            OfficialState.SHOWN -> ruleColor.copy(alpha = 0.55f)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )
    val rotation by animateFloatAsState(
        targetValue = if (state == OfficialState.SHOWN) 180f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(if (state == OfficialState.SHOWN) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onToggle() }
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
                    text = if (state == OfficialState.SHOWN) "Ocultar Artículo Oficial" else "Ver Artículo Oficial FIBA",
                    color = ruleColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = when {
                        state == OfficialState.IDLE -> "Consultar texto oficial"
                        else -> "Texto oficial verificado ✓"
                    },
                    color = if (state == OfficialState.SHOWN) ruleColor else Color.White.copy(alpha = 0.38f),
                    fontSize = 10.sp
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                null,
                tint = ruleColor,
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
fun LoadingOfficialDb(ruleColor: Color) {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = LinearOutSlowInEasing),
        label = "progress"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0D0520).copy(alpha = 0.65f))
            .border(1.dp, ruleColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = ruleColor, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text("CONSULTANDO BASE DE DATOS FIBA", color = ruleColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
            Text("Recuperando artículo oficial…", color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp)
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = ruleColor,
                trackColor = Color.White.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun OfficialTextCard(ruleColor: Color, article: Article) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(0.dp, RoundedCornerShape(20.dp), spotColor = ruleColor.copy(alpha = 0.3f))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A0640), Color(0xFF0D1A60))
                )
            )
            .border(1.5.dp, ruleColor.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp).clip(RoundedCornerShape(6.dp)).background(ruleColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("F", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("REGLAMENTO OFICIAL FIBA 2026", color = ruleColor, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("Texto recuperado · Solo lectura", color = Color.White.copy(alpha = 0.35f), fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF50DC78), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DB OK", color = Color(0xFF50DC78), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = ruleColor.copy(alpha = 0.2f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFD166).copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFFFFD166).copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Row {
                    Icon(Icons.Default.Info, null, tint = Color(0xFFFFD166), modifier = Modifier.size(11.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "Este texto es una copia oficial protegida para consulta legal.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 9.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = article.officialText,
                color = Color.White.copy(alpha = 0.80f),
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontStyle = FontStyle.Italic,
                lineHeight = 22.sp
            )

            HorizontalDivider(modifier = Modifier.padding(top = 20.dp, bottom = 8.dp), color = Color.White.copy(alpha = 0.1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VerifiedUser, null, tint = Color.White.copy(alpha = 0.2f), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Contenido Oficial · FIBA Rules 2026", color = Color.White.copy(alpha = 0.22f), fontSize = 9.sp)
            }
        }
    }
}
