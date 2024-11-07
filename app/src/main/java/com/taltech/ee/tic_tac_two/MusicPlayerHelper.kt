package com.taltech.ee.tic_tac_two

import android.content.Context
import android.media.MediaPlayer

object MusicPlayerHelper {
    private var mediaPlayer: MediaPlayer? = null
    private var isInitialized = false
    private var shouldResumeMusic = true


    fun initialize(context: Context) {
        if (!isInitialized) {
            mediaPlayer = MediaPlayer.create(context, R.raw.coolandwarm).apply {
                isLooping = true
            }
            isInitialized = true
        }
    }

    fun startMusic() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun stopMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        shouldResumeMusic = false
    }

    fun pauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
    }

    fun resumeMusic() {
        if (shouldResumeMusic) {
            startMusic()
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun release() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
            isInitialized = false
        }
    }
}
