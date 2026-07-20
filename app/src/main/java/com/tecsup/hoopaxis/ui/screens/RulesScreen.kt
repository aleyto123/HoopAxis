package com.tecsup.hoopaxis.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.RuleCategory
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.GlassmorphicCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@Composable
fun RulesScreen(
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

    val radialGlow = object : ShaderBrush() {
        override fun createShader(size: androidx.compose.ui.geometry.Size): Shader {
            return RadialGradientShader(
                center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height * 0.15f),
                radius = size.width * 1.5f,
                colors = listOf(BackgroundGlow, BackgroundBase),
                colorStops = listOf(0f, 1f),
                tileMode = TileMode.Clamp
            )
        }
    }

    Scaffold(
        bottomBar = { 
            BottomNavBar(
                currentRoute = "reglas",
                onHomeClick = onNavigateToHome,
                onRulesClick = onNavigateToRules,
                onChaptersClick = onNavigateToChapters,
                onProfileClick = onNavigateToProfile
            ) 
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(radialGlow)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "NIVEL 1",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                
                Text(
                    text = "8 Reglas Principales",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                
                Text(
                    text = "Selecciona una regla para ver sus capítulos",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(24.dp))

                uiState.categories.forEach { category ->
                    RuleVerticalCard(
                        category = category,
                        onClick = { onNavigateToDetail(category.id) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                PublicityBanner()
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RuleVerticalCard(category: RuleCategory, onClick: () -> Unit) {
    val tagColor = Color(android.graphics.Color.parseColor(category.tagColorHex))
    
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 28.dp,
        backgroundAlpha = 0.06f
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Etiqueta R1, R2...
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tagColor.copy(alpha = 0.15f))
                    .border(1.dp, tagColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "R${category.id}",
                    color = tagColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = category.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = category.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = category.description,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${category.chaptersCount} capítulos · ${category.lessonsCount} lecciones",
                    color = NavSelected,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Progreso Circular
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = Color.White.copy(alpha = 0.08f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(ProgressCyan, ProgressPurple, ProgressBlue, ProgressCyan)
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * category.progress,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(category.progress * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
