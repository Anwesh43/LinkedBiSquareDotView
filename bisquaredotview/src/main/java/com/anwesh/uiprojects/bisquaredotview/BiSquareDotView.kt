package com.anwesh.uiprojects.bisquaredotview

/**
 * Created by anweshmishra on 27/09/18.
 */


import android.app.Activity
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
    val gap : Float = h / (nodes + 1)
    val size : Float = gap / 3.5f
    val r : Float = size/3
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(scale - 0.5f, 0f)) * 2
    val sf : Float = 1f - 2 * (i % 2)
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    paint.color = Color.parseColor("#4A148C")
    save()
    translate(w/2 + sf * sc2 * (w/2 - size -r), gap + i * gap)
    rotate(90f * sf * sc2)
    for (j in 0..1) {
        val sc : Float = Math.min(0.5f, Math.max(0f, sc1 - 0.5f * j)) * 2
        val sj : Float = 1f - 2 * j
        save()
        scale(sj, sj)
        for (p in 0..1) {
            save()
            translate(size , size * (1 - 2 * p))
            paint.style = Paint.Style.STROKE
            drawCircle(0f, 0f, r, paint)
            paint.style = Paint.Style.FILL
            drawArc(RectF(-r, -r, r, r), 0f, 360f * sc, true, paint)
            restore()
            val a : Float = size * sc * p
            val b : Float = size * sc * (1 - p)
            val a1 : Float = size * (1 - p)
            val b1 : Float = size * p
            drawLine(-a + a1, -b + b1, a + a1, b + b1, paint)
        }
        restore()
    }
    restore()
}

class BiSquareDotView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += (0.025f + 0.025f * (Math.min(1f, Math.floor(scale.toDouble() * 2).toFloat()))) * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BSDNode(var i : Int, val state : State = State()) {

        private var next : BSDNode? = null

        private var prev : BSDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BSDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBSDNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BSDNode {
            var curr : BSDNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BiSquareDot(var i : Int) {

        private var root : BSDNode = BSDNode(0)
        private var curr : BSDNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BiSquareDotView) {

        private val bds : BiSquareDot = BiSquareDot(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            bds.draw(canvas, paint)
            animator.animate {
                bds.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bds.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : BiSquareDotView {
            val view : BiSquareDotView = BiSquareDotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}