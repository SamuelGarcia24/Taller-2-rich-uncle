package com.ud.taller2.ui.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ud.taller2.navigation.Screen

@Composable
fun GameScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Game Screen")

        // Example navigation to Victory (for testing)
        Button(
            onClick = {
                navController.navigate(Screen.Victory.createRoute(5000, 10))
            }
        ) {
            Text("Test Victory")
        }

        // Example navigation to Game Over (for testing)
        Button(
            onClick = {
                navController.navigate(Screen.GameOver.createRoute(0, 5))
            }
        ) {
            Text("Test Game Over")
        }

        // TODO: Person 2 - Implement complete Game UI
    }
}