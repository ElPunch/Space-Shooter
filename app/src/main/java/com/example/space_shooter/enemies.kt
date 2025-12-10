package com.example.space_shooter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Enemy(
    var x: Float,
    var y: Float,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val bitmap: Bitmap
) {

    val width = 210f
    val height = 210f

    // Velocidad más lenta: 100-180 en lugar de 200-350
    private val baseSpeed = 100f
    private val speed = baseSpeed + (Math.random() * 80).toFloat()

    // Movimiento lateral más lento
    private val lateralSpeed = (Math.random() * 50 - 25).toFloat()
    private var direction = if (Math.random() > 0.5) 1 else -1

    var isDestroyed = false
    var hp = 1

    fun update(deltaTime: Float) {
        y += speed * deltaTime
        x += lateralSpeed * direction * deltaTime

        if (x < 0 || x > screenWidth - width) {
            direction *= -1
        }
    }

    fun render(canvas: Canvas, paint: Paint) {
        canvas.drawBitmap(bitmap, x, y, paint)
    }

    fun destroy() {
        isDestroyed = true
    }

    fun takeDamage(damage: Int) {
        hp -= damage
        if (hp <= 0) {
            destroy()
        }
    }
}