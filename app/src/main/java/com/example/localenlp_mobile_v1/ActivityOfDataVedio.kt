package com.example.localenlp_mobile_v1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterForVideo
import com.example.localenlp_mobile_v1.DB.VideoDB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class ActivityOfDataVideo : AppCompatActivity() {
    private lateinit var goBack: ImageView
    private lateinit var recOfVideo: RecyclerView
    private lateinit var getVideo: FloatingActionButton
    private lateinit var recordeVideo: FloatingActionButton
    private lateinit var db: VideoDB
    private lateinit var listOfString: ArrayList<String>
    private lateinit var adapter: AdapterForVideo
    private val firestore = FirebaseFirestore.getInstance() // Firestore instance

    private var recordedVideoUri: Uri? = null // Variable to hold recorded video URI

    // ActivityResultLauncher to handle video picking
    private lateinit var videoPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_vedio)

        // Initialize views
        goBack = findViewById(R.id.goBack)
        recOfVideo = findViewById(R.id.recOfVedio)
        getVideo = findViewById(R.id.floatingActionButton4)
        recordeVideo = findViewById(R.id.floatingActionButton5)

        // Set up RecyclerView
        recOfVideo.layoutManager = GridLayoutManager(this, 1)

        // Initialize database
        db = VideoDB(this)

        // Initialize ActivityResultLauncher for selecting and updating video
        videoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedVideoUri = data?.data
                if (selectedVideoUri != null) {
                    db.addVideo(selectedVideoUri.toString()) // Save selected video to database
                    updateVideoList() // Refresh the video list
                }
            }
        }

        // Initialize and set adapter
        updateVideoList()

        // Set OnClickListener for selecting video
        getVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                addCategory(Intent.CATEGORY_OPENABLE) // Ensure that only accessible videos are shown
            }
            videoPickerLauncher.launch(intent) // Launch video picker
        }

        // Set OnClickListener for recording video
        recordeVideo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request permissions
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE)
            } else {
                // Permissions already granted, start video recording
                startVideoRecording()
            }
        }

        // Set OnClickListener for checking internet connection and uploading data
        goBack.setOnClickListener {
            if (isConnectedToInternet()) {
                uploadDataToFirebase() // Upload local data to Firebase
            } else {
                Toast.makeText(this, "No internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to start recording a video
    private fun startVideoRecording() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_RECORD_VIDEO)
        } else {
            Toast.makeText(this, "No application available to record video.", Toast.LENGTH_SHORT).show()
        }
    }

    // Check for internet connectivity
    private fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    // Method to upload video data to Firebase
    private fun uploadDataToFirebase() {
        listOfString = ArrayList(db.getAllVideo()) // Fetch video list from local DB
        for (videoUri in listOfString) {
            val videoData = hashMapOf("videoUri" to videoUri)
            firestore.collection("videos")
                .add(videoData)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "Video added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding video", e)
                }
        }
        Toast.makeText(this, "Data upload initiated.", Toast.LENGTH_SHORT).show()
    }

    // Handle permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start video recording
                    startVideoRecording()
                } else {
                    Toast.makeText(this, "Permission denied to access camera.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Handle activity result for video recording
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_RECORD_VIDEO -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        if (uri.toString().isNotEmpty()) {
                            db.addVideo(uri.toString()) // Save recorded video to database
                            updateVideoList()
                        } else {
                            Toast.makeText(this, "Recorded video URI is empty.", Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        Toast.makeText(this, "Failed to retrieve the recorded video.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // Method to update the video list and set the adapter
    private fun updateVideoList() {
        listOfString = ArrayList(db.getAllVideo()) // Fetch updated list from the database
        adapter = AdapterForVideo(this, listOfString, videoPickerLauncher) { videoUriString ->
            db.deleteVideo(videoUriString) // Remove the video from the database
            Toast.makeText(this, "Video deleted from database.", Toast.LENGTH_SHORT).show()
        }
        recOfVideo.adapter = adapter // Set the new adapter
    }

    companion object {
        private const val REQUEST_CODE_RECORD_VIDEO = 201
        private const val PERMISSION_REQUEST_CODE = 202 // Permission request code
        private const val TAG = "ActivityOfDataVideo"
    }
}
