package com.taltech.ee.tic_tac_two

import android.annotation.SuppressLint
import android.content.Intent
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
    private var isMusicPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("TicTacToeSettings", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        stopMusicButton = findViewById(R.id.stopMusicButton)
        toggleSoundButton = findViewById(R.id.toggleSoundButton)

        updateSoundButtonText()
        updateButtonText()

        stopMusicButton.setOnClickListener {
            isMusicPlaying = !isMusicPlaying
            if (isMusicPlaying) {
                startService(Intent(this, MusicService::class.java))
            } else {
                stopService(Intent(this, MusicService::class.java))
            }
            updateButtonText()
        }

        toggleSoundButton.setOnClickListener {
            toggleSoundEffects()
            updateSoundButtonText()
        }
    }

    private fun toggleSoundEffects() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        editor.putBoolean("soundEffectsEnabled", !soundEffectsEnabled).apply()
    }

    private fun updateButtonText() {
        stopMusicButton.text = if (isMusicPlaying) "Stop Music" else "Start Music"
    }

    private fun updateSoundButtonText() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        toggleSoundButton.text = if (soundEffectsEnabled) "Turn Sounds Off" else "Turn Sounds On"
    }
}