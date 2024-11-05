package com.taltech.ee.tic_tac_two

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class StatsActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var textXWins: TextView
    private lateinit var textOWins: TextView
    private lateinit var textHumanWins: TextView
    private lateinit var textBotWins: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_stats)
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToeStats", MODE_PRIVATE)

        // Get TextViews
        textXWins = findViewById(R.id.textXWins)
        textOWins = findViewById(R.id.textOWins)
        textHumanWins = findViewById(R.id.textHumanWins)
        textBotWins = findViewById(R.id.textBotWins)

        // Load stats
        val xWins = sharedPreferences.getInt("xWins", 0)
        val oWins = sharedPreferences.getInt("oWins", 0)
        val humanWins = sharedPreferences.getInt("humanWins", 0)
        val botWins = sharedPreferences.getInt("botWins", 0)

        // Display stats
        textXWins.text = "X Wins: $xWins"
        textOWins.text = "O Wins: $oWins"
        textHumanWins.text = "Human Wins: $humanWins"
        textBotWins.text = "Bot Wins: $botWins"
    }
}