package com.ud.taller2.ui.lobby

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ud.taller2.R
import com.ud.taller2.data.model.PlayerRoom
import com.ud.taller2.navigation.Screen
import com.ud.taller2.utils.BackgroundMusic
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
    val backgroundMusic = remember { BackgroundMusic(context) }

    val uiState by viewModel.uiState.collectAsState()

    var showLeaveDialog by remember { mutableStateOf(false) }

    // Initialize sounds and load room
    LaunchedEffect(Unit) {
        soundManager.initSounds()
        viewModel.loadRoom(roomCode)
    }

    // Navigate to game when status changes to playing
    LaunchedEffect(uiState.room?.status) {
        if (uiState.room?.status == "playing") {
            navController.navigate(
                Screen.Game.createRoute(roomCode, playerId)
            ) {
                popUpTo(Screen.Lobby.route) { inclusive = true }
            }
        }
    }

    // Check if current player is host
    val isHost = uiState.room?.hostId == playerId
    val currentPlayer = uiState.room?.players?.get(playerId)
    val isReady = currentPlayer?.isReady ?: false
    val allPlayersReady = uiState.players.all { it.isReady } && uiState.players.isNotEmpty()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.background_home),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Back button
        IconButton(
            onClick = {
                soundManager.playClickSound()
                showLeaveDialog = true
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Text(
                text = "←",
                fontSize = 32.sp,
                color = Color.White
            )
        }

        // Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Room Code Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ROOM CODE",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = roomCode,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        letterSpacing = 8.sp
                    )

                    Text(
                        text = "Share this code with your friends",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Players Section
            Text(
                text = "PLAYERS (${uiState.players.size}/3)",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Players List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.players) { player ->
                    PlayerCard(
                        player = player,
                        isHost = uiState.room?.hostId == uiState.room?.players?.entries
                            ?.find { it.value == player }?.key,
                        isCurrentPlayer = uiState.room?.players?.entries
                            ?.find { it.value == player }?.key == playerId
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Ready Button / Start Game Button
            if (isHost) {
                // Host sees Start Game button
                Button(
                    onClick = {
                        soundManager.playClickSound()
                        viewModel.startGame(roomCode)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allPlayersReady && uiState.players.size >= 2)
                            Color(0xFF4CAF50)
                        else
                            Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = allPlayersReady && uiState.players.size >= 2
                ) {
                    Text(
                        text = if (allPlayersReady) "START GAME" else "WAITING FOR PLAYERS...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                if (!allPlayersReady && uiState.players.size >= 2) {
                    Text(
                        text = "Waiting for all players to be ready",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else if (uiState.players.size < 2) {
                    Text(
                        text = "Need at least 2 players to start",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                // Non-host sees Ready button
                Button(
                    onClick = {
                        soundManager.playClickSound()
                        viewModel.setPlayerReady(roomCode, playerId, !isReady)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isReady) Color(0xFFFF9800) else Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isReady) "NOT READY" else "READY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = if (isReady) "Waiting for host to start..." else "Click ready when you're prepared",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Leave Room Dialog
        if (showLeaveDialog) {
            AlertDialog(
                onDismissRequest = { showLeaveDialog = false },
                title = {
                    Text(
                        text = "Leave Room?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to leave this room?",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            soundManager.playClickSound()
                            viewModel.leaveRoom(roomCode, playerId)
                            navController.popBackStack(Screen.Home.route, inclusive = false)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF44336)
                        )
                    ) {
                        Text("LEAVE")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            soundManager.playClickSound()
                            showLeaveDialog = false
                        }
                    ) {
                        Text("CANCEL", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF1E1E1E),
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
    }
}

@Composable
fun PlayerCard(
    player: PlayerRoom,
    isHost: Boolean,
    isCurrentPlayer: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlayer)
                Color(0xFF2196F3).copy(alpha = 0.3f)
            else
                Color(0xFF2A2A2A).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Player avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isHost) Color(0xFFFFD700) else Color(0xFF4CAF50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = player.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )

                        if (isHost) {
                            Card(
                                shape = RoundedCornerShape(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFD700)
                                )
                            ) {
                                Text(
                                    text = "HOST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (isCurrentPlayer) {
                            Card(
                                shape = RoundedCornerShape(4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF2196F3)
                                )
                            ) {
                                Text(
                                    text = "YOU",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Starting Money: $1000",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            // Ready indicator
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(
                        if (player.isReady) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
            )
        }
    }
}