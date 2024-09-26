package com.example.localenlp_mobile_v1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterForVideo
import com.example.localenlp_mobile_v1.DB.VideoDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.log

class ActivityOfDataVideo : AppCompatActivity() {
    private lateinit var goBack: ImageView
    private lateinit var recOfVideo: RecyclerView
    private lateinit var getVideo: FloatingActionButton
    private lateinit var db: VideoDB
    private lateinit var listOfString: ArrayList<String>
    private lateinit var adapter: AdapterForVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_vedio)

        // Initialize views
        goBack = findViewById(R.id.goBack)
        recOfVideo = findViewById(R.id.recOfVedio)
        getVideo = findViewById(R.id.floatingActionButton4)

        // Set up RecyclerView
        recOfVideo.layoutManager = GridLayoutManager(this, 1)

        // Initialize database
        db = VideoDB(this)

        // Initialize and set adapter
        updateVideoList()

        // Set OnClickListener for selecting video
        getVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                addCategory(Intent.CATEGORY_OPENABLE) // Ensure that only accessible videos are shown
            }
            startActivityForResult(intent, REQUEST_CODE)
        }

        // Set OnClickListener for the back button
        goBack.setOnClickListener {
            finish() // Close the activity
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                if (uri.toString().isNotEmpty()) {
                    db.addVideo(uri.toString())
                    // Refresh the adapter
                    updateVideoList()
                } else {
                    Toast.makeText(this, "Selected video URI is empty.", Toast.LENGTH_LONG).show()
                }
            } ?: run {
                Toast.makeText(this, "Failed to retrieve the selected video.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateVideoList() {
        listOfString = ArrayList(db.getAllVideo()) // Fetch updated list from database
        adapter = AdapterForVideo(this, listOfString) // Create adapter with updated list
        recOfVideo.adapter = adapter // Set the new adapter
//        Toast.makeText(this@ActivityOfDataVideo,db.getAllVideo()[0].toString(),Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val REQUEST_CODE = 200
    }
}
