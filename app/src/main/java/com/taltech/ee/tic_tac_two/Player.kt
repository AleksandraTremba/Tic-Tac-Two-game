package com.taltech.ee.tic_tac_two

enum class Player(val symbol: String) {
    X("X"),
    O("O"),
    NONE(""); // Represents an empty space

    // Returns true if the player is X
    fun isX() = this == X

    // Returns true if the player is O
    fun isO() = this == O

    // Returns true if the space is empty
    fun isEmpty() = this == NONE
}

