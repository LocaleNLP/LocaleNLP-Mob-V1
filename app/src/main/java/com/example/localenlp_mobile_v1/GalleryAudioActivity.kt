package com.example.localenlp_mobile_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterAudio
import com.example.localenlp_mobile_v1.Classes.AudioClass
import com.example.localenlp_mobile_v1.DB.AudioDB
import com.example.localenlp_mobile_v1.Interfaces.OnItemClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        Toast.makeText(this, "Simple click on item $position", Toast.LENGTH_LONG).show()
    }

    override fun onItelLongClickListener(position: Int) {
        Toast.makeText(this, "Simple click Long on item $position", Toast.LENGTH_LONG).show()
    }


}
