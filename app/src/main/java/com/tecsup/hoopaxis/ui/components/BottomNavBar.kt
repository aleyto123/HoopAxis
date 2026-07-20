package com.tecsup.hoopaxis.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.hoopaxis.ui.theme.BackgroundBase
import com.tecsup.hoopaxis.ui.theme.NavSelected
import com.tecsup.hoopaxis.ui.theme.NavUnselected

@Composable
fun BottomNavBar(
    currentRoute: String,
    onHomeClick: () -> Unit = {},
    onRulesClick: () -> Unit = {},
    onChaptersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = BackgroundBase.copy(alpha = 0.98f),
        tonalElevation = 0.dp
    ) {
        val navItems = listOf(
            NavigationItem("inicio", "Inicio", Icons.Rounded.Home, onHomeClick),
            NavigationItem("reglas", "Reglas", Icons.AutoMirrored.Rounded.MenuBook, onRulesClick),
            NavigationItem("capitulos", "Capítulos", Icons.Rounded.Description, onChaptersClick),
            NavigationItem("perfil", "Perfil", Icons.Rounded.Person, onProfileClick)
        )
        
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = null,
                        tint = if (selected) NavSelected else NavUnselected,
                        modifier = Modifier.size(28.dp)
                    ) 
                },
                label = { 
                    Text(
                        text = item.label, 
                        color = if (selected) NavSelected else NavUnselected,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        fontSize = 11.sp
                    ) 
                },
                selected = selected,
                onClick = item.onClick,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)
