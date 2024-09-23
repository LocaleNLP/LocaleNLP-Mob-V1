package com.example.localenlp_mobile_v1

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load saved language before setting content view
        loadLocale()

        setContentView(R.layout.activity_main)

        // Navigate to Dashboard after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@MainActivity, DashBoardOfActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }

    // Method to load saved language from SharedPreferences
    private fun loadLocale() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en") // Default to English if no language is set
        setLocale(language)
    }

    // Method to set the app locale
    private fun setLocale(languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}
