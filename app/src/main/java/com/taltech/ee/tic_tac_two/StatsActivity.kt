package com.taltech.ee.tic_tac_two

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class StatsActivity : AppCompatActivity() {
    private lateinit var databaseHelper: StatisticsDatabaseHelper

    private lateinit var textXWins: TextView
    private lateinit var textOWins: TextView
    private lateinit var textHumanWins: TextView
    private lateinit var textBotWins: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_stats)

        // Initialize Database Helper
        databaseHelper = StatisticsDatabaseHelper(this)

        // Get TextViews
        textXWins = findViewById(R.id.textXWins)
        textOWins = findViewById(R.id.textOWins)
        textHumanWins = findViewById(R.id.textHumanWins)
        textBotWins = findViewById(R.id.textBotWins)

        // Load stats from the database
        val stats = databaseHelper.loadStats()

        // Display stats, defaulting to 0 if stats are null
        textXWins.text = "X Wins: ${stats?.xWins ?: 0}"
        textOWins.text = "O Wins: ${stats?.oWins ?: 0}"
        textHumanWins.text = "Human Wins: ${stats?.humanWins ?: 0}"
        textBotWins.text = "Bot Wins: ${stats?.botWins ?: 0}"
    }
}