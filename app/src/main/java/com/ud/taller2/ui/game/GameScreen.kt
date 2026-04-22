package com.ud.taller2.ui.game

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ud.taller2.navigation.Screen

@Composable
fun GameScreen(
    navController: NavController,
    roomCode: String,
    playerId: String,
    viewModel: GameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Initialize game with room info
    LaunchedEffect(roomCode, playerId) {
        viewModel.initGame(roomCode, playerId)
    }

    // Observe game status to navigate to Victory or Game Over screens
    LaunchedEffect(uiState.status) {
        when (uiState.status) {
            GameStatus.WON -> {
                navController.navigate(Screen.Victory.createRoute(uiState.balance, uiState.turn)) {
                    popUpTo(Screen.Game.route) { inclusive = true }
                }
            }
            GameStatus.LOST -> {
                navController.navigate(Screen.GameOver.createRoute(uiState.balance, uiState.turn)) {
                    popUpTo(Screen.Game.route) { inclusive = true }
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Header ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Turn: ${uiState.turn}", fontWeight = FontWeight.Bold)
                    Text("Target: $${uiState.target}", color = Color.Gray)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = uiState.activePlayerName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.isMyTurn) Color(0xFF4CAF50) else Color.Gray
                )

                Text(
                    text = "$${uiState.balance}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (uiState.balance <= 0) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text("Current Balance")
            }
        }

        // --- Body (Event Log) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 24.dp)
                .fillMaxWidth()
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = uiState.eventLog,
                fontSize = 18.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // --- Controls ---
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Actions only enabled if it's the player's turn and the game is active
            val enabled = uiState.status == GameStatus.PLAYING && uiState.isMyTurn

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onSave() },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SAVE (+5%)")
                }
                Button(
                    onClick = { viewModel.onSpend() },
                    enabled = enabled,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SPEND (-$150)")
                }
            }
            
            Button(
                onClick = { viewModel.onInvest() },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAA520)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("INVEST ($200)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Footer ---
            OutlinedButton(
                onClick = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("QUIT GAME")
            }
        }
    }
}

// Helper to safely find the Activity from Context
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
