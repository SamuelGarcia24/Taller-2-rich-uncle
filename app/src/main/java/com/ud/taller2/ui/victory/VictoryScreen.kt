package com.ud.taller2.ui.victory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ud.taller2.navigation.Screen

@Composable
fun VictoryScreen(navController: NavController, finalMoney: Int, turns: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD4EDDA)), // Light Green
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Victory",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFF28A745)
        )
        Text(text = "VICTORY!", style = MaterialTheme.typography.displayMedium, color = Color(0xFF155724))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Final Capital: $$finalMoney", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Total Turns: $turns", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("home") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28A745))
        ) {
            Text("Main Menu")
        }
    }
}