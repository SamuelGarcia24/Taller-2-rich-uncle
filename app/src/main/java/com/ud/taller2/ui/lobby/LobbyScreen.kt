package com.ud.taller2.ui.lobby

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ud.taller2.R
import com.ud.taller2.data.model.PlayerRoom
import com.ud.taller2.navigation.Screen
import com.ud.taller2.utils.SoundManager

@Composable
fun LobbyScreen(
    navController: NavController,
    roomCode: String,
    playerId: String,
    viewModel: LobbyViewModel = viewModel()
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    val uiState by viewModel.uiState.collectAsState()

    var showLeaveDialog by remember { mutableStateOf(false) }

    // Load room data
    LaunchedEffect(roomCode) {
        soundManager.initSounds()
        viewModel.loadRoom(roomCode)
    }

    // Navigate to game when status changes to playing
    LaunchedEffect(uiState.room?.status) {
        if (uiState.room?.status == "playing") {
            navController.navigate(Screen.Game.createRoute(roomCode, playerId)) {
                popUpTo(Screen.Lobby.route) { inclusive = true }
            }
        }
    }

    val room = uiState.room
    val playersMap = room?.players ?: emptyMap()
    val isHost = room?.hostId == playerId
    val currentPlayer = playersMap[playerId]
    val isReady = currentPlayer?.ready ?: false
    
    val playersList = playersMap.values.toList()
    val allPlayersReady = playersList.isNotEmpty() && playersList.all { it.ready }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.background_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)))

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "ROOM: $roomCode", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "PLAYERS (${playersList.size}/3)", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(playersMap.toList()) { (id, player) ->
                    PlayerCard(
                        player = player,
                        isHost = id == room?.hostId,
                        isCurrentPlayer = id == playerId
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Control Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // READY Button for everyone (including host if they want to toggle)
                Button(
                    onClick = { 
                        soundManager.playClickSound()
                        viewModel.setPlayerReady(roomCode, playerId, !isReady) 
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReady) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isReady) "SET NOT READY" else "SET READY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // START GAME Button - Only visible to host
                if (isHost) {
                    Button(
                        onClick = { 
                            soundManager.playClickSound()
                            viewModel.startGame(roomCode) 
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = allPlayersReady && playersList.size >= 1, // Min 1 player for easy testing
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3),
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (allPlayersReady) "START GAME" else "WAITING FOR READY...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { showLeaveDialog = true }) {
                Text("LEAVE ROOM", color = Color.Red)
            }
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Leave Room?") },
            text = { Text("Are you sure you want to leave?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.leaveRoom(roomCode, playerId)
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }) { Text("LEAVE", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) { Text("CANCEL") }
            }
        )
    }
}

@Composable
fun PlayerCard(player: PlayerRoom, isHost: Boolean, isCurrentPlayer: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlayer) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(if (player.ready) Color.Green else Color.Red))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(player.name + (if (isCurrentPlayer) " (You)" else ""), color = Color.White, fontWeight = FontWeight.Bold)
                if (isHost) Text("Host", color = Color(0xFFFFD700), fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(if (player.ready) "READY" else "NOT READY", color = if (player.ready) Color.Green else Color.Red)
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
