package com.tecsup.hoopaxis.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    onNavigateToChapters: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as HoopAxisApplication).repository
    val viewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.provideFactory(repository)
    )
    
    val uiState by viewModel.uiState.collectAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }

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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Purple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = AppColors.Purple,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editedName.isNotBlank()) {
                        viewModel.updateUserName(editedName)
                        showEditDialog = false
                    }
                }) {
                    Text("GUARDAR", color = AppColors.Purple, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCELAR", color = AppColors.TextSecondary)
                }
            }
        )
    }

    Scaffold(
        bottomBar = { 
            BottomNavBar(
                currentRoute = "perfil",
                onHomeClick = onNavigateToHome,
                onRulesClick = onNavigateToRules,
                onChaptersClick = onNavigateToChapters,
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
            
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Activar Modo Árbitro Pro Banner
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                categoryColor = AppColors.Gold
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = AppColors.Gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Activar Modo Árbitro Pro",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
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
                    // Avatar
                    val userInitial = (uiState.user?.name ?: "A").take(1).uppercase()
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(AppColors.Pink, AppColors.Purple)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitial,
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.user?.name ?: "Árbitro",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Nivel 4 — Árbitro Amateur",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            repeat(4) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = AppColors.Gold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AppColors.TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { 
                            editedName = uiState.user?.name ?: ""
                            showEditDialog = true 
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = AppColors.TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // XP Progress Bar
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Nivel 4 — Árbitro Amateur",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "680 / 1000 XP",
                            color = AppColors.Purple,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.68f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(AppColors.Purple, AppColors.Pink)
                                    )
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    icon = Icons.Rounded.MenuBook,
                    title = "3/16",
                    subtitle = "Capítulos completados",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(
                    icon = Icons.Rounded.TrendingUp,
                    title = "31%",
                    subtitle = "Progreso global",
                    modifier = Modifier.weight(1f),
                    iconTint = AppColors.Green
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    icon = Icons.Rounded.ElectricBolt,
                    title = "7 días 🔥",
                    subtitle = "Racha activa",
                    modifier = Modifier.weight(1f),
                    iconTint = AppColors.Gold
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatCard(
                    icon = Icons.Rounded.TrackChanges,
                    title = "82%",
                    subtitle = "Precisión quiz",
                    modifier = Modifier.weight(1f),
                    iconTint = AppColors.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Items
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    MenuItem(icon = Icons.Outlined.Notifications, label = "Notificaciones")
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    MenuItem(icon = Icons.Rounded.Settings, label = "Configuración")
                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                    MenuItem(icon = Icons.Rounded.StarOutline, label = "Calificar la app")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Red.copy(alpha = 0.1f))
                    .border(1.dp, AppColors.Red.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                        contentDescription = null,
                        tint = AppColors.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cerrar sesión",
                        color = AppColors.Red,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, iconTint: Color = AppColors.Purple) {
    GlassCard(
        modifier = modifier.height(130.dp)
    ) {
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
fun MenuItem(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AppColors.TextSecondary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}
