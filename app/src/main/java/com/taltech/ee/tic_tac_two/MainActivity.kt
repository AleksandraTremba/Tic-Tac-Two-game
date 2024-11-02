package com.taltech.ee.tic_tac_two

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val buttonMap = mutableMapOf<Int, Button>()
    private var currentPlayer = "X"
    private val gameState = Array(5) { Array(5) { "" } }
    private lateinit var imageViewX: ImageView
    private lateinit var imageViewO: ImageView
    private var currentGridCenter = 13

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imageViewX = findViewById(R.id.imageView)
        imageViewO = findViewById(R.id.imageView2)
        updatePlayerImages()

        val menuButton = findViewById<Button>(R.id.menuButton)
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            resetGame()
        }

        for (i in 1..25) {
            val buttonId = resources.getIdentifier("game_button$i", "id", packageName)
            val button = findViewById<Button>(buttonId)
            buttonMap[i] = button
            enable3x3CenterGrid()

            button.setOnClickListener {
                onButtonClicked(button, i)
            }

            button.setOnLongClickListener {
                val tag = it.tag.toString().toInt()
                enable3x3Grid(tag)
                true
            }
        }
    }

    private fun updatePlayerImages() {
        if (currentPlayer == "X") {
            imageViewX.setImageResource(R.drawable.x_active) // Set active for player X
            imageViewO.setImageResource(R.drawable.o_notactive) // Set inactive for player O
        } else {
            imageViewX.setImageResource(R.drawable.x_notactive) // Set inactive for player X
            imageViewO.setImageResource(R.drawable.o_active) // Set active for player O
        }
    }

    private fun onButtonClicked(button: Button, buttonIndex: Int) {
        // Only proceed if the button is not already clicked
        if (button.isClickable) {
            if (gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] == "") {
                // Update game state and button text
                gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] = currentPlayer
                button.text = currentPlayer

                // Check for a win
                if (checkWin()) {
                    Toast.makeText(this, "Player $currentPlayer wins!", Toast.LENGTH_SHORT).show()
                    resetGame()
                    return
                }

                // Switch player
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                updatePlayerImages()
            }
        }
    }

    private fun checkWin(): Boolean {
        val centerRow = (currentGridCenter - 1) / 5
        val centerColumn = (currentGridCenter - 1) % 5

        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        // Check rows and columns
        for (row in minRow..maxRow) {
            if ((minColumn..maxColumn).all { gameState[row][it] == currentPlayer }) return true // Check row
        }
        for (col in minColumn..maxColumn) {
            if ((minRow..maxRow).all { gameState[it][col] == currentPlayer }) return true // Check column
        }

        // Check diagonals
        if (centerRow == centerColumn) {
            if ((minRow..maxRow).all { gameState[it][it + (minColumn - minRow)] == currentPlayer }) return true
        }
        if (centerRow + centerColumn == 2) {
            if ((minRow..maxRow).all { gameState[it][maxColumn - (it - minRow)] == currentPlayer }) return true
        }

        return false
    }


    private fun resetGame() {
        // Clear game state and reset buttons
        for (button in buttonMap.values) {
            button.text = ""
            button.isClickable
        }
        currentPlayer = "X" // Reset to Player X
        for (i in gameState.indices) {
            for (j in gameState[i].indices) {
                gameState[i][j] = "" // Reset game state
            }
        }
        enable3x3CenterGrid() // Re-enable center grid
        updatePlayerImages()
    }

    private fun enable3x3CenterGrid() {
        // Get the colors from the theme
        val buttonColor = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, this, true)
        }.data

        val buttonColorGrid = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, this, true)
        }.data

        for (button in buttonMap.values) {
            button.isClickable = false
            button.setBackgroundColor(buttonColor)
        }

        // Calculate the center button's row and column
        val centerRow = (13 - 1) / 5
        val centerColumn = (13 - 1) % 5

        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        for (row in minRow..maxRow) {
            for (col in minColumn..maxColumn) {
                val buttonTag = row * 5 + col + 1
                buttonMap[buttonTag]?.apply {
                    isClickable = true
                    setBackgroundColor(buttonColorGrid)
                }
            }
        }
    }



    private fun enable3x3Grid(tag: Int) {
        val buttonColor = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, this, true)
        }.data

        val buttonColorGrid = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, this, true)
        }.data

        currentGridCenter = tag
        // First, disable all buttons
        for (button in buttonMap.values) {
            button.isClickable = false
            button.setBackgroundColor(buttonColor)
        }
        var centerTag = when (tag) {
            1, 2, 6 -> 7
            4, 5, 10 -> 9
            20, 24, 25 -> 19
            16, 21, 22 -> 17
            3 -> 8
            15 -> 14
            23 -> 18
            11 -> 12
            else -> tag
        }

        // Calculate the center button's row and column
        val centerRow = (centerTag - 1) / 5
        val centerColumn = (centerTag - 1) % 5

        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        // Enable the 3x3 grid around the center
        for (row in minRow..maxRow) {
            for (col in minColumn..maxColumn) {
                val buttonTag = row * 5 + col + 1
                buttonMap[buttonTag]?.apply {
                    isClickable = true
                    // Change the button color to the "enabled" color
                    setBackgroundColor(buttonColorGrid)
                }
            }
        }
        currentPlayer = if (currentPlayer == "X") "O" else "X"
        updatePlayerImages()
    }
}