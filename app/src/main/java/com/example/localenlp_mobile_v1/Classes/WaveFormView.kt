package com.example.localenlp_mobile_v1.Classes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveFormView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.rgb(244, 81, 30)
    }

    private val amplitudes = ArrayList<Float>()
    private val spikes = ArrayList<RectF>()

    private val radius = 6f
    private val spikeWidth = 9f
    private val spikeSpacing = 6f

    private val screenWidth: Float = resources.displayMetrics.widthPixels.toFloat()
    private val screenHeight = 400f

    private val maxSpikes = (screenWidth / (spikeWidth + spikeSpacing)).toInt()

    fun addAmplitude(amp: Float) {
        val normalizedAmp = Math.min((amp / 7).toInt(), screenHeight.toInt()).toFloat()
        amplitudes.add(normalizedAmp)

        spikes.clear()
        val recentAmplitudes = amplitudes.takeLast(maxSpikes)
        for (i in recentAmplitudes.indices) {
            val left = screenWidth - i * (spikeWidth + spikeSpacing)
            val spikeHeight = recentAmplitudes[i]
            val top = (screenHeight - spikeHeight) / 2 // Center spike vertically
            val right = left + spikeWidth
            val bottom = top + spikeHeight
            spikes.add(RectF(left, top, right, bottom))
        }

        invalidate() // Request re-draw after updating spikes
    }

    fun clear():ArrayList<Float>{
        var amps = amplitudes.clone() as ArrayList<Float>
        amplitudes.clear()
        spikes.clear()
        invalidate()
        return amps
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        spikes.forEach {
            canvas.drawRoundRect(it, radius, radius, paint)
        }
    }
}
