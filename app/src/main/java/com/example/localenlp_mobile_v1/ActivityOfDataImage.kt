package com.example.localenlp_mobile_v1

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
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
    lateinit var back:ImageView

    private var imageUri: Uri? = null

    companion object {
        const val IMAGE_REQUEST_CODE = 100
        const val REQUEST_CAMERA_PERMISSION = 200
        const val CAMERA_REQUEST_CODE = 300
        const val REQUEST_WRITE_STORAGE_PERMISSION = 400
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_image)

        back = findViewById(R.id.imageView2)
        recOfImage = findViewById(R.id.recOfImage)
        addImage = findViewById(R.id.floatingActionButton2)
        takeImage = findViewById(R.id.floatingActionButton3)
        dbImage = ImageDB(this)
        recOfImage.layoutManager = GridLayoutManager(this, 2)
        listOfImage = dbImage.getAllImages() as ArrayList<String>
        adapter = AdapterOfImage(this, listOfImage)
        recOfImage.adapter = adapter // Set the adapter for RecyclerView
        adapter.notifyDataSetChanged()

        back.setOnClickListener {
            val intent = Intent(this@ActivityOfDataImage,DashBoardOfActivity::class.java)
            startActivity(intent)
        }

        addImage.setOnClickListener {
            pickImageGallery()
        }

        takeImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_STORAGE_PERMISSION)
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    private fun openCamera() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Image")
            put(MediaStore.Images.Media.DESCRIPTION, "Image captured from camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_REQUEST_CODE -> {
                    // Handle gallery image result
                    data?.data?.let { uri ->
                        (recOfImage.adapter as AdapterOfImage).handleImageResult(requestCode, resultCode, data)
                        dbImage.addImage(uri.toString()) // Save the image URI to the database
                        listOfImage.add(uri.toString())
                        adapter.notifyDataSetChanged()
                    }
                }
                CAMERA_REQUEST_CODE -> {
                    // Handle camera image result
                    imageUri?.let { uri ->
                        // Add the camera image URI to the adapter and save it to the database
                        (recOfImage.adapter as AdapterOfImage).handleImageResult(requestCode, resultCode, data)
                        dbImage.addImage(uri.toString()) // Save the image URI to the database
                        listOfImage.add(uri.toString())
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_WRITE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open the camera
                    openCamera()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Camera and storage permissions are required to use these features", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, open the camera
                    openCamera()
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
