package com.example.localenlp_mobile_v1

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class LanguageActivity : AppCompatActivity() {
    lateinit var btnBack: ImageView
    lateinit var btnArabic: Button
    lateinit var btnFrench: Button
    lateinit var btnEnglish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        btnArabic = findViewById(R.id.arab)
        btnEnglish = findViewById(R.id.english)
        btnFrench = findViewById(R.id.french)
        btnBack = findViewById(R.id.imageView5)

        // Handle language change
        btnArabic.setOnClickListener {
            changeLanguage("ar")
        }

        btnFrench.setOnClickListener {
            changeLanguage("fr")
        }

        btnEnglish.setOnClickListener {
            changeLanguage("en")
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun changeLanguage(languageCode: String) {
        // Save language preference
        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("My_Lang", languageCode)
        editor.apply()

        // Set the locale and restart app
        setLocale(languageCode)
        restartApp()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
