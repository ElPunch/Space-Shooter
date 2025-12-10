package com.example.space_shooter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

/**
 * Player - Clase de la nave del jugador
 * Gestiona movimiento, restricciones y renderizado
 */
class Player(
    var x: Float,
    var y: Float,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val bitmap: Bitmap
) {

    val width = 240f
    val height = 300f

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
        canvas.drawBitmap(bitmap, x, y, paint)
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