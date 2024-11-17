package com.taltech.ee.tic_tac_two

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GameDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "game_state.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "game_state"
        const val COLUMN_ID = "id"
        const val COLUMN_GAME_STATE = "game_state"
        const val COLUMN_CURRENT_PLAYER = "current_player"
        const val COLUMN_X_WINS = "x_wins"
        const val COLUMN_O_WINS = "o_wins"
        const val COLUMN_HUMAN_WINS = "human_wins"
        const val COLUMN_BOT_WINS = "bot_wins"
        const val COLUMN_GRID_CENTER = "grid_center"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_GAME_STATE TEXT,
                $COLUMN_CURRENT_PLAYER TEXT,
                $COLUMN_X_WINS INTEGER,
                $COLUMN_O_WINS INTEGER,
                $COLUMN_HUMAN_WINS INTEGER,
                $COLUMN_BOT_WINS INTEGER,
                $COLUMN_GRID_CENTER INTEGER
            )
        """
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveGameState(
        gameState: Array<Array<String>>,
        currentPlayer: String,
        xWins: Int,
        oWins: Int,
        humanWins: Int,
        botWins: Int,
        gridCenter: Int
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_GAME_STATE, gameState.joinToString(",") { it.joinToString("") })
            put(COLUMN_CURRENT_PLAYER, currentPlayer)
            put(COLUMN_X_WINS, xWins)
            put(COLUMN_O_WINS, oWins)
            put(COLUMN_HUMAN_WINS, humanWins)
            put(COLUMN_BOT_WINS, botWins)
            put(COLUMN_GRID_CENTER, gridCenter)
        }
        db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    fun loadGameState(): GameData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME, null, "$COLUMN_ID = 1", null,
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val gameState = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME_STATE))
                .split(",").map { it.chunked(1).toTypedArray() }.toTypedArray()
            val currentPlayer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PLAYER))
            val xWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_X_WINS))
            val oWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_O_WINS))
            val humanWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMAN_WINS))
            val botWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOT_WINS))
            val gridCenter = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRID_CENTER))

            cursor.close()
            db.close()
            GameData(gameState, currentPlayer, xWins, oWins, humanWins, botWins, gridCenter)
        } else {
            cursor.close()
            db.close()
            null
        }
    }

    data class GameData(
        val gameState: Array<Array<String>>,
        val currentPlayer: String,
        val xWins: Int,
        val oWins: Int,
        val humanWins: Int,
        val botWins: Int,
        val gridCenter: Int
    )
}
