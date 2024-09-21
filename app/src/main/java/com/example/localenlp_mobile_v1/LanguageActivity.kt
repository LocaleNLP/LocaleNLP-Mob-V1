package com.example.localenlp_mobile_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class LanguageActivity : AppCompatActivity() {
    lateinit var btnBack:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        btnBack = findViewById(R.id.imageView5)
        btnBack.setOnClickListener {
            finish()
        }
    }
}