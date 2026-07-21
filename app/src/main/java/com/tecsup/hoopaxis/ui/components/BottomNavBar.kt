package com.tecsup.hoopaxis.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.hoopaxis.ui.theme.AppColors

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onRulesClick: () -> Unit = {},
    onChaptersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .background(AppColors.GlassDeep)
    ) {
        Column {
            HorizontalDivider(color = Color.White.copy(alpha = 0.13f), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val navItems = listOf(
                    NavigationItem("inicio", "Inicio", Icons.Rounded.Home, onHomeClick),
                    NavigationItem("reglas", "Reglas", Icons.AutoMirrored.Rounded.MenuBook, onRulesClick),
                    NavigationItem("capitulos", "Capítulos", Icons.Rounded.Description, onChaptersClick),
                    NavigationItem("perfil", "Perfil", Icons.Rounded.Person, onProfileClick)
                )

                navItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavItem(item, selected)
                }
            }
        }
    }
}

@Composable
fun NavItem(item: NavigationItem, selected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { item.onClick() }
            .padding(horizontal = 8.dp)
    ) {
        AnimatedVisibility(visible = selected) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(AppColors.Purple)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (selected) AppColors.Purple else Color.White.copy(alpha = 0.38f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = item.label,
            color = if (selected) AppColors.Purple else Color.White.copy(alpha = 0.38f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp
        )
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
