package com.example.localenlp_mobile_v1.Classes

import android.content.AttributionSource
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.util.jar.Attributes

class WaveFormView(context:Context,attrs:AttributeSet):View(context, attrs) {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()


    private var raduis = 6f
    private var w = 9f
    fun addAmplitude(amp:Float){
        amplitudes.add(amp)

        var left = 0f
        var top = 0f
        var right:Float = left + w
        var bottom:Float = amp


        spikes.add(RectF(left,top,right,bottom))

        invalidate()
    }
    init {
        paint.color = Color.rgb(244,81,30)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        spikes.forEach{
            canvas?.drawRoundRect(it,raduis,raduis,paint)
        }
    }
}