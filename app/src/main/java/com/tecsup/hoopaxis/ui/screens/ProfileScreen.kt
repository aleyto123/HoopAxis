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
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.hoopaxis.HoopAxisApplication
import com.tecsup.hoopaxis.ui.components.BottomNavBar
import com.tecsup.hoopaxis.ui.components.GlassmorphicCard
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

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = SurfaceColor,
            title = { Text("Editar Nombre", color = Color.White, fontWeight = FontWeight.Black) },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Nombre", color = Color.White.copy(alpha = 0.6f)) },
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProgressCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = ProgressCyan
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
                    Text("GUARDAR", color = ProgressCyan, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCELAR", color = Color.White.copy(alpha = 0.5f))
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
                    text = "Mi Perfil",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Activar Modo Árbitro Pro Banner
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 20.dp,
                    backgroundAlpha = 0.05f
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
                                tint = AccentYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Activar Modo Árbitro Pro",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // User Info Card
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 28.dp,
                    backgroundAlpha = 0.08f
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
                                        listOf(Color(0xFFFE8EBD), Color(0xFF9D50BB))
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
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Nivel 4 — Árbitro Amateur",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                repeat(4) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = AccentYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.2f),
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
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // XP Progress Bar
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp,
                    backgroundAlpha = 0.05f
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Nivel 4 — Árbitro Amateur",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "680 / 1000 XP",
                                color = ProgressPurple,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        // Progress Bar
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
                                            listOf(ProgressPurple, Color(0xFFFE8EBD))
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
                        iconTint = Color(0xFF00FF87)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        icon = Icons.Rounded.ElectricBolt,
                        title = "7 días 🔥",
                        subtitle = "Racha activa",
                        modifier = Modifier.weight(1f),
                        iconTint = AccentYellow
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard(
                        icon = Icons.Rounded.TrackChanges,
                        title = "82%",
                        subtitle = "Precisión quiz",
                        modifier = Modifier.weight(1f),
                        iconTint = Color(0xFFFF4B2B)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Menu Items
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 28.dp,
                    backgroundAlpha = 0.05f
                ) {
                    Column {
                        MenuItem(icon = Icons.Outlined.Notifications, label = "Notificaciones")
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                        MenuItem(icon = Icons.Rounded.Settings, label = "Configuración")
                        Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(vertical = 12.dp))
                        MenuItem(icon = Icons.Rounded.StarOutline, label = "Calificar la app")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFFFF4B2B).copy(alpha = 0.1f))
                        .border(1.dp, Color(0xFFFF4B2B).copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = null,
                            tint = Color(0xFFFF4B2B),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Cerrar sesión",
                            color = Color(0xFFFF4B2B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, title: String, subtitle: String, modifier: Modifier = Modifier, iconTint: Color = ProgressPurple) {
    GlassmorphicCard(
        modifier = modifier.height(130.dp),
        cornerRadius = 24.dp,
        backgroundAlpha = 0.05f
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
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
            Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(16.dp)
        )
    }
}
