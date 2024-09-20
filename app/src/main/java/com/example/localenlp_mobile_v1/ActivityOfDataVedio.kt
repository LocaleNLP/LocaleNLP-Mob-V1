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
    lateinit var goBack: ImageView
    lateinit var recOfVideo: RecyclerView
    lateinit var getVideo: FloatingActionButton
    lateinit var db: VideoDB
    lateinit var listOfString: ArrayList<String>
    lateinit var adapter: AdapterForVideo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_vedio)

        goBack = findViewById(R.id.goBack)
        recOfVideo = findViewById(R.id.recOfVedio)
        getVideo = findViewById(R.id.floatingActionButton4)

        recOfVideo.layoutManager = GridLayoutManager(this, 1)
        db = VideoDB(this)

        // Initialize and set Adapter
        listOfString = db.getAllVideo() as ArrayList<String>
        adapter = AdapterForVideo(this, listOfString)
        recOfVideo.adapter = adapter

        // Set OnClickListener for selecting video
        getVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
            }
            startActivityForResult(intent, 200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                db.addVideo(uri.toString())
                // Refresh the adapter
                listOfString = db.getAllVideo() as ArrayList<String>
                adapter.notifyDataSetChanged()
            } ?: run {
                Toast.makeText(this, "There is a problem here", Toast.LENGTH_LONG).show()
            }
        }
    }
}
