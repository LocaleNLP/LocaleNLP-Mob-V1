package com.example.localenlp_mobile_v1.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ImageDB(context:Context):SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE) // Create the table
    }

    // This method is called when the database needs to be upgraded
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TEXTS")
        // Create the table again
        onCreate(db)
    }

    companion object {
        // Database name and version
        private const val DATABASE_NAME = "imageDB.db"
        private const val DATABASE_VERSION = 1

        // Table name and column name
        const val TABLE_TEXTS = "images"
        const val COLUMN_CONTENT = "content"

        // SQL query to create the table
        private const val TABLE_CREATE = "CREATE TABLE $TABLE_TEXTS (" +
                "$COLUMN_CONTENT TEXT);"
    }

    fun addImage(content: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, content)
        db.insert(TABLE_TEXTS, null, values)
        db.close()
    }

    fun deleteImage(content: String) {
        val db = this.writableDatabase
        db.delete(TABLE_TEXTS, "$COLUMN_CONTENT=?", arrayOf(content))
        db.close()
    }

    fun updateImage(oldContent: String, newContent: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, newContent)
        db.update(TABLE_TEXTS, values, "$COLUMN_CONTENT=?", arrayOf(oldContent))
        db.close()
    }


    @SuppressLint("Range")
    fun getAllImages(): List<String> {
        val imageList = ArrayList<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TEXTS", null)

        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                imageList.add(content)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return imageList
    }

}