package com.example.localenlp_mobile_v1

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.appbar.MaterialToolbar
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvFilename: TextView

    private lateinit var tvTrackerProgree: TextView
    private lateinit var tvTrackerDuration: TextView

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay: ImageButton
    private lateinit var rewardAudio: ImageButton
    private lateinit var replayAudio: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private val delay = 1000L  // Update interval for the SeekBar in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        // Initialize UI components
        toolbar = findViewById(R.id.toolbar)
        tvFilename = findViewById(R.id.tvFileName)
        btnPlay = findViewById(R.id.btnPlay)
        rewardAudio = findViewById(R.id.btnAdd)
        replayAudio = findViewById(R.id.btnMinus)
        seekBar = findViewById(R.id.seekBar)

        tvTrackerProgree = findViewById(R.id.tvTrackerProgress)
        tvTrackerDuration = findViewById(R.id.tvTrackerDuration)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Get file path and name from intent
        val filePath = intent.getStringExtra("filepath")
        val fileName = intent.getStringExtra("filename")

        tvFilename.text = fileName

        if (filePath == null || fileName == null) {
            Toast.makeText(this, "Invalid file path or name", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            tvTrackerDuration.text = dateFormate(mediaPlayer.duration) // Move this after prepare()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading audio file", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Set the SeekBar max to the media file duration
        seekBar.max = mediaPlayer.duration

        // Initialize the handler and runnable for updating the SeekBar
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (mediaPlayer.isPlaying) {
                seekBar.progress = mediaPlayer.currentPosition
                tvTrackerProgree.text = dateFormate(mediaPlayer.currentPosition)
                handler.postDelayed(runnable, delay)
            }
        }

        // Set up play/pause functionality
        btnPlay.setOnClickListener {
            playPausePlayer()
        }

        // Fast forward (skip ahead by 10 seconds)
        rewardAudio.setOnClickListener {
            val currentPosition = mediaPlayer.currentPosition
            val skipForward = 10000 // 10 seconds
            if (currentPosition + skipForward <= mediaPlayer.duration) {
                mediaPlayer.seekTo(currentPosition + skipForward)
            } else {
                mediaPlayer.seekTo(mediaPlayer.duration)
            }
            seekBar.progress = mediaPlayer.currentPosition
        }

        // Rewind (skip back by 10 seconds)
        replayAudio.setOnClickListener {
            val currentPosition = mediaPlayer.currentPosition
            val skipBackward = 10000 // 10 seconds
            if (currentPosition - skipBackward >= 0) {
                mediaPlayer.seekTo(currentPosition - skipBackward)
            } else {
                mediaPlayer.seekTo(0)
            }
            seekBar.progress = mediaPlayer.currentPosition
        }

        // Update the SeekBar as the media plays
        mediaPlayer.setOnCompletionListener {
            btnPlay.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, theme))
            handler.removeCallbacks(runnable)
            seekBar.progress = 0
        }

        // SeekBar change listener to allow user interaction
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(runnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                handler.postDelayed(runnable, delay)
            }
        })

        // Start playing immediately if required
        playPausePlayer()
    }

    private fun playPausePlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            btnPlay.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.pause, theme))
            handler.postDelayed(runnable, 0) // Start updating the SeekBar
        } else {
            mediaPlayer.pause()
            btnPlay.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.play_button, theme))
            handler.removeCallbacks(runnable) // Stop updating the SeekBar
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacks(runnable)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (::mediaPlayer.isInitialized) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
        handler.removeCallbacks(runnable)
    }


    private fun dateFormate(duration: Int): String {
        val d = duration / 1000
        val s = d % 60
        val m = d / 60 % 60
        val h = (d / 3600)

        val f: NumberFormat = DecimalFormat("00")
        var str = "$m:${f.format(s)}"

        if (h > 0) {
            str = "$h:$str"
        }
        return str
    }
}
