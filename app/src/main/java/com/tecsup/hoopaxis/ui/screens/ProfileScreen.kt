package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.*
import com.tecsup.hoopaxis.viewmodel.DashboardViewModel

@Composable
fun ProfileScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToRules: () -> Unit = {},
    onNavigateToArticles: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    
    var showLegalDialog by remember { mutableStateOf(false) }
    var legalDialogTitle by remember { mutableStateOf("") }
    var legalDialogText by remember { mutableStateOf("") }
    var showFibaButton by remember { mutableStateOf(false) }

    Crossfade(targetState = uiState.isLoading, animationSpec = tween(300)) { loading ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LoadingPulse()
            }
        } else {
            // Scaffold y Contenido Real
            Scaffold(
                bottomBar = { 
                    BottomNavBar(
                        currentRoute = "perfil",
                        onHomeClick = onNavigateToHome,
                        onRulesClick = onNavigateToRules,
                        onArticlesClick = onNavigateToArticles,
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
                    
                    val completedArticles = uiState.allArticles.count { it.isCompleted }
                    val currentXP = uiState.user?.xp ?: 0
                    val totalXP = 5000
                    val userRank = when {
                        completedArticles <= 15 -> "Árbitro Común"
                        completedArticles <= 35 -> "Árbitro Intermedio"
                        else -> "Árbitro Avanzado"
                    }

                    Text(
                        text = "Mi Perfil",
                        style = MaterialTheme.typography.displayLarge
                    )

                    if (uiState.user?.isAdmin == true) {
                        Spacer(modifier = Modifier.height(16.dp))
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToAdmin() },
                            categoryColor = AppColors.Purple
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, null, tint = AppColors.Purple)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Configuración de Administrador", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Info Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        categoryColor = AppColors.Purple
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val userInitial = (uiState.user?.name ?: "A").take(1).uppercase()
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Brush.verticalGradient(listOf(AppColors.Pink, AppColors.Purple))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = userInitial, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = uiState.user?.name ?: "Árbitro", style = MaterialTheme.typography.headlineMedium)
                                Text(text = "Rango: $userRank", style = MaterialTheme.typography.bodyMedium)
                            }

                            IconButton(
                                onClick = { 
                                    editedName = uiState.user?.name ?: ""
                                    showEditDialog = true 
                                },
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.Edit, "Editar", tint = AppColors.TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // XP Progress
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Nivel: $userRank", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "$currentXP / $totalXP XP", color = AppColors.Purple, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f))) {
                                Box(modifier = Modifier.fillMaxWidth(currentXP.toFloat() / totalXP).fillMaxHeight().background(Brush.horizontalGradient(listOf(AppColors.Purple, AppColors.Pink))))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats
                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatCard(icon = Icons.Rounded.MenuBook, title = "$completedArticles/50", subtitle = "Artículos completados", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(16.dp))
                        val globalProgress = (completedArticles.toFloat() / 50f * 100).toInt()
                        StatCard(icon = Icons.Rounded.TrendingUp, title = "$globalProgress%", subtitle = "Progreso global", modifier = Modifier.weight(1f), iconTint = AppColors.Green)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        val userStreak = uiState.user?.streak ?: 0
                        val quizAccuracy = if ((uiState.user?.totalQuestions ?: 0) > 0) {
                            (uiState.user!!.correctAnswers.toFloat() / uiState.user!!.totalQuestions * 100).toInt()
                        } else 0
                        
                        StatCard(icon = Icons.Rounded.ElectricBolt, title = "$userStreak días 🔥", subtitle = "Racha activa", modifier = Modifier.weight(1f), iconTint = AppColors.Gold)
                        Spacer(modifier = Modifier.width(16.dp))
                        StatCard(icon = Icons.Rounded.TrackChanges, title = "$quizAccuracy%", subtitle = "Precisión quiz", modifier = Modifier.weight(1f), iconTint = AppColors.Red)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Menu
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            MenuItem(
                                icon = Icons.Rounded.Share, 
                                label = "Compartir Aplicación",
                                onClick = {
                                    val sendIntent = android.content.Intent().apply {
                                        action = android.content.Intent.ACTION_SEND
                                        putExtra(android.content.Intent.EXTRA_TEXT, "¡Estoy dominando el reglamento de basketball con HoopAxis! Descárgala y únete al desafío.")
                                        type = "text/plain"
                                    }
                                    context.startActivity(android.content.Intent.createChooser(sendIntent, null))
                                }
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            MenuItem(
                                icon = Icons.Rounded.Star, 
                                label = "Calificar la app",
                                onClick = {
                                    uriHandler.openUri("https://play.google.com/store/apps/details?id=com.tecsup.HoopAxis")
                                }
                            )
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            MenuItem(icon = Icons.Rounded.Info, label = "Acerca de HoopAxis", onClick = {
                                legalDialogTitle = "Acerca de HoopAxis"
                                legalDialogText = "HoopAxis es una aplicación educativa independiente diseñada para facilitar el aprendizaje de las Reglas Oficiales de Baloncesto de la FIBA. No está afiliada, patrocinada ni respaldada por la FIBA. Para consultar la versión oficial y vigente del reglamento, visite el sitio web oficial de la FIBA."
                                showFibaButton = true
                                showLegalDialog = true
                            })
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            MenuItem(icon = Icons.Rounded.Gavel, label = "Información legal", onClick = {
                                legalDialogTitle = "Información legal"
                                legalDialogText = "Esta aplicación se proporciona con fines educativos y de consulta. La FIBA sigue siendo la única autoridad oficial para el reglamento de baloncesto."
                                showFibaButton = false
                                showLegalDialog = true
                            })
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            MenuItem(icon = Icons.Rounded.PrivacyTip, label = "Política de privacidad", onClick = {
                                legalDialogTitle = "Política de privacidad"
                                legalDialogText = "En HoopAxis respetamos tu privacidad. No compartimos tus datos personales con terceros. Tus progresos se guardan de forma segura para mejorar tu experiencia."
                                showFibaButton = false
                                showLegalDialog = true
                            })
                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                            MenuItem(icon = Icons.Rounded.Description, label = "Términos y condiciones", onClick = {
                                legalDialogTitle = "Términos y condiciones"
                                legalDialogText = "Al usar HoopAxis, aceptas que el contenido es para fines educativos. El uso de la app busca apoyar tu estudio del reglamento FIBA de manera dinámica."
                                showFibaButton = false
                                showLegalDialog = true
                            })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Logout
                    Box(
                        modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(24.dp)).background(AppColors.Red.copy(alpha = 0.1f)).border(1.dp, AppColors.Red.copy(alpha = 0.3f), RoundedCornerShape(24.dp)).clickable { onLogout() },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = AppColors.Red, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Cerrar sesión", color = AppColors.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Dialogs
            if (showEditDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDialog = false },
                    containerColor = AppColors.Background,
                    title = { Text("Editar Nombre", style = MaterialTheme.typography.headlineMedium) },
                    text = {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Nombre", color = AppColors.TextSecondary) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppColors.Purple, unfocusedBorderColor = Color.White.copy(alpha = 0.3f), cursorColor = AppColors.Purple, focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = { if (editedName.isNotBlank()) { viewModel.updateUserName(editedName); showEditDialog = false } }) {
                            Text("GUARDAR", color = AppColors.Purple, fontWeight = FontWeight.Black)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDialog = false }) { Text("CANCELAR", color = AppColors.TextSecondary) }
                    }
                )
            }

            if (showLegalDialog) {
                AlertDialog(
                    onDismissRequest = { showLegalDialog = false },
                    containerColor = AppColors.Background,
                    title = { Text(legalDialogTitle, style = MaterialTheme.typography.headlineMedium) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = legalDialogText, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, lineHeight = 22.sp)
                            if (showFibaButton) {
                                Button(onClick = { uriHandler.openUri("https://about.fiba.basketball/es/services/resource-hub/downloads") }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple), shape = RoundedCornerShape(12.dp)) {
                                    Icon(Icons.Rounded.Language, null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(8.dp)); Text("Ver reglamento FIBA", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showLegalDialog = false }) { Text("CERRAR", color = AppColors.Purple, fontWeight = FontWeight.Black) }
                    }
                )
            }
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, iconTint: Color = AppColors.Purple) {
    GlassCard(modifier = modifier.height(130.dp)) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
        Icon(imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = AppColors.TextMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun LoadingPulse() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha),
            contentAlignment = Alignment.Center
        ) {
            Text("🏀", fontSize = 60.sp)
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "SINCRONIZANDO...",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp
        )
    }
}
