package com.taltech.ee.tic_tac_two

import android.content.Context
import android.media.MediaPlayer
import android.content.SharedPreferences

object SoundEffectsHelper {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences("TicTacToeSettings", Context.MODE_PRIVATE)
    }

    fun playXTurn(context: Context) {
        if (isSoundEnabled()) {
            playSound(context, R.raw.xturn)
        }
    }

    fun playOTurn(context: Context) {
        if (isSoundEnabled()) {
            playSound(context, R.raw.oturn)
        }
    }

    private fun playSound(context: Context, soundResId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, soundResId)
        mediaPlayer?.start()
    }

    private fun isSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean("soundEffectsEnabled", true)
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

}
