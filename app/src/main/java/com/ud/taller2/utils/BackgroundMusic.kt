package com.ud.taller2.utils

import android.content.Context
import android.media.MediaPlayer
import com.ud.taller2.R

/**
 * Manages background music for the game
 */
class BackgroundMusic(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isMusicEnabled = true

    /**
     * Initialize and start background music
     */
    fun start() {
        if (!isMusicEnabled) return

        mediaPlayer = MediaPlayer.create(context, R.raw.background_music).apply {
            isLooping = true
            setVolume(0.3f, 0.3f) // 30% volume
            start()
        }
    }

    /**
     * Pause background music
     */
    fun pause() {
        mediaPlayer?.pause()
    }

    /**
     * Resume background music
     */
    fun resume() {
        if (isMusicEnabled) {
            mediaPlayer?.start()
        }
    }

    /**
     * Stop and release background music
     */
    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Toggle music on/off
     */
    fun setEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            if (mediaPlayer == null) {
                start()
            } else {
                resume()
            }
        } else {
            pause()
        }
    }
}