package com.taltech.ee.tic_tac_two

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MenuActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val botButton = findViewById<Button>(R.id.botButton)
        botButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("enableBot", true)
            startActivity(intent)
        }

        val humanButton = findViewById<Button>(R.id.humanButton)
        humanButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("enableBot", false)
            startActivity(intent)
        }


        val statsButton = findViewById<Button>(R.id.statsButton)
        statsButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
