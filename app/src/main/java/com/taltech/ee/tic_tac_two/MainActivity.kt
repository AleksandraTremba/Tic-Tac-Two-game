package com.taltech.ee.tic_tac_two

import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var dbHelper: GameDatabaseHelper
    private lateinit var statsDbHelper: StatisticsDatabaseHelper

    private var xMoves = 0
    private var oMoves = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("TicTacToeStats", MODE_PRIVATE)
        editor = sharedPreferences.edit()


        val isBotEnabled = intent.getBooleanExtra("enableBot", false)
        isBotActive = isBotEnabled

        SoundEffectHelper.initialize(this)

        dbHelper = GameDatabaseHelper(this)
        statsDbHelper = StatisticsDatabaseHelper(this)

        val stats = statsDbHelper.loadStats()
        if (stats != null) {
            xWins = stats.xWins
            oWins = stats.oWins
            humanWins = stats.humanWins
            botWins = stats.botWins
        } else {
            xWins = 0
            oWins = 0
            humanWins = 0
            botWins = 0
        }



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

        if (isBotEnabled) {
            Log.d("BOT", "current player: $currentPlayer")
            resetGame() // Reset game state
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
                if (xMoves >= 2 && oMoves >= 2) {
                    val tag = it.tag.toString().toInt()
                    enable3x3Grid(tag)
                    if (checkWin()) {
                        Toast.makeText(this, "$currentPlayer wins!", Toast.LENGTH_SHORT).show()
                        resetGame()
                    }
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                updatePlayerImages()
                if (isBotActive && currentPlayer == "O") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        botTurn()
                    }, 1000)
                }
                }
                true

            }
        }
    }



    private fun botTurn() {
        Log.d("BOT", "bot started the turn")
        if (currentPlayer != "O") return
        if (xMoves >= 2 && oMoves >= 2) {
            // Determine if the bot will hold
            val shouldHold = Random.nextInt(100) < 30

            if (shouldHold) {
                val randomButton = (1..25).random()
                enable3x3Grid(randomButton)
                oMoves++
                Log.d("BOT", "bot held $randomButton")
                currentPlayer = "X"
                updatePlayerImages()
                if (checkWin()) {
                    Toast.makeText(this, "$currentPlayer wins!", Toast.LENGTH_SHORT).show()
                    resetGame()
                    return
                }
                return
            }
            Log.d("BOT", "bot didnt hold any button")
        }


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
        oMoves++

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

                    if (currentPlayer == "X") {
                        xMoves++
                    } else {
                        oMoves++
                    }

                    // Switch player
                    Log.d("LoadGame", "Changes current player in on button cliecked: $currentPlayer")
                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                    Log.d("LoadGame", "Changes current player in on button cliecked: $currentPlayer")
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

                        xMoves++
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
        val tagRow = (currentGridCenter - 1) / 5
        val tagColumn = (currentGridCenter - 1) % 5
        Log.d("WIN", "tag row: $tagRow")
        Log.d("WIN", "tag column: $tagColumn")



        var centerRow = when (tagRow) {
            0 -> 1
            4 -> 3
            else -> tagRow
        }

        var centerColumn = when (tagColumn) {
           0 -> 1
            4 -> 3
            else -> tagColumn
        }

        val minRow = maxOf(0, centerRow - 1)
        val maxRow = minOf(4, centerRow + 1)
        val minColumn = maxOf(0, centerColumn - 1)
        val maxColumn = minOf(4, centerColumn + 1)

        var winDetected = false

        Log.d("WIN", "Current 3x3 grid:")
        for (row in minRow..maxRow) {
            val rowString = (minColumn..maxColumn).joinToString("") { col ->
                if (row in 0..4 && col in 0..4) {
                    when (gameState[row][col]) {
                        "X" -> "X"
                        "O" -> "O"
                        else -> "-"
                    }
                } else {
                    "-"
                }
            }
            Log.d("WIN", rowString)
        }

        //For X player

        // Check rows and columns
        for (row in minRow..maxRow) {
            if ((minColumn..maxColumn).all { gameState[row][it] == "X" }) winDetected = true
        }
        for (col in minColumn..maxColumn) {
            if ((minRow..maxRow).all { gameState[it][col] == "X" }) winDetected = true
        }

        // Check main diagonal (top-left to bottom-right)
        if ((minRow..maxRow).all { gameState[it][it - minRow + minColumn] == "X" }) winDetected = true

        // Check anti-diagonal (top-right to bottom-left)
        if ((minRow..maxRow).all { gameState[it][maxColumn - (it - minRow)] == "X" }) winDetected = true

        //For O player

        // Check rows and columns
        for (row in minRow..maxRow) {
            if ((minColumn..maxColumn).all { gameState[row][it] == "O" }) winDetected = true
        }
        for (col in minColumn..maxColumn) {
            if ((minRow..maxRow).all { gameState[it][col] == "O" }) winDetected = true
        }

        // Check main diagonal (top-left to bottom-right)
        if ((minRow..maxRow).all { gameState[it][it - minRow + minColumn] == "O" }) winDetected = true

        // Check anti-diagonal (top-right to bottom-left)
        if ((minRow..maxRow).all { gameState[it][maxColumn - (it - minRow)] == "O" }) winDetected = true


        if (winDetected) {
            when {
                isBotActive && currentPlayer == "O" -> botWins++
                isBotActive -> humanWins++
                currentPlayer == "X" -> xWins++
                else -> oWins++
            }
            // Save the updated stats to the database
            statsDbHelper.saveStats(xWins, oWins, humanWins, botWins)
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
        xMoves = 0  // Reset X player moves
        oMoves = 0  // Reset O player moves
        for (i in gameState.indices) {
            for (j in gameState[i].indices) {
                gameState[i][j] = "" // Reset game state
            }
        }
        currentGridCenter = 13  // Ensure the center is reset to 13
        enable3x3CenterGrid() // Re-enable center grid
        updatePlayerImages()
        saveGame()
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
    }

    fun enableBot() {
        if (isBotActive == true) {
            isBotActive = false
        } else isBotActive = true
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundEffectHelper.release()
        gameState = Array(5) { Array(5) { "" } }
        buttonMap.clear()

    }

    private fun saveGame() {
        Log.d("LoadGame", "Saving gameState: ${gameState.contentDeepToString()}")
        dbHelper.saveGameState(
            gameState = gameState,
            currentPlayer = currentPlayer,
            gridCenter = currentGridCenter,
            oMoves = oMoves,
            xMoves = xMoves
        )
    }

    private fun loadGame() {
        dbHelper.loadGameState()?.let { data ->
            Log.d("LoadGame", "Loaded data: ${data.gameState.contentDeepToString()}")
            if (data.gameState.isNotEmpty() && data.gameState.size == 5 && data.gameState[0].size == 5) {
                gameState = data.gameState
                currentPlayer = data.currentPlayer
                currentGridCenter = data.gridCenter
                oMoves = data.oMoves
                xMoves = data.xMoves
                updateUIAfterLoad()
                Log.d("LoadGame", "Loaded with current player in bd ${data.currentPlayer} but $currentPlayer and id ${data.id}")
            } else {
                Log.d("LoadGame", "Failed to load game: Data is invalid")
            }
        }
    }


    override fun onPause() {
        super.onPause()
        saveGame()
    }

    override fun onStop() {
        super.onStop()
        saveGame()
    }

    override fun onStart() {
        super.onStart()
        loadGame()
    }

    override fun onResume() {
        super.onResume()
        loadGame()
    }

    private fun updateUIAfterLoad() {
        for (i in 1..25) {
            val button = buttonMap[i]
            button?.text = gameState[(i - 1) / 5][(i - 1) % 5]
            button?.isClickable = gameState[(i - 1) / 5][(i - 1) % 5].isEmpty()
        }
        updatePlayerImages()
        enable3x3Grid(currentGridCenter)
    }

}