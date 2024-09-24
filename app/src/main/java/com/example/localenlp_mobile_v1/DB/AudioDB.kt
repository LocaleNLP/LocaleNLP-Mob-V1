package com.example.localenlp_mobile_v1.DB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.localenlp_mobile_v1.Classes.AudioClass

class AudioDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "audioDB.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "audio_records"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FILE_NAME = "file_name"
        private const val COLUMN_FILE_PATH = "file_path"
        private const val COLUMN_TIMESTAMP = "timestamp"
        private const val COLUMN_DURATION = "duration"
        private const val COLUMN_AMPS_PATH = "amps_path"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FILE_NAME TEXT NOT NULL,
                $COLUMN_FILE_PATH TEXT NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL,
                $COLUMN_DURATION TEXT NOT NULL,
                $COLUMN_AMPS_PATH TEXT NOT NULL
            );
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to add a new audio record
    fun addAudioRecord(audio: AudioClass) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FILE_NAME, audio.filename)
            put(COLUMN_FILE_PATH, audio.filepath)
            put(COLUMN_TIMESTAMP, audio.timestamp)
            put(COLUMN_DURATION, audio.duration)
            put(COLUMN_AMPS_PATH, audio.ampsPath)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Method to delete an audio record by ID
    fun deleteAudioRecord(id: Int) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }

    // Method to get all audio records
    fun getAllAudioRecords(): List<AudioClass> {
        val audioList = mutableListOf<AudioClass>()
        val db = readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val cursor: Cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val audio = AudioClass(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILE_PATH)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AMPS_PATH))
                )
                audioList.add(audio)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return audioList
    }

    // Method to update an existing audio record
    fun updateAudioRecord(id: Int, audio: AudioClass) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_FILE_NAME, audio.filename)
            put(COLUMN_FILE_PATH, audio.filepath)
            put(COLUMN_TIMESTAMP, audio.timestamp)
            put(COLUMN_DURATION, audio.duration)
            put(COLUMN_AMPS_PATH, audio.ampsPath)
        }
        db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }
}
