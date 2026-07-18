package com.tecsup.hoopaxis.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tecsup.hoopaxis.ui.theme.Background

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleDetailScreen(ruleId: Int, onBack: () -> Unit) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Regla #$ruleId", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Detalle de la Regla ID: $ruleId", color = Color.White)
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
