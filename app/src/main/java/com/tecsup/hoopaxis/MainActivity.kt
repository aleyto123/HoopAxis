package com.tecsup.hoopaxis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.tecsup.hoopaxis.ui.navigation.HoopAxisNavGraph
import com.tecsup.hoopaxis.ui.screens.DashboardScreen
import com.tecsup.hoopaxis.ui.theme.HoopAxisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoopAxisTheme {
                val navController = rememberNavController()
                HoopAxisNavGraph(navController = navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    HoopAxisTheme {
        DashboardScreen()
    }
}
