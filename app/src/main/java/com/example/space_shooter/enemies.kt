package com.example.space_shooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Enemy(
    var x: Float,
    var y: Float,
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    val width = 70f
    val height = 70f

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
        paint.color = Color.RED
        paint.style = Paint.Style.FILL

        val centerX = x + width / 2
        val centerY = y + height / 2
        val size = width / 2

        val path = android.graphics.Path()
        for (i in 0..7) {
            val angle = Math.PI * 2 * i / 8 - Math.PI / 2
            val px = centerX + (size * Math.cos(angle)).toFloat()
            val py = centerY + (size * Math.sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(px, py)
            } else {
                path.lineTo(px, py)
            }
        }
        path.close()

        canvas.drawPath(path, paint)

        paint.color = Color.rgb(139, 0, 0)
        canvas.drawCircle(centerX, centerY, size / 2, paint)

        paint.color = Color.BLACK
        canvas.drawCircle(centerX - 12, centerY - 8, 6f, paint)
        canvas.drawCircle(centerX + 12, centerY - 8, 6f, paint)

        paint.color = Color.rgb(255, 100, 100)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawPath(path, paint)

        paint.style = Paint.Style.FILL
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