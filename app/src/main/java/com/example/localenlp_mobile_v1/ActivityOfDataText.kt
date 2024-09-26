package com.example.localenlp_mobile_v1

import TextDB
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterText
import com.example.localenlp_mobile_v1.DialogFragment.DialogFragmentForText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ActivityOfDataText : AppCompatActivity() {
    lateinit var back: ImageView
    lateinit var addText: FloatingActionButton
    lateinit var db: TextDB
    lateinit var listOfText: ArrayList<String>
    lateinit var adapter: AdapterText
    lateinit var recOfText: RecyclerView
    lateinit var firebaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_text)

        // Initialize views
        back = findViewById(R.id.back)
        addText = findViewById(R.id.floatingActionButton)
        db = TextDB(this)
        firebaseRef = FirebaseDatabase.getInstance().getReference("texts")

        // Initialize the list of texts from the database
        listOfText = ArrayList(db.getAllTexts())

        // Set up RecyclerView with the adapter
        recOfText = findViewById(R.id.recOfText)
        adapter = AdapterText(this@ActivityOfDataText, listOfText)
        recOfText.layoutManager = LinearLayoutManager(this)
        recOfText.adapter = adapter

        // Back button click listener
        back.setOnClickListener {
            val intent = Intent(this, DashBoardOfActivity::class.java)
            startActivity(intent)
        }

        // Floating Action Button to add new text
        addText.setOnClickListener {
            val dialog = DialogFragmentForText()
            dialog.setDialogListener(object : DialogFragmentForText.DialogListener {
                override fun onTextInput(text: String) {
                    // Add text to the database
                    db.addText(text)
                    // Update the list of texts
                    listOfText.clear()
                    listOfText.addAll(db.getAllTexts())
                    // Notify the adapter about the new data
                    adapter.notifyDataSetChanged()

                    // Save the new text to Firebase
                    saveNewTextToFirebase(text)
                }

                override fun onCancel() {
                    Toast.makeText(this@ActivityOfDataText, "Cancel", Toast.LENGTH_LONG).show()
                }
            })
            dialog.show(supportFragmentManager, "DialogFragmentForText")
        }
    }

    private fun saveNewTextToFirebase(text: String) {
        if (isInternetAvailable()) {
            // Save the new text to Firebase
            firebaseRef.push().setValue(text).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save data to Firebase", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
