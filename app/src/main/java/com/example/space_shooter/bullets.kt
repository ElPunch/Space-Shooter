package com.example.space_shooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Bullet(
    var x: Float,
    var y: Float,
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    val width = 10f
    val height = 30f

    private val speed = 900f
    var isDestroyed = false

    fun update(deltaTime: Float) {
        y -= speed * deltaTime
    }

    fun render(canvas: Canvas, paint: Paint) {
        paint.color = Color.YELLOW
        paint.style = Paint.Style.FILL

        canvas.drawRect(x, y, x + width, y + height, paint)

        paint.color = Color.WHITE
        canvas.drawRect(x + 2, y + 2, x + width - 2, y + height / 2, paint)

        paint.color = Color.rgb(255, 200, 0)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(x, y, x + width, y + height, paint)

        paint.style = Paint.Style.FILL
    }

    fun destroy() {
        isDestroyed = true
    }
}