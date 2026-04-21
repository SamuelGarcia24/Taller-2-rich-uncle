package com.ud.taller2.ui.gameover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
fun GameOverScreen(navController: NavController, finalMoney: Int, turns: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8D7DA)), // Light Red
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Defeat",
            modifier = Modifier.size(120.dp),
            tint = Color(0xFFDC3545)
        )
        Text(text = "GAME OVER", style = MaterialTheme.typography.displayMedium, color = Color(0xFF721C24))
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Final Capital: $$finalMoney", style = MaterialTheme.typography.headlineSmall)
        Text(text = "You went bankrupt in turn $turns", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate("home") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
        ) {
            Text("Try Again")
        }
    }
}