package com.example.space_shooter

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * GameView - Vista principal del juego
 * Gestiona el renderizado, el loop del juego y los eventos táctiles
 */
class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private val gameThread: GameThread
    private val gameManager: GameManager
    private val paint = Paint()

    // Dimensiones de pantalla
    var screenWidth: Int = 0
    var screenHeight: Int = 0

    init {
        holder.addCallback(this)
        isFocusable = true

        gameManager = GameManager(this)
        gameThread = GameThread(holder, this)

        // Configurar Paint para texto
        paint.textSize = 40f
        paint.color = Color.WHITE
        paint.typeface = Typeface.DEFAULT_BOLD
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        screenWidth = width
        screenHeight = height
        gameManager.initialize(screenWidth, screenHeight)
        gameThread.setRunning(true)
        gameThread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameThread.setRunning(false)
                gameThread.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gameManager.handleTouch(event)
        return true
    }

    /**
     * Actualiza la lógica del juego
     */
    fun update() {
        gameManager.update()
    }

    /**
     * Renderiza todos los elementos del juego
     */
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        // Fondo manejado en GameManager


        gameManager.render(canvas, paint)
    }

    fun resume() {
        gameManager.resume()
    }

    fun pause() {
        gameManager.pause()
    }

    fun handleBackPressed() {
        gameManager.handleBackPressed()
    }
}

/**
 * GameThread - Hilo principal del juego
 * Ejecuta el game loop a 60 FPS
 */
class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: GameView
) : Thread() {

    private var running = false
    private val targetFPS = 60
    private val targetTime = (1000 / targetFPS).toLong()

    fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    override fun run() {
        var startTime: Long
        var timeMillis: Long
        var waitTime: Long

        while (running) {
            startTime = System.currentTimeMillis()
            var canvas: Canvas? = null

            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.update()
                    canvas?.let { gameView.draw(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            timeMillis = System.currentTimeMillis() - startTime
            waitTime = targetTime - timeMillis

            try {
                if (waitTime > 0) {
                    sleep(waitTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}