package com.tecsup.hoopaxis.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Purple,
    secondary = AppColors.Blue,
    tertiary = AppColors.Gold,
    background = AppColors.Background,
    surface = AppColors.GlassSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = AppColors.Background,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AnimatedMeshBackground(content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    
    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse1"
    )
    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(8500, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse2"
    )
    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0.93f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse3"
    )
    val pulse4 by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse4"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(color = AppColors.Background)
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AppColors.Purple.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(-100f, -100f),
                    radius = 400.dp.toPx() * pulse1
                ),
                center = Offset(-100f, -100f),
                radius = 400.dp.toPx() * pulse1
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AppColors.Pink.copy(alpha = 0.47f), Color.Transparent),
                    center = Offset(size.width + 80f, 160f),
                    radius = 320.dp.toPx() * pulse2
                ),
                center = Offset(size.width + 80f, 160f),
                radius = 320.dp.toPx() * pulse2
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AppColors.Blue.copy(alpha = 0.42f), Color.Transparent),
                    center = Offset(50f, size.height - 180f),
                    radius = 270.dp.toPx() * pulse3
                ),
                center = Offset(50f, size.height - 180f),
                radius = 270.dp.toPx() * pulse3
            )
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(AppColors.Gold.copy(alpha = 0.42f), Color.Transparent),
                    center = Offset(size.width - 30f, size.height - 60f),
                    radius = 220.dp.toPx() * pulse4
                ),
                center = Offset(size.width - 30f, size.height - 60f),
                radius = 220.dp.toPx() * pulse4
            )
        }
        content()
    }
}

@Composable
fun HoopAxisTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography
    ) {
        AnimatedMeshBackground(content = content)
    }
}
