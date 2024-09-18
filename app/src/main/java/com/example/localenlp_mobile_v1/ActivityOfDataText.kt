package com.example.localenlp_mobile_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ActivityOfDataText : AppCompatActivity() {
    lateinit var back:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_text)

        back = findViewById(R.id.back)

    }
}