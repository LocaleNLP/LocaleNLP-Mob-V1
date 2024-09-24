package com.example.localenlp_mobile_v1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.localenlp_mobile_v1.Classes.AudioClass
import com.example.localenlp_mobile_v1.Classes.Timer
import com.example.localenlp_mobile_v1.Classes.WaveFormView
import com.example.localenlp_mobile_v1.DB.AudioDB
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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
    lateinit var buttonSheetBehavior: BottomSheetBehavior<LinearLayout>
    lateinit var buttonShetBG:View
    lateinit var fileNameInpute: TextInputEditText
    lateinit var btnCancel:Button
    lateinit var btnOk:Button
    lateinit var bottonSheeat:LinearLayout

    private var dirPath = ""
    private var filename = ""

    private var isRecording = false
    private var isPaused = false

    private lateinit var timer: Timer

    private lateinit var db:AudioDB
    private var duration = ""

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
        buttonShetBG = findViewById(R.id.buttonShetBG)
        btnCancel = findViewById(R.id.btnCancel)
        btnOk = findViewById(R.id.btnOk)
        bottonSheeat = findViewById(R.id.BottomSheet)
        fileNameInpute = findViewById(R.id.fileNameInpute)

        db = AudioDB(this)

        buttonSheetBehavior = BottomSheetBehavior.from(bottonSheeat)
        buttonSheetBehavior.peekHeight = 0
        buttonSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED


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

        buttonShetBG.setOnClickListener {
            File("$dirPath/$filename.mp3").delete()
            dissmis()
        }

        listOfAudios.setOnClickListener {
            startActivity(Intent(this@ActivityOfDataAudio,GalleryAudioActivity::class.java))
        }

        btnCancel.setOnClickListener {
            File("$dirPath/$filename.mp3").delete()
            dissmis()
        }


        btnOk.setOnClickListener {
            dissmis()
            save()
        }

        deletBtn.setOnClickListener {
            stopRecorder()
            File("$dirPath/$filename.mp3").delete()
            Toast.makeText(this,getString(R.string.Recorde_Deleted),Toast.LENGTH_LONG).show()
        }

        donBtn.setOnClickListener {
            stopRecorder()
            Toast.makeText(this,getString(R.string.Recorde_Saved),Toast.LENGTH_LONG).show()
            buttonSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            buttonShetBG.visibility = View.VISIBLE

            fileNameInpute.setText(filename)

        }



        deletBtn.isClickable = false

    }

    private fun save() {
        val newFileName = fileNameInpute.text.toString()

        // Check if the filename has changed
        if (newFileName != filename) {
            val newFile = File("$dirPath/$newFileName.mp3")
            val oldFile = File("$dirPath/$filename.mp3")

            // Rename the file
            if (oldFile.exists()) {
                if (oldFile.renameTo(newFile)) {
                    filename = newFileName // Update the filename if renaming was successful
                } else {
                    // Handle renaming failure
                    println("Error: Failed to rename the file")
                    return
                }
            }
        }

        // Update the filePath and other attributes
        val filePath = "$dirPath/$newFileName.mp3"
        val timestamp = Date().time
        val ampsPaths = "$dirPath/$newFileName.amplitude" // Change the extension to .amplitude or similar

        try {
            // Write the amplitude data to the file using ObjectOutputStream
            val fos = FileOutputStream(ampsPaths)
            val out = ObjectOutputStream(fos)
            out.writeObject(amplitude)

            // Properly close the streams
            out.close()
            fos.close()
        } catch (e: Exception) {
            // Handle any IO exceptions
            e.printStackTrace()
        }

        // Create an AudioClass instance
        val record = AudioClass(newFileName, filePath, timestamp, duration, ampsPaths)

        // Insert the audio record into the database
        db.addAudioRecord(record)

        // Optionally, show a Toast message confirming that the record was saved
        Toast.makeText(this, "Audio saved successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun dissmis(){
        buttonShetBG.visibility = View.GONE
        hidkeyboard(fileNameInpute)
        Handler(Looper.getMainLooper()).postDelayed({
            buttonSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        },100)
    }

    private fun hidkeyboard(view:View){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)

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
        this.duration = duration.dropLast(3)
        waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())
    }
}
