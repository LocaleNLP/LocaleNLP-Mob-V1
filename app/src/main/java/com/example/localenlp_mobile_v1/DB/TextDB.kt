import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TextDB(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // This method is called when the database is created for the first time
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

    // Function to add data to the database
    fun addText(content: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, content)

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_TEXTS, null, values)
    }

    // Function to delete data from the database
    fun deleteText(content: String): Int {
        val db = this.writableDatabase

        // Delete rows matching the content
        return db.delete(TABLE_TEXTS, "$COLUMN_CONTENT = ?", arrayOf(content))
    }

    // Function to update data in the database
    fun updateText(oldContent: String, newContent: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CONTENT, newContent)

        // Update the content in the database where the content matches
        return db.update(TABLE_TEXTS, values, "$COLUMN_CONTENT = ?", arrayOf(oldContent))
    }

    fun getAllTexts(): List<String> {
        val texts = mutableListOf<String>()
        val db = this.readableDatabase

        // Query to get all rows from the texts table
        val cursor: Cursor = db.query(
            TABLE_TEXTS,         // Table name
            arrayOf(COLUMN_CONTENT),  // Columns to return
            null,                // WHERE clause (none here)
            null,                // WHERE arguments (none here)
            null,                // GROUP BY clause (none here)
            null,                // HAVING clause (none here)
            null                 // ORDER BY clause (none here)
        )

        // Iterate over the rows and add each content to the list
        if (cursor.moveToFirst()) {
            do {
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                texts.add(content)
            } while (cursor.moveToNext())
        }

        // Close the cursor to avoid memory leaks
        cursor.close()

        return texts
    }
    companion object {
        // Database name and version
        private const val DATABASE_NAME = "textDB.db"
        private const val DATABASE_VERSION = 1

        // Table name and column name
        const val TABLE_TEXTS = "texts"
        const val COLUMN_CONTENT = "content"

        // SQL query to create the table
        private const val TABLE_CREATE = "CREATE TABLE $TABLE_TEXTS (" +
                "$COLUMN_CONTENT TEXT);"
    }
}
