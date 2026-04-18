package com.ud.taller2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ud.taller2.navigation.NavigationGraph
import com.ud.taller2.utils.BackgroundMusic

class MainActivity : ComponentActivity() {

    private lateinit var backgroundMusic: BackgroundMusic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize background music once for the whole app
        backgroundMusic = BackgroundMusic(this)
        backgroundMusic.start()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop music when app is closed
        backgroundMusic.stop()
    }
}