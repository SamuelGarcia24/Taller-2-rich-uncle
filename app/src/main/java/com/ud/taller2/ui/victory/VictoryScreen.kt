package com.ud.taller2.ui.victory

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
fun VictoryScreen(
    navController: NavController,
    finalMoney: Int,
    turns: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "VICTORY!")
        Text(text = "Final money: $$finalMoney")
        Text(text = "Turns: $turns")

        Button(
            onClick = { navController.navigate(Screen.Home.route) }
        ) {
            Text("Back to Home")
        }

        // TODO: Person 2 - Implement complete Victory UI
    }
}