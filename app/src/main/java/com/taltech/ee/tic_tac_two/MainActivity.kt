package com.taltech.ee.tic_tac_two

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: GameViewModel
    private val buttonMap = mutableMapOf<Int, Button>()
    private var currentPlayer = "X"
    private var gameState = Array(5) { Array(5) { "" } }
    private lateinit var imageViewX: Button
    private lateinit var imageViewO: Button
    private var currentGridCenter = 13
    private var isBotActive = false

    private var xWins = 0
    private var oWins = 0

    private var humanWins = 0
    private var botWins = 0

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var turnMediaPlayer: MediaPlayer? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Use viewModel to get and set data
        currentPlayer = viewModel.currentPlayer
        gameState = viewModel.gameState
        xWins = viewModel.xWins
        oWins = viewModel.oWins
        humanWins = viewModel.humanWins
        botWins = viewModel.botWins

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToeStats", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Load previous stats
        xWins = sharedPreferences.getInt("xWins", 0)
        oWins = sharedPreferences.getInt("oWins", 0)
        humanWins = sharedPreferences.getInt("humanWins", 0)
        botWins = sharedPreferences.getInt("botWins", 0)

        val isBotEnabled = intent.getBooleanExtra("enableBot", false)
        isBotActive = isBotEnabled

//        MusicPlayerHelper.initialize(this)
//        MusicPlayerHelper.startMusic()
        SoundEffectHelper.initialize(this)


        imageViewX = findViewById<Button>(R.id.x_button)
        imageViewO = findViewById<Button>(R.id.o_button)
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
                if (isBotActive && currentPlayer == "O") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        botTurn()
                    }, 1000)
                }
                true
            }
        }
    }



    private fun botTurn() {
        Log.d("BOT", "bot started the turn")
        // Determine if the bot will hold
        val shouldHold = Random.nextInt(100) < 30

        if (shouldHold) {
            val randomButton = (1..25).random()
            enable3x3Grid(randomButton)
            Log.d("BOT", "bot held $randomButton")
            currentPlayer = "X"
            updatePlayerImages()
            return
        }
        Log.d("BOT", "bot didnt hold any button")


        // Get the center row and column of the current grid
        val centerRow = (currentGridCenter - 1) / 5
        val centerColumn = (currentGridCenter - 1) % 5

        // Determine the bounds for the random selection
        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        var selectedButton: Int
        var buttonIsEmpty: Boolean
        var randomRow: Int
        var randomColumn: Int
        do {
            randomRow = Random.nextInt(minRow, maxRow + 1)
            randomColumn = Random.nextInt(minColumn, maxColumn + 1)
            selectedButton = randomRow * 5 + randomColumn + 1
            buttonIsEmpty = gameState[randomRow][randomColumn] == ""
        } while (!buttonIsEmpty)

        // Update game state and button text for the bot's turn
        val button = buttonMap[selectedButton]
        button?.let {
            it.text = "O" // Set the bot's symbol
            gameState[randomRow][randomColumn] = "O" // Update the game state
            SoundEffectHelper.playOTurn(this)

            // Check for a win after the bot's move
            if (checkWin()) {
                Toast.makeText(this, "Player O wins!", Toast.LENGTH_SHORT).show()
                resetGame()
                return
            }
        }

        Log.d("BOT", "bot made the turn")


        currentPlayer = "X"
        updatePlayerImages()
    }



    private fun updatePlayerImages() {
        val buttonColor = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, this, true)
        }.data

        val buttonColorGrid = TypedValue().apply {
            theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, this, true)
        }.data
        if (currentPlayer == "X") {
            imageViewX.setBackgroundColor(buttonColorGrid) // Set active for player X
            imageViewO.setBackgroundColor(buttonColor) // Set inactive for player O
        } else {
            imageViewX.setBackgroundColor(buttonColor) // Set inactive for player X
            imageViewO.setBackgroundColor(buttonColorGrid) // Set active for player O
        }
    }

    private fun onButtonClicked(button: Button, buttonIndex: Int) {
        if (isBotActive == false) {
            // Only proceed if the button is not already clicked
            if (button.isClickable) {
                if (gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] == "") {
                    // Update game state and button text
                    gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] = currentPlayer

                    if (currentPlayer == "X") {
                        button.text = currentPlayer
                        SoundEffectHelper.playXTurn(this)
                    } else {
                        button.text = currentPlayer
                        SoundEffectHelper.playOTurn(this)
                    }

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
        } else {
            if (currentPlayer == "X") {
                if (button.isClickable) {
                    if (gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] == "") {
                        // Update game state and button text
                        gameState[(buttonIndex - 1) / 5][(buttonIndex - 1) % 5] = currentPlayer
                        button.text = currentPlayer
                        SoundEffectHelper.playXTurn(this)

                        // Check for a win
                        if (checkWin()) {
                            Toast.makeText(this, "Player $currentPlayer wins!", Toast.LENGTH_SHORT).show()
                            resetGame()
                            return
                        }

                        // Schedule the bot's turn after a short delay
                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                        updatePlayerImages()

                        // Adding a delay before bot's turn
                        Handler(Looper.getMainLooper()).postDelayed({
                            botTurn()
                        }, 500) // Delay in milliseconds
                    }
                }
            }
        }
    }


    private fun checkWin(): Boolean {
        Log.d("WIN", "Just checked for the win")
        val centerRow = (currentGridCenter - 1) / 5
        val centerColumn = (currentGridCenter - 1) % 5

        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        var winDetected = false

        // Check rows and columns
        for (row in minRow..maxRow) {
            Log.d("WIN", "Checking row $row for player $currentPlayer")
            for (col in minColumn..maxColumn) {
                Log.d("WIN", "Cell [$row][$col] = ${gameState[row][col]}")
            }
            if ((minColumn..maxColumn).all { gameState[row][it] == currentPlayer }) {
                Log.d("WIN", "Row $row is fully occupied by $currentPlayer. Win detected.")
                winDetected = true
            }
        }

        for (col in minColumn..maxColumn) {
            Log.d("WIN", "Checking column $col for player $currentPlayer")
            for (row in minRow..maxRow) {
                Log.d("WIN", "Cell [$row][$col] = ${gameState[row][col]}")
            }
            if ((minRow..maxRow).all { gameState[it][col] == currentPlayer }) {
                Log.d("WIN", "Column $col is fully occupied by $currentPlayer. Win detected.")
                winDetected = true
            }
        }

        // Check main diagonal (top-left to bottom-right)
        Log.d("WIN", "Checking main diagonal for player $currentPlayer")
        if ((minRow..maxRow).all {
                val row = it
                val col = it - minRow + minColumn
                Log.d("WIN", "Cell [$row][$col] = ${gameState[row][col]}")
                gameState[row][col] == currentPlayer
            }) {
            Log.d("WIN", "Main diagonal is fully occupied by $currentPlayer. Win detected.")
            winDetected = true
        }

        // Check anti-diagonal (top-right to bottom-left)
        Log.d("WIN", "Checking anti-diagonal for player $currentPlayer")
        if ((minRow..maxRow).all {
                val row = it
                val col = maxColumn - (it - minRow)
                Log.d("WIN", "Cell [$row][$col] = ${gameState[row][col]}")
                gameState[row][col] == currentPlayer
            }) {
            Log.d("WIN", "Anti-diagonal is fully occupied by $currentPlayer. Win detected.")
            winDetected = true
        }

        if (winDetected) {
            // Increment the appropriate win counter
            if (isBotActive) {
                if (currentPlayer == "O") { // Bot wins
                    botWins++
                    editor.putInt("botWins", botWins).apply()
                } else { // Human wins
                    humanWins++
                    editor.putInt("humanWins", humanWins).apply()
                }
            } else { // Human vs Human
                if (currentPlayer == "X") {
                    xWins++
                    editor.putInt("xWins", xWins).apply()
                } else {
                    oWins++
                    editor.putInt("oWins", oWins).apply()
                }
            }
            return true
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

        checkWin()

        currentPlayer = if (currentPlayer == "X") "O" else "X"
        updatePlayerImages()
    }

    fun enableBot() {
        if (isBotActive == true) {
            isBotActive = false
        } else isBotActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicPlayerHelper.release()
        SoundEffectHelper.release()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the game state, including the game grid and the current player
        outState.putSerializable("gameState", gameState)
        outState.putString("currentPlayer", currentPlayer)
        outState.putInt("currentGridCenter", currentGridCenter)

        // Save any other states like wins if needed
        outState.putInt("xWins", xWins)
        outState.putInt("oWins", oWins)
        outState.putInt("humanWins", humanWins)
        outState.putInt("botWins", botWins)

    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        // Restore the game state
        gameState = savedInstanceState.getSerializable("gameState") as Array<Array<String>>
        currentPlayer = savedInstanceState.getString("currentPlayer", "X") ?: "X"
        currentGridCenter = savedInstanceState.getInt("currentGridCenter", 13)

        // Restore the win counters if needed
        xWins = savedInstanceState.getInt("xWins", 0)
        oWins = savedInstanceState.getInt("oWins", 0)
        humanWins = savedInstanceState.getInt("humanWins", 0)
        botWins = savedInstanceState.getInt("botWins", 0)

        // Update the UI (button texts and colors) after restoring the state
        updatePlayerImages()

        // Restore the buttons' texts
        for (i in 1..25) {
            val button = buttonMap[i]
            button?.text = gameState[(i - 1) / 5][(i - 1) % 5]
            button?.isClickable = gameState[(i - 1) / 5][(i - 1) % 5].isEmpty()
        }
    }

}