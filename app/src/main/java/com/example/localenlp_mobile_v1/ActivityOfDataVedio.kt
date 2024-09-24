package com.example.localenlp_mobile_v1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterForVideo
import com.example.localenlp_mobile_v1.DB.VideoDB
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        goBack = findViewById(R.id.goBack)
        recOfVideo = findViewById(R.id.recOfVedio)
        getVideo = findViewById(R.id.floatingActionButton4)

        // Set the appropriate layout manager
        recOfVideo.layoutManager = GridLayoutManager(this, 1)

        db = VideoDB(this)

        // Initialize and set Adapter
        updateVideoList()

        // Set OnClickListener for selecting video
        getVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
            }
            startActivityForResult(intent, REQUEST_CODE)
        }

        // Set OnClickListener for the back button
        goBack.setOnClickListener {
            finish() // Close the activity
        }

        updateVideoList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                db.addVideo(uri.toString())
                // Refresh the adapter
                updateVideoList()
            } ?: run {
                Toast.makeText(this, "There is a problem here", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateVideoList() {
        listOfString = ArrayList(db.getAllVideo()) // Fetch updated list from database
        adapter = AdapterForVideo(this, listOfString) // Recreate adapter with updated list
        recOfVideo.adapter = adapter // Set the new adapter
        //listOfString.clear()
    }

    companion object {
        private const val REQUEST_CODE = 200
    }
}
