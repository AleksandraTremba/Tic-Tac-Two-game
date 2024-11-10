package com.taltech.ee.tic_tac_two

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.JobIntentService

class MusicService : JobIntentService() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = true

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.coolandwarm).apply {
            isLooping = true
            start() // Start music initially
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "PAUSE_MUSIC" -> pauseMusic()
            "RESUME_MUSIC" -> resumeMusic()
            "STOP_MUSIC" -> stopMusic() // Fully stops the music and releases resources
            else -> if (mediaPlayer?.isPlaying == false && !isPaused) {
                mediaPlayer?.start()
            }
        }
        return START_STICKY
    }

    private fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            isPaused = true
            updateMusicStateInPreferences(true) // Music is paused
        }
    }

    private fun resumeMusic() {
        if (isPaused) {
            mediaPlayer?.start()
            isPaused = false
            updateMusicStateInPreferences(false) // Music is playing
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopSelf()
        updateMusicStateInPreferences(false) // Music is stopped
    }

    private fun updateMusicStateInPreferences(isPaused: Boolean) {
        val preferences = getSharedPreferences("TicTacToeSettings", MODE_PRIVATE)
        preferences.edit().putBoolean("isMusicPaused", isPaused).apply()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onHandleWork(intent: Intent) {
        when (intent.action) {
            "PAUSE_MUSIC" -> pauseMusic()
            "RESUME_MUSIC" -> resumeMusic()
            "STOP_MUSIC" -> stopMusic() // Fully stops the music and releases resources
        }

    }

}