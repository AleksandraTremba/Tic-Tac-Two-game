package com.taltech.ee.tic_tac_two

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity(){
    private lateinit var stopMusicButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        MusicPlayerHelper.initialize(this)
        stopMusicButton = findViewById(R.id.stopMusicButton)
        updateButtonText()

        stopMusicButton.setOnClickListener {
            toggleMusic()
            updateButtonText()
        }
    }

    private fun toggleMusic() {
        if (MusicPlayerHelper.isPlaying()) {
            MusicPlayerHelper.stopMusic()
        } else {
            MusicPlayerHelper.startMusic()
        }
    }

    private fun updateButtonText() {
        val isMusicOn = MusicPlayerHelper.isPlaying()
        stopMusicButton.text = if (isMusicOn) "Stop Music" else "Start Music"
    }
}