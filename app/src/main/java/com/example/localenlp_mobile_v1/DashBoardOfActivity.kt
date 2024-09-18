package com.example.localenlp_mobile_v1

import DataAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Classes.DataItem

class DashBoardOfActivity : AppCompatActivity() {

    lateinit var recOfDataType: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board_of)

        // Initialize RecyclerView
        recOfDataType = findViewById(R.id.recOfDataType)

        // Set LayoutManager for the RecyclerView (LinearLayoutManager for a vertical list)
        recOfDataType.layoutManager = GridLayoutManager(this, 2)


        // Sample data for the RecyclerView
        val dataList = listOf(
            DataItem(R.drawable.text, "Text"),
            DataItem(R.drawable.insert_picture_icon, "Image"),
            DataItem(R.drawable.video_camera, "Vedio") ,
            DataItem(R.drawable.volume, "Audio")
        )

        // Set adapter for RecyclerView
        val adapter = DataAdapter(dataList)
        recOfDataType.adapter = adapter
    }
}
