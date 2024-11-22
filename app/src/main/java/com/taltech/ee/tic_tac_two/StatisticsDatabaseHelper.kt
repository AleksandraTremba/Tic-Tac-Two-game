package com.taltech.ee.tic_tac_two

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class StatisticsDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "statistics.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "statistics"
        const val COLUMN_ID = "id"
        const val COLUMN_X_WINS = "x_wins"
        const val COLUMN_O_WINS = "o_wins"
        const val COLUMN_HUMAN_WINS = "human_wins"
        const val COLUMN_BOT_WINS = "bot_wins"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_X_WINS INTEGER,
                $COLUMN_O_WINS INTEGER,
                $COLUMN_HUMAN_WINS INTEGER,
                $COLUMN_BOT_WINS INTEGER
            )
        """
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun saveStats(
        xWins: Int,
        oWins: Int,
        humanWins: Int,
        botWins: Int,
    ) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_X_WINS, xWins)
            put(COLUMN_O_WINS, oWins)
            put(COLUMN_HUMAN_WINS, humanWins)
            put(COLUMN_BOT_WINS, botWins)
        }
        db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
    }


    fun loadStats(): StatsData? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(
                COLUMN_X_WINS,
                COLUMN_O_WINS,
                COLUMN_HUMAN_WINS,
                COLUMN_BOT_WINS,
            ),
            null, null, null, null,
            "$COLUMN_ID DESC",
            "1"
        )

        return if (cursor.moveToFirst()) {
            val xWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_X_WINS))
            val oWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_O_WINS))
            val humanWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMAN_WINS))
            val botWins = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BOT_WINS))

            cursor.close()
            StatsData(xWins, oWins, humanWins, botWins)
        } else {
            cursor.close()
            null
        }
    }



    data class StatsData(
        val xWins: Int,
        val oWins: Int,
        val humanWins: Int,
        val botWins: Int,
    )
}
