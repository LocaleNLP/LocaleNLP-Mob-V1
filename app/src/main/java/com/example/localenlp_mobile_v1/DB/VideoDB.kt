package com.example.localenlp_mobile_v1.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VideoDB(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE) // Create the table
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEXTS") // Drop the old table if it exists
        onCreate(db) // Create the table again
    }

    fun addVideo(content: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTENT, content)
        }
        db.insert(TABLE_TEXTS, null, values)
        db.close()
    }

    fun deleteVideo(content: String) {
        val db = this.writableDatabase
        db.delete(TABLE_TEXTS, "$COLUMN_CONTENT=?", arrayOf(content))
        db.close()
    }

    fun updateVideo(oldContent: String, newContent: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTENT, newContent)
        }
        db.update(TABLE_TEXTS, values, "$COLUMN_CONTENT=?", arrayOf(oldContent))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllVideo(): List<String> {
        val videoList = mutableListOf<String>() // Use mutableListOf for cleaner syntax
        val db = this.readableDatabase
        var cursor: Cursor? = null // Declare cursor outside the try block

        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_TEXTS", null)

            if (cursor.moveToFirst()) {
                do {
                    // Get the video content from the cursor
                    val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                    videoList.add(content) // Add to the list
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            // Log or handle the exception as necessary
            e.printStackTrace()
        } finally {
            cursor?.close() // Ensure the cursor is closed in the finally block
        }

        return videoList // Return the list of videos
    }


    companion object {
        private const val DATABASE_NAME = "videoDB.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_TEXTS = "video"
        const val COLUMN_CONTENT = "content"
        private const val TABLE_CREATE = "CREATE TABLE $TABLE_TEXTS ($COLUMN_CONTENT TEXT);"
    }
}
