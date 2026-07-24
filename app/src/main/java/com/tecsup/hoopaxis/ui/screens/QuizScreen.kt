package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.hoopaxis.ui.theme.AppColors

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: String
)

@Composable
fun QuizScreen(navController: NavController) {
    val questions = remember {
        listOf(
            QuizQuestion("¿Cuántos jugadores tiene un equipo en pista?", listOf("4 jugadores", "5 jugadores", "6 jugadores", "12 jugadores"), 1, "Un equipo de baloncesto FIBA juega con 5 jugadores en cancha.", "El Juego"),
            QuizQuestion("¿Cuánto dura un partido FIBA (tiempo reglamentario)?", listOf("20 min", "40 min", "48 min", "30 min"), 1, "Se divide en 4 cuartos de 10 minutos cada uno.", "El Juego"),
            QuizQuestion("¿A partir de qué falta de equipo se lanzan tiros libres?", listOf("3 faltas", "4 faltas", "5 faltas", "6 faltas"), 2, "Tras la 4ª falta de equipo en un cuarto.", "Faltas"),
            QuizQuestion("¿Qué ocurre tras una falta técnica?", listOf("1 tiro + saque", "2 tiros + posesión", "1 tiro libre", "Expulsión directa"), 0, "Se concede 1 tiro libre y el balón al equipo que tenía el control.", "Faltas"),
            QuizQuestion("¿Tiempo máximo para cruzar pista propia?", listOf("5 segundos", "10 segundos", "8 segundos", "24 segundos"), 2, "Un equipo debe pasar el balón a pista delantera en menos de 8 segundos.", "Violaciones"),
            QuizQuestion("¿Faltas personales para ser eliminado?", listOf("4 faltas", "5 faltas", "6 faltas", "7 faltas"), 1, "Al cometer la 5ª falta (personal o técnica), el jugador debe salir.", "Faltas"),
            QuizQuestion("¿Tiempo de posesión para lanzar a canasta?", listOf("14 segundos", "24 segundos", "30 segundos", "8 segundos"), 1, "La regla de los 24 segundos obliga a lanzar antes de que acabe el tiempo.", "Violaciones"),
            QuizQuestion("¿Se puede pedir tiempo muerto tras canasta en últimos 2 min?", listOf("Sí", "No", "Solo el local", "Solo el visitante"), 0, "Sí, el equipo que recibe la canasta puede solicitarlo.", "Procedimientos")
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    val history = remember { mutableStateListOf<Boolean>() }

    val currentQuestion = questions[currentIndex]

    AnimatedVisibility(visible = true, enter = fadeIn() + slideInHorizontally { it / 3 }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Quiz 🎯", style = MaterialTheme.typography.displayLarge, fontSize = 22.sp)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = AppColors.Gold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${currentIndex + 1}/8", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { (currentIndex + 1) / 8f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(50)),
                color = AppColors.Purple,
                trackColor = Color.White.copy(alpha = 0.12f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (i in 0 until 8) {
                    val color = when {
                        i < history.size -> if (history[i]) AppColors.Green else AppColors.Red
                        i == currentIndex -> AppColors.Purple
                        else -> Color.White.copy(alpha = 0.15f)
                    }
                    Box(modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(color))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.Purple.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(currentQuestion.category.uppercase(), color = AppColors.Purple, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(currentQuestion.question, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Black, lineHeight = 26.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val isCorrect = index == currentQuestion.correctIndex
                val statusColor = when {
                    selectedAnswer == null -> Color.White.copy(alpha = 0.18f)
                    isCorrect -> AppColors.Green
                    isSelected -> AppColors.Red
                    else -> Color.White.copy(alpha = 0.18f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedAnswer != null && (isCorrect || isSelected)) statusColor.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.06f))
                        .border(1.dp, if (selectedAnswer != null && (isCorrect || isSelected)) statusColor else Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedAnswer == null) {
                            selectedAnswer = index
                            val correct = index == currentQuestion.correctIndex
                            history.add(correct)
                            if (correct) score++
                        }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(28.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.2f)).border(1.dp, statusColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = ('A'.code + index).toChar().toString(), color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(option, color = if (selectedAnswer != null && (isCorrect || isSelected)) statusColor else Color.White, modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        
                        if (selectedAnswer != null) {
                            if (isCorrect) Icon(Icons.Default.CheckCircle, null, tint = AppColors.Green)
                            else if (isSelected) Icon(Icons.Default.Cancel, null, tint = AppColors.Red)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = selectedAnswer != null) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("💡 ${currentQuestion.explanation}", color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Pink)))
                            .clickable {
                                if (currentIndex < 7) {
                                    currentIndex++
                                    selectedAnswer = null
                                } else {
                                    navController.navigate("quiz_results/$score/8") {
                                        popUpTo("quiz") { inclusive = true }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (currentIndex < 7) "Siguiente →" else "Ver resultados", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
