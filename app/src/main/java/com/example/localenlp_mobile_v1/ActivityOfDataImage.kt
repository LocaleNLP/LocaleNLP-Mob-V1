package com.example.localenlp_mobile_v1

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterOfImage
import com.example.localenlp_mobile_v1.DB.ImageDB
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ActivityOfDataImage : AppCompatActivity() {

    lateinit var recOfImage: RecyclerView
    lateinit var addImage: FloatingActionButton
    lateinit var takeImage: FloatingActionButton
    lateinit var dbImage: ImageDB
    lateinit var listOfImage: ArrayList<String>
    lateinit var adapter: AdapterOfImage

    companion object {
        const val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_image)

        recOfImage = findViewById(R.id.recOfImage)
        addImage = findViewById(R.id.floatingActionButton2)
        takeImage = findViewById(R.id.floatingActionButton3)
        dbImage = ImageDB(this)
        recOfImage.layoutManager = GridLayoutManager(this,2)
        listOfImage = dbImage.getAllImages() as ArrayList<String>
        adapter = AdapterOfImage(this, listOfImage)
        recOfImage.adapter = adapter // Set the adapter for RecyclerView
        adapter.notifyDataSetChanged()

        addImage.setOnClickListener {
            pickImageGallery()
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE) {
            // Pass the result to the adapter to handle
            (recOfImage.adapter as AdapterOfImage).handleImageResult(requestCode, resultCode, data)
        }
    }
}
