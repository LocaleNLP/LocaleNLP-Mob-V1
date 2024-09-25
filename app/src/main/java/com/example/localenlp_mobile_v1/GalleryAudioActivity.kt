package com.example.localenlp_mobile_v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterAudio
import com.example.localenlp_mobile_v1.Classes.AudioClass
import com.example.localenlp_mobile_v1.DB.AudioDB
import com.example.localenlp_mobile_v1.Interfaces.OnItemClickListener
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryAudioActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var records: ArrayList<AudioClass>
    private lateinit var mAdapter: AdapterAudio
    private lateinit var db: AudioDB
    private lateinit var recAudio: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_audio)
        recAudio = findViewById(R.id.recRecycleView)

        // Initialize the database
        db = AudioDB(this)

        // Retrieve all audio records from the database
        records = ArrayList(db.getAllAudioRecords())

        // Initialize the adapter
        mAdapter = AdapterAudio(records, this)

        // Set up the RecyclerView
        recAudio.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@GalleryAudioActivity)
        }

        fetchAll()


    }

    private fun fetchAll() {
        CoroutineScope(Dispatchers.IO).launch {
            val queryResult: List<AudioClass> = db.getAllAudioRecords()
            records.clear()
            records.addAll(queryResult)
            launch(Dispatchers.Main) {
                mAdapter.notifyDataSetChanged() // Notify adapter of data change on main thread
            }
        }
    }

    override fun onItemClickListener(position: Int) {
        var audioRecord = records[position]
        val intent = Intent(this,AudioPlayerActivity::class.java)
        intent.putExtra("filepath",audioRecord.filepath)
        intent.putExtra("filename",audioRecord.filename)
        startActivity(intent)
    }

    override fun onItelLongClickListener(position: Int) {
        val audioRecord = records[position]

        // Show a confirmation dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Audio Record")
        builder.setMessage("Are you sure you want to delete ${audioRecord.filename}?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            // Proceed with deleting the item from the database
            CoroutineScope(Dispatchers.IO).launch {
                db.deleteAudioRecord(position) // Assuming deleteAudioRecord is a method in your AudioDB class
                withContext(Dispatchers.Main) {
                    // Remove the item from the list and notify the adapter
                    records.removeAt(position)
                    mAdapter.notifyItemRemoved(position)
                    Toast.makeText(this@GalleryAudioActivity, "Audio record deleted", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Close the dialog if the user chooses "No"
        }

        val dialog = builder.create()
        dialog.show()
    }


}
