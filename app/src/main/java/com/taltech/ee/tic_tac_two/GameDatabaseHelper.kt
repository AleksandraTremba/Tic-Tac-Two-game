package com.taltech.ee.tic_tac_two

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class GameDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "game_state.db"
        const val DATABASE_VERSION = 2
        const val TABLE_NAME = "game_state"
        const val COLUMN_ID = "id"
        const val COLUMN_GAME_STATE = "game_state"
        const val COLUMN_CURRENT_PLAYER = "current_player"
        const val COLUMN_GRID_CENTER = "grid_center"
        const val COLUMN_O_MOVES = "o_moves"
        const val COLUMN_X_MOVES = "x_moves"

    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_GAME_STATE TEXT,
                $COLUMN_CURRENT_PLAYER TEXT,
                $COLUMN_GRID_CENTER INTEGER,
                $COLUMN_O_MOVES INTEGER,
                $COLUMN_X_MOVES INTEGER
            )
        """
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_O_MOVES INTEGER DEFAULT 0")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_X_MOVES INTEGER DEFAULT 0")
        }
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveGameState(
        gameState: Array<Array<String>>,
        currentPlayer: String,
        gridCenter: Int,
        oMoves: Int,
        xMoves: Int
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_GAME_STATE, gameState.joinToString(";") { it.joinToString(",") })
            put(COLUMN_CURRENT_PLAYER, currentPlayer)
            put(COLUMN_GRID_CENTER, gridCenter)
            put(COLUMN_O_MOVES, oMoves)
            put(COLUMN_X_MOVES, xMoves)
        }
        db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }


    fun loadGameState(): GameData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_ID,
                COLUMN_GAME_STATE,
                COLUMN_CURRENT_PLAYER,
                COLUMN_GRID_CENTER,
                COLUMN_O_MOVES,
                COLUMN_X_MOVES
            ),
            null, null, null, null,
            "$COLUMN_ID DESC",
            "1"
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GAME_STATE))
            val gameStateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GAME_STATE))
            Log.d("LoadGame", "Loaded data string in the database: $gameStateString")

            val outerArray = gameStateString.split(";")
            val gameState = outerArray.map { it.split(",").toTypedArray() }.toTypedArray()

            Log.d("LoadGame", "Loaded data in the database: ${gameState.contentDeepToString()}")

            val currentPlayer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PLAYER))
            val gridCenter = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_GRID_CENTER))

            val oMoves = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_O_MOVES))
            val xMoves = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_X_MOVES))

            cursor.close()
            GameData(id, gameState, currentPlayer, gridCenter, oMoves, xMoves)
        } else {
            cursor.close()
            null
        }
    }



    data class GameData(
        val id: Int,
        val gameState: Array<Array<String>>,
        val currentPlayer: String,
        val gridCenter: Int,
        val oMoves: Int,
        val xMoves: Int
    )
}
