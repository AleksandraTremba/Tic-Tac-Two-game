package com.taltech.ee.tic_tac_two

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var stopMusicButton: Button
    private lateinit var toggleSoundButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var isMusicPaused = false // Track whether music is paused

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("TicTacToeSettings", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        stopMusicButton = findViewById(R.id.stopMusicButton)
        toggleSoundButton = findViewById(R.id.toggleSoundButton)

        // Get the current state of the music (paused or playing) from shared preferences
        isMusicPaused = sharedPreferences.getBoolean("isMusicPaused", false)

        // Update button text based on music state
        updateMusicButtonText()

        stopMusicButton.setOnClickListener {
            toggleMusic()
            updateMusicButtonText()
        }

        toggleSoundButton.setOnClickListener {
            toggleSoundEffects()
            updateSoundButtonText()
        }
    }

    private fun toggleMusic() {
        val musicIntent = Intent(this, MusicService::class.java)
        if (isMusicPaused) {
            musicIntent.action = "RESUME_MUSIC"
            startService(musicIntent)
        } else {
            musicIntent.action = "PAUSE_MUSIC"
            startService(musicIntent)
        }
        isMusicPaused = !isMusicPaused // Toggle the state
    }

    private fun toggleSoundEffects() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        editor.putBoolean("soundEffectsEnabled", !soundEffectsEnabled).apply()
    }

    private fun updateMusicButtonText() {
        // Update the button text depending on whether music is paused
        stopMusicButton.text = if (isMusicPaused) "Resume Music" else "Pause Music"
    }

    private fun updateSoundButtonText() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        toggleSoundButton.text = if (soundEffectsEnabled) "Turn Sounds Off" else "Turn Sounds On"
    }
}
