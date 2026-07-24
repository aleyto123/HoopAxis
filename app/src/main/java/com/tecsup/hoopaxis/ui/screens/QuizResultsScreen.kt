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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.hoopaxis.ui.components.CircularProgress
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.AppColors

@Composable
fun QuizResultsScreen(navController: NavController, score: String?, total: String?) {
    val s = score?.toIntOrNull() ?: 0
    val t = total?.toIntOrNull() ?: 8
    val percentage = (s.toFloat() / t.toFloat() * 100).toInt()

    AnimatedVisibility(visible = true, enter = fadeIn() + slideInHorizontally { it / 3 }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Resultados 🏆", style = MaterialTheme.typography.displayLarge, fontSize = 24.sp)
            
            Spacer(modifier = Modifier.height(24.dp))

            // Hero Card
            GlassCard(modifier = Modifier.fillMaxWidth(), categoryColor = AppColors.Gold) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    CircularProgress(progress = s.toFloat() / t.toFloat(), categoryColor = AppColors.Gold, size = 88.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("$percentage%", color = AppColors.Gold, style = MaterialTheme.typography.displayLarge, fontSize = 36.sp)
                    Text("$s de $t correctas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when {
                            percentage >= 80 -> "¡Excelente! Eres un experto en el reglamento."
                            percentage >= 50 -> "¡Bien! Sigue practicando para mejorar."
                            else -> "Repasa las reglas e inténtalo de nuevo."
                        },
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mock history for UI demo
                items(List(t) { it }) { index ->
                    val isCorrect = index < s
                    ReviewItem(isCorrect = isCorrect, index = index)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Pink)))
                    .clickable { navController.navigate("quiz") },
                contentAlignment = Alignment.Center
            ) {
                Text("Intentar de nuevo", color = Color.White, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .clickable { 
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("Volver al inicio", color = Color.White, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ReviewItem(isCorrect: Boolean, index: Int) {
    val color = if (isCorrect) AppColors.Green else AppColors.Red
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Pregunta de ejemplo número ${index + 1} del reglamento...",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("TEMA", color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
