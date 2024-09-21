package com.example.localenlp_mobile_v1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.localenlp_mobile_v1.Classes.Timer
import com.example.localenlp_mobile_v1.Classes.WaveFormView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE = 200

class ActivityOfDataAudio : AppCompatActivity(), Timer.OnTimerTickListener {
    private lateinit var amplitude: ArrayList<Float>
    private val permission = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    private lateinit var recordingAudio: FloatingActionButton
    private lateinit var recorder: MediaRecorder
    private lateinit var timerText: TextView
    private lateinit var vibrator: Vibrator
    private lateinit var waveFormView: WaveFormView
    lateinit var listOfAudios:FloatingActionButton
    lateinit var deletBtn:FloatingActionButton
    lateinit var donBtn:FloatingActionButton

    private var dirPath = ""
    private var filename = ""

    private var isRecording = false
    private var isPaused = false

    private lateinit var timer: Timer

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_of_data_audio)

        timerText = findViewById(R.id.textView)
        recordingAudio = findViewById(R.id.floatingActionButton6)
        waveFormView = findViewById(R.id.wv) // Fixed variable name
        listOfAudios = findViewById(R.id.floatingActionButton7)
        deletBtn = findViewById(R.id.floatingActionButton8)
        donBtn = findViewById(R.id.floatingActionButton9)

        permissionGranted = ActivityCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        timer = Timer(this)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
        }

        recordingAudio.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecorder()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }


        listOfAudios.setOnClickListener {
            Toast.makeText(this,"list button",Toast.LENGTH_LONG).show()
        }

        deletBtn.setOnClickListener {
            stopRecorder()
            File("$dirPath/$filename.mp3")
            Toast.makeText(this,"Recorde Deleted",Toast.LENGTH_LONG).show()
        }

        donBtn.setOnClickListener {
            stopRecorder()
            Toast.makeText(this,"Recorde Saved",Toast.LENGTH_LONG).show()
        }

        deletBtn.isClickable = false

    }

    private fun pauseRecorder() {
        recorder.pause()
        isPaused = true
        recordingAudio.setImageResource(R.drawable.podcast)

        timer.pause()
    }

    private fun resumeRecording() {
        recorder.resume()
        isPaused = false
        recordingAudio.setImageResource(R.drawable.mic)

        timer.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        if (!permissionGranted) {
            recordingAudio.isEnabled = false
        }
    }

    private fun startRecording() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
            return
        }

        recorder = MediaRecorder()
        dirPath = externalCacheDir?.absolutePath ?: ""
        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", java.util.Locale.getDefault())
        val date = simpleDateFormat.format(Date())

        filename = "audio_record_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath/$filename.mp3")

            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        recordingAudio.setImageResource(R.drawable.podcast)
        isRecording = true
        isPaused = false

        timer.start()

        deletBtn.isClickable = true
        deletBtn.setImageResource(R.drawable.baseline_close_24)
        listOfAudios.visibility = View.GONE
        donBtn.visibility = View.VISIBLE
    }

    override fun onStop() {
        super.onStop()
        if (isRecording) {
            stopRecorder()
        }
    }

    private fun stopRecorder() {
        recorder.stop()
        recorder.release()
        isRecording = false
        isPaused = false
        timer.stop()

        listOfAudios.visibility = View.VISIBLE
        donBtn.visibility = View.GONE
        deletBtn.isClickable = false
        deletBtn.setImageResource(R.drawable.baseline_close_24)
        recordingAudio.setImageResource(R.drawable.mic)

        timerText.text = "00:00:00"

        amplitude = waveFormView.clear()
    }

    override fun onTimerTick(duration: String) {
        timerText.text = duration
        waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}
