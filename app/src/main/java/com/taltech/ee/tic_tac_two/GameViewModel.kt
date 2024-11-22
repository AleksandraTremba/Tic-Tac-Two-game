package com.taltech.ee.tic_tac_two

import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var currentPlayer = "X"
    var gameState = Array(5) { Array(5) { "" } }
    var xWins = 0
    var oWins = 0
    var humanWins = 0
    var botWins = 0
}
