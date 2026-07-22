package com.tecsup.hoopaxis.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.hoopaxis.data.repository.UserRepository
import com.tecsup.hoopaxis.ui.components.GlassCard
import com.tecsup.hoopaxis.ui.theme.AppColors
import kotlinx.coroutines.delay

@Composable
fun ProScreen(navController: NavController) {
    var selectedPlan by remember { mutableStateOf("yearly") }
    var isPaying by remember { mutableStateOf(false) }

    LaunchedEffect(isPaying) {
        if (isPaying) {
            delay(1800)
            UserRepository.setPro(true)
            navController.navigate("payment_success") {
                popUpTo("dashboard") { inclusive = false }
            }
        }
    }

    AnimatedVisibility(visible = true, enter = fadeIn() + slideInHorizontally { it / 3 }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
            }

            // HERO BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF1A0640), Color(0xFF2D0A5E), Color(0xFF0D1A60))
                        )
                    )
                    .border(1.dp, AppColors.Purple.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFFFFD166), Color(0xFFFF9800)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(AppColors.Gold.copy(alpha = 0.15f))
                            .border(1.dp, AppColors.Gold.copy(alpha = 0.3f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("ACCESO EXCLUSIVO", color = AppColors.Gold, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Modo Árbitro Pro", style = MaterialTheme.typography.displayLarge, fontSize = 26.sp)
                    Text(
                        "Domina el reglamento oficial FIBA con herramientas avanzadas.",
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 14.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // BENEFITS
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                BenefitItem("🚫", "Sin anuncios", "Experiencia de estudio 100% limpia.")
                BenefitItem("📚", "16 capítulos completos", "Desbloquea todo el contenido FIBA.")
                BenefitItem("📑", "Artículos oficiales", "Acceso al texto legal bajo demanda.")
                BenefitItem("⚡", "Exámenes cronometrados", "Práctica real con presión de tiempo.")
                BenefitItem("🏆", "Certificado digital", "Al completar todos los módulos.")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // PLAN SELECTOR
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                PlanCard("yearly", "$34.99/año", "AHORRA 42%", selectedPlan == "yearly") { selectedPlan = "yearly" }
                PlanCard("monthly", "$4.99/mes", null, selectedPlan == "monthly") { selectedPlan = "monthly" }
                PlanCard("lifetime", "$89.99 único", "MEJOR VALOR", selectedPlan == "lifetime") { selectedPlan = "lifetime" }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AppColors.Purple, AppColors.Pink, AppColors.Gold),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .clickable(enabled = !isPaying) { isPaying = true }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPaying) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Procesando pago seguro…", color = Color.White, fontWeight = FontWeight.Black)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Activar Modo Árbitro Pro · $selectedPlan", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }

            Text(
                "Cancela cuando quieras · Pago seguro SSL",
                color = Color.White.copy(alpha = 0.28f),
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 40.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun BenefitItem(emoji: String, title: String, desc: String) {
    GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                Text(desc, color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            }
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF50DC78), modifier = Modifier.size(15.dp))
        }
    }
}

@Composable
fun PlanCard(id: String, price: String, badge: String?, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(0.dp, RoundedCornerShape(20.dp), spotColor = if (selected) AppColors.Purple.copy(alpha = 0.25f) else Color.Transparent)
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (selected) {
                    Modifier.background(Brush.linearGradient(listOf(AppColors.Purple.copy(alpha = 0.22f), AppColors.Pink.copy(alpha = 0.16f))))
                } else {
                    Modifier.background(Color.White.copy(alpha = 0.05f))
                }
            )
            .border(if (selected) 1.5.dp else 1.dp, if (selected) AppColors.Purple else Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (selected) AppColors.Purple else Color.White.copy(alpha = 0.2f), CircleShape)
                    .background(if (selected) AppColors.Purple else Color.Transparent)
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(id.uppercase(), color = if (selected) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Text(price, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.Pink)))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(badge, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
