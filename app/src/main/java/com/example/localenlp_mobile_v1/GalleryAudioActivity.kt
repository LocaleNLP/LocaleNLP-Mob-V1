package com.example.localenlp_mobile_v1

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterAudio
import com.example.localenlp_mobile_v1.Classes.AudioClass
import com.example.localenlp_mobile_v1.DB.AudioDB
import com.example.localenlp_mobile_v1.Interfaces.OnItemClickListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryAudioActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var records: ArrayList<AudioClass>
    private lateinit var mAdapter: AdapterAudio
    private lateinit var db: AudioDB
    private lateinit var recAudio: RecyclerView
    private val ioScope = CoroutineScope(Dispatchers.IO) // Define CoroutineScope at the class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_audio)
        recAudio = findViewById(R.id.recRecycleView)

        // Initialize the database
        db = AudioDB(this)

        // Initialize records list
        records = ArrayList()

        // Set up the RecyclerView
        mAdapter = AdapterAudio(records, this)
        recAudio.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@GalleryAudioActivity)
        }

        // Fetch all records
        fetchAll()
    }

    private fun fetchAll() {
        if (isInternetAvailable(this)) {
            // Fetch records from the local database
            ioScope.launch {
                try {
                    val queryResult: List<AudioClass> = db.getAllAudioRecords()
                    records.clear()
                    records.addAll(queryResult)

                    // Save the data to Firebase
                    saveToFirebase(queryResult)

                    withContext(Dispatchers.Main) {
                        mAdapter.notifyDataSetChanged() // Notify adapter of data change on main thread
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@GalleryAudioActivity, "Error fetching records", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun saveToFirebase(records: List<AudioClass>) {
        val database = FirebaseDatabase.getInstance()
        val audioRef = database.getReference("audioRecords")

        // Clear the current data in Firebase (if needed)
        audioRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (record in records) {
                    // Sanitize the filename to create a valid Firebase path
                    val sanitizedFilename = record.filename.replace(".", "_")

                    // Use the sanitized filename as the key in Firebase
                    audioRef.child(sanitizedFilename).setValue(record).addOnCompleteListener { saveTask ->
                        if (saveTask.isSuccessful) {
                            Log.d("Firebase", "Record saved: $sanitizedFilename")
                        } else {
                            Log.e("Firebase", "Failed to save record: ${saveTask.exception?.message}")
                        }
                    }
                }
            } else {
                Log.e("Firebase", "Failed to clear records: ${task.exception?.message}")
            }
        }
    }


    override fun onItemClickListener(position: Int) {
        val audioRecord = records[position]
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra("filepath", audioRecord.filepath)
            putExtra("filename", audioRecord.filename)
        }
        startActivity(intent)
    }

    override fun onItelLongClickListener(position: Int) {
        val audioRecord = records[position]

        // Show a confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Delete Audio Record")
            .setMessage("Are you sure you want to delete ${audioRecord.filename}?")
            .setPositiveButton("Yes") { dialog, _ ->
                ioScope.launch {
                    db.deleteAudioRecord(position) // Assuming deleteAudioRecord is a method in your AudioDB class
                    withContext(Dispatchers.Main) {
                        records.removeAt(position)
                        mAdapter.notifyItemRemoved(position)
                        Toast.makeText(this@GalleryAudioActivity, "Audio record deleted", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
