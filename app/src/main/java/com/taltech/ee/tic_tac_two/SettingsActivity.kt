package com.taltech.ee.tic_tac_two

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity(){
    private lateinit var stopMusicButton: Button
    private lateinit var toggleSoundButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("TicTacToeSettings", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        stopMusicButton = findViewById(R.id.stopMusicButton)
        toggleSoundButton = findViewById(R.id.toggleSoundButton)

        // Set initial button text based on the current state
        updateSoundButtonText()

        stopMusicButton.setOnClickListener {
            toggleMusic()
            updateButtonText()
        }

        toggleSoundButton.setOnClickListener {
            toggleSoundEffects()
            updateSoundButtonText()
        }
    }

    private fun toggleMusic() {
        if (MusicPlayerHelper.isPlaying()) {
            MusicPlayerHelper.stopMusic()
        } else {
            MusicPlayerHelper.startMusic()
        }
    }

    private fun toggleSoundEffects() {
        // Toggle the sound effects setting
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        editor.putBoolean("soundEffectsEnabled", !soundEffectsEnabled).apply()
    }

    private fun updateButtonText() {
        val isMusicOn = MusicPlayerHelper.isPlaying()
        stopMusicButton.text = if (isMusicOn) "Stop Music" else "Start Music"
    }

    private fun updateSoundButtonText() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        toggleSoundButton.text = if (soundEffectsEnabled) "Turn Sounds Off" else "Turn Sounds On"
    }
}