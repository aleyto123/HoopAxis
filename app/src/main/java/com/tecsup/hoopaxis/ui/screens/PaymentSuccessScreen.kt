package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.hoopaxis.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun PaymentSuccessScreen(navController: NavController) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(3500)
        navController.navigate("dashboard") {
            popUpTo(0) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .shadow(24.dp, CircleShape, spotColor = AppColors.Gold.copy(alpha = 0.6f))
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Gold))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("¡Bienvenido, Árbitro Pro!", style = MaterialTheme.typography.displayLarge, fontSize = 26.sp)
            Text("Tu cuenta ha sido actualizada.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)

            Spacer(modifier = Modifier.height(40.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                UnlockItem("Anuncios eliminados", 0)
                UnlockItem("16 capítulos desbloqueados", 100)
                UnlockItem("Texto oficial FIBA disponible", 200)
                UnlockItem("Exámenes cronometrados activos", 300)
            }

            Spacer(modifier = Modifier.height(48.dp))

            var progress by remember { mutableFloatStateOf(0f) }
            LaunchedEffect(Unit) {
                animate(0f, 1f, animationSpec = tween(3000, easing = LinearEasing)) { value, _ -> progress = value }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.width(200.dp).height(4.dp).clip(RoundedCornerShape(50)),
                color = AppColors.Gold,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text("Redirigiendo a la app Pro…", color = Color.White.copy(alpha = 0.35f), fontSize = 11.sp)
        }
    }
}

@Composable
fun UnlockItem(text: String, delayMillis: Int) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(400)) + expandVertically(tween(400))
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF50DC78), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text, color = Color(0xFF50DC78), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
