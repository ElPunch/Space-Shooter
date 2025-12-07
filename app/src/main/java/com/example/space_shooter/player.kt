package com.example.space_shooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * Player - Clase de la nave del jugador
 * Gestiona movimiento, restricciones y renderizado
 */
class Player(
    var x: Float,
    var y: Float,
    private val screenWidth: Int,
    private val screenHeight: Int
) {

    val width = 80f
    val height = 100f

    private var targetX = x
    private val speed = 800f // píxeles por segundo

    init {
        // Asegurar que el jugador no salga de pantalla al iniciar
        clampPosition()
    }

    /**
     * Actualiza la posición del jugador
     */
    fun update(deltaTime: Float) {
        // Movimiento suave hacia la posición objetivo
        if (x != targetX) {
            val direction = if (targetX > x) 1 else -1
            val distance = Math.abs(targetX - x)
            val movement = speed * deltaTime

            if (movement >= distance) {
                x = targetX
            } else {
                x += direction * movement
            }
        }

        clampPosition()
    }

    /**
     * Establece la posición objetivo del jugador (donde se movera)
     */
    fun moveTo(targetX: Float) {
        this.targetX = targetX - width / 2
        clampTargetPosition()
    }

    /**
     * Restringe la posición para que no salga de la pantalla
     */
    private fun clampPosition() {
        if (x < 0) x = 0f
        if (x > screenWidth - width) x = screenWidth - width
    }

    /**
     * Restringe la posición objetivo
     */
    private fun clampTargetPosition() {
        if (targetX < 0) targetX = 0f
        if (targetX > screenWidth - width) targetX = screenWidth - width
    }

    /**
     * Renderiza la nave del jugador
     */
    fun render(canvas: Canvas, paint: Paint) {
        // Cuerpo principal (triángulo)
        paint.color = Color.CYAN
        paint.style = Paint.Style.FILL

        val path = android.graphics.Path()
        path.moveTo(x + width / 2, y) // Punta superior
        path.lineTo(x, y + height) // Esquina inferior izquierda
        path.lineTo(x + width, y + height) // Esquina inferior derecha
        path.close()

        canvas.drawPath(path, paint)

        // Cabina (círculo pequeño)
        paint.color = Color.BLUE
        canvas.drawCircle(x + width / 2, y + height / 3, 15f, paint)

        // Alas
        paint.color = Color.GRAY
        canvas.drawRect(x - 10, y + height - 30, x + 10, y + height - 10, paint)
        canvas.drawRect(x + width - 10, y + height - 30, x + width + 10, y + height - 10, paint)

        // Borde
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        canvas.drawPath(path, paint)

        paint.style = Paint.Style.FILL
    }

    /**
     * Reinicia la posición del jugador
     */
    fun reset(newX: Float, newY: Float) {
        x = newX
        y = newY
        targetX = x
        clampPosition()
    }
}