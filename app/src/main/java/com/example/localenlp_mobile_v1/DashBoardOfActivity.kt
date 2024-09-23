package com.example.localenlp_mobile_v1

import DataAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Classes.DataItem

class DashBoardOfActivity : AppCompatActivity() {

    lateinit var recOfDataType: RecyclerView
    lateinit var imageOfLanguages:ImageView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_of)

        // Initialize RecyclerView
        recOfDataType = findViewById(R.id.recOfDataType)
        imageOfLanguages = findViewById(R.id.imageView4)


        imageOfLanguages.setOnClickListener {
            val intent = Intent(this@DashBoardOfActivity,LanguageActivity::class.java)
            startActivity(intent)
        }

        // Set LayoutManager for the RecyclerView (LinearLayoutManager for a vertical list)
        recOfDataType.layoutManager = GridLayoutManager(this, 2)


        // Sample data for the RecyclerView
        val dataList = listOf(
            DataItem(R.drawable.text, getString(R.string.tx)),
            DataItem(R.drawable.insert_picture_icon, getString(R.string.im)),
            DataItem(R.drawable.video_camera, getString(R.string.vd)) ,
            DataItem(R.drawable.volume, getString(R.string.au))
        )

        // Set adapter for RecyclerView
        val adapter = DataAdapter(dataList)
        recOfDataType.adapter = adapter
    }
}
