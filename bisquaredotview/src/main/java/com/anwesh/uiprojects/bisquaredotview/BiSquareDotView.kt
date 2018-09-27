package com.anwesh.uiprojects.bisquaredotview

/**
 * Created by anweshmishra on 27/09/18.
 */


import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.content.Context
import android.graphics.RectF

val nodes : Int = 5

fun Canvas.drawBSDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / nodes
    val size : Float = gap / 3
    val r : Float = size/5
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(scale - 0.5f, 0f)) * 2
    val sf : Float = 1f - 2 * i
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4A148C")
    save()
    translate(w/2 + sf * sc2 * (w/2 - size), h/2)
    rotate(90f * sf * sc2)
    for (j in 0..1) {
        val sc : Float = Math.min(0.5f, Math.max(0f, sc1 - 0.5f * j)) * 2
        val sj : Float = 1f - 2 * j
        save()
        scale(sj, sj)
        for (p in 0..2) {
            save()
            translate(size / 2, size/2 * (1 - 2 * p))
            paint.style = Paint.Style.STROKE
            drawCircle(0f, 0f, r, paint)
            paint.style = Paint.Style.FILL
            drawArc(RectF(-r, -r, r, r), 0f, 360f * sc, true, paint)
            restore()
            val a : Float = size/2 * sc * p
            val b : Float = size/2 * sc * (1 - p)
            val a1 : Float = size/2 * (1 - p)
            val b1 : Float = size/2 * p
            drawLine(-a + a1, -b + b1, a + a1, b + b1, paint)
        }
        restore()
    }
    restore()
}

class BiSquareDotView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}