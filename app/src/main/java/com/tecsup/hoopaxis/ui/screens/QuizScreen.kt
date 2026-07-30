package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.data.model.QuizQuestion
import com.tecsup.hoopaxis.ui.theme.AppColors
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    articleId: String?
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )

    val allQuestions by repository.allQuizQuestions.collectAsState(initial = emptyList())
    val questions = remember(allQuestions, articleId) {
        allQuestions.filter { it.category == articleId }
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var showResults by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<Boolean>() }

    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = AppColors.Purple)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando evaluación...", color = Color.White)
            }
        }
        return
    }

    if (showResults) {
        QuizLocalResults(
            score = score,
            total = questions.size,
            onRetry = {
                currentIndex = 0
                selectedAnswer = null
                score = 0
                showResults = false
                history.clear()
            },
            onFinish = { passed ->
                if (passed) {
                    // Si pasó, volvemos a la lección con un flag de éxito o simplemente permitimos avanzar
                    navController.previousBackStackEntry?.savedStateHandle?.set("quiz_passed", true)
                    navController.popBackStack()
                } else {
                    navController.popBackStack()
                }
            }
        )
        return
    }

    val currentQuestion = questions[currentIndex]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Evaluación", color = Color.White, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pregunta ${currentIndex + 1} de ${questions.size}", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text("Puntos: $score", color = AppColors.Gold, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(50)),
                color = AppColors.Purple,
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp))
                    .padding(24.dp)
            ) {
                Text(
                    text = currentQuestion.question,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Options
            currentQuestion.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val isCorrect = index == currentQuestion.correctIndex
                
                val borderColor = when {
                    selectedAnswer == null -> Color.White.copy(alpha = 0.15f)
                    isCorrect -> AppColors.Green
                    isSelected -> AppColors.Red
                    else -> Color.White.copy(alpha = 0.1f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) borderColor.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.04f))
                        .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                        .clickable(enabled = selectedAnswer == null) {
                            selectedAnswer = index
                            if (isCorrect) score++
                            history.add(isCorrect)
                        }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isSelected || (selectedAnswer != null && isCorrect)) borderColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f))
                                .border(1.dp, borderColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A'.code + index).toChar().toString(),
                                color = if (selectedAnswer != null && (isSelected || isCorrect)) borderColor else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = option,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (selectedAnswer != null) {
                            if (isCorrect) Icon(Icons.Default.CheckCircle, null, tint = AppColors.Green)
                            else if (isSelected) Icon(Icons.Default.Cancel, null, tint = AppColors.Red)
                        }
                    }
                }
            }

            // Feedback and Next Button
            AnimatedVisibility(visible = selectedAnswer != null) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text("RAZÓN:", color = AppColors.Purple, fontWeight = FontWeight.Black, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(currentQuestion.explanation, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                currentIndex++
                                selectedAnswer = null
                            } else {
                                showResults = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple)
                    ) {
                        Text(if (currentIndex < questions.size - 1) "Siguiente pregunta" else "Finalizar evaluación", fontWeight = FontWeight.Black)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun QuizLocalResults(
    score: Int,
    total: Int,
    onRetry: () -> Unit,
    onFinish: (Boolean) -> Unit
) {
    val passed = score == total
    val percentage = (score.toFloat() / total.toFloat() * 100).toInt()

    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (passed) "¡Excelente! 🎉" else "Casi lo logras...",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (passed) "Has demostrado que dominas este artículo perfectamente." else "Para avanzar al siguiente artículo debes responder todas correctamente ($score/$total).",
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                CircularProgressIndicator(
                    progress = { score.toFloat() / total },
                    modifier = Modifier.fillMaxSize(),
                    color = if (passed) AppColors.Green else AppColors.Pink,
                    strokeWidth = 10.dp,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Text("$percentage%", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            Spacer(modifier = Modifier.height(60.dp))

            if (!passed) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Pink)
                ) {
                    Text("Intentar de nuevo", fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = { onFinish(passed) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (passed) AppColors.Purple else Color.White.copy(alpha = 0.1f))
            ) {
                Text(if (passed) "Continuar" else "Volver a la lectura", fontWeight = FontWeight.Black, color = Color.White)
            }
        }
    }
}
