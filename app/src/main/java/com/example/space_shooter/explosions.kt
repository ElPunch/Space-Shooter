package com.example.space_shooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Explosion - Clase de animación de explosión
 * Gestiona efecto visual de partículas al destruir enemigos
 */
class Explosion(
    private val x: Float,
    private val y: Float
) {

    private val particles = mutableListOf<Particle>()
    private val duration = 0.6f // segundos
    private var timer = 0f

    var isFinished = false

    init {
        // Crear partículas
        for (i in 0 until 20) {
            val angle = Math.random() * Math.PI * 2
            val speed = (100 + Math.random() * 200).toFloat()

            particles.add(
                Particle(
                    x, y,
                    (Math.cos(angle) * speed).toFloat(),
                    (Math.sin(angle) * speed).toFloat()
                )
            )
        }
    }

    /**
     * Actualiza la animación de la explosión
     */
    fun update(deltaTime: Float) {
        timer += deltaTime

        if (timer >= duration) {
            isFinished = true
            return
        }

        for (particle in particles) {
            particle.update(deltaTime)
        }
    }

    /**
     * Renderiza la explosión
     */
    fun render(canvas: Canvas, paint: Paint) {
        val alpha = (255 * (1 - timer / duration)).toInt()

        for (particle in particles) {
            particle.render(canvas, paint, alpha)
        }
    }

    /**
     * Particle - Clase interna para partículas de explosión
     */
    private class Particle(
        var x: Float,
        var y: Float,
        private val velocityX: Float,
        private val velocityY: Float
    ) {

        private val size = (5 + Math.random() * 10).toFloat()
        private val color = when ((Math.random() * 3).toInt()) {
            0 -> Color.RED
            1 -> Color.YELLOW
            else -> Color.rgb(255, 165, 0) // Naranja
        }

        fun update(deltaTime: Float) {
            x += velocityX * deltaTime
            y += velocityY * deltaTime
        }

        fun render(canvas: Canvas, paint: Paint, alpha: Int) {
            paint.color = Color.argb(
                alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )

            paint.style = Paint.Style.FILL
            canvas.drawCircle(x, y, size, paint)
        }
    }
}