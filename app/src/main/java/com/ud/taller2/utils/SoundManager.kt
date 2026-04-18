package com.ud.taller2.utils

import android.content.Context
import android.media.MediaPlayer
import com.ud.taller2.R

/**
 * Manages sound effects for the game
 */
class SoundManager(private val context: Context) {

    private var clickSoundPlayer: MediaPlayer? = null

    /**
     * Initialize sound effects
     */
    fun initSounds() {
        clickSoundPlayer = MediaPlayer.create(context, R.raw.click_sound)
        clickSoundPlayer?.setVolume(0.5f, 0.5f)
    }

    /**
     * Play button click sound effect
     */
    fun playClickSound() {
        clickSoundPlayer?.seekTo(0)
        clickSoundPlayer?.start()
    }

    /**
     * Release resources
     */
    fun release() {
        clickSoundPlayer?.release()
        clickSoundPlayer = null
    }
}