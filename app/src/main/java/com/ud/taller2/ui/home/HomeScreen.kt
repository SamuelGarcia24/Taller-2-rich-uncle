package com.ud.taller2.ui.home

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.ud.taller2.navigation.Screen
import com.ud.taller2.utils.BackgroundMusic
import com.ud.taller2.utils.SoundManager
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current

    // Sound managers
    val soundManager = remember { SoundManager(context) }
    val backgroundMusic = remember { BackgroundMusic(context) }

    // Animation state
    var buttonsVisible by remember { mutableStateOf(false) }

    // Initialize sounds and animations
    LaunchedEffect(Unit) {
        soundManager.initSounds()
        backgroundMusic.start()

        // Animation delay
        delay(500)
        buttonsVisible = true
    }

    // Cleanup on exit
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
            backgroundMusic.stop()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image from res/drawable (NO BLUR)
        Image(
            painter = painterResource(R.drawable.background_home),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient overlay for better button contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Main Content - Buttons at bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Buttons Section with animation
            AnimatedVisibility(
                visible = buttonsVisible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 100 })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Create Room Button
                    Button(
                        onClick = {
                            soundManager.playClickSound()
                            navController.navigate(Screen.CreateRoom.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(65.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🏦",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "CREATE ROOM",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    // Join Room Button
                    Button(
                        onClick = {
                            soundManager.playClickSound()
                            navController.navigate(Screen.JoinRoom.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(65.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "🔑",
                                fontSize = 28.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "JOIN ROOM",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Exit Button
                    OutlinedButton(
                        onClick = {
                            soundManager.playClickSound()
                            backgroundMusic.stop()
                            (context as? Activity)?.finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(25.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp
                        )
                    ) {
                        Text(
                            text = "EXIT",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 4.sp
                        )
                    }
                }
            }
        }
    }
}