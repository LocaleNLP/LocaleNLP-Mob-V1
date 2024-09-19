package com.example.localenlp_mobile_v1

import TextDB
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localenlp_mobile_v1.Adapters.AdapterText
import com.example.localenlp_mobile_v1.DialogFragment.DialogFragmentForText
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ActivityOfDataText : AppCompatActivity() {
    lateinit var back: ImageView
    lateinit var addText: FloatingActionButton
    lateinit var db: TextDB
    lateinit var listOfText: ArrayList<String>
    lateinit var adapter: AdapterText
    lateinit var recOfText: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_text)

        // Initialize views
        back = findViewById(R.id.back)
        addText = findViewById(R.id.floatingActionButton)
        db = TextDB(this)

        // Initialize the list of texts from the database
        listOfText = ArrayList(db.getAllTexts())  // Convert List to ArrayList

        // Set up RecyclerView with the adapter
        recOfText = findViewById(R.id.recOfText)
        adapter = AdapterText(this@ActivityOfDataText,listOfText)
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
                }

                override fun onCancel() {
                    Toast.makeText(this@ActivityOfDataText, "Cancel", Toast.LENGTH_LONG).show()
                }
            })
            dialog.show(supportFragmentManager, "DialogFragmentForText")
        }
    }
}
