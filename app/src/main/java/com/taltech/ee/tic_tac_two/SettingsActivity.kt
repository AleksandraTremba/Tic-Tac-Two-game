package com.taltech.ee.tic_tac_two

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    private lateinit var toggleSoundButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("TicTacToeSettings", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        toggleSoundButton = findViewById(R.id.toggleSoundButton)

        // Ensure the button text is set based on current sound setting
        updateSoundButtonText()

        toggleSoundButton.setOnClickListener {
            toggleSoundEffects()
            updateSoundButtonText()
        }
    }


    private fun toggleSoundEffects() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        editor.putBoolean("soundEffectsEnabled", !soundEffectsEnabled).apply()
    }

    private fun updateSoundButtonText() {
        val soundEffectsEnabled = sharedPreferences.getBoolean("soundEffectsEnabled", true)
        toggleSoundButton.text = if (soundEffectsEnabled) "Turn Sounds Off" else "Turn Sounds On"
    }
}
