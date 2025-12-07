package com.example.space_shooter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.MotionEvent
import kotlin.compareTo
import kotlin.div
import kotlin.text.compareTo
import kotlin.unaryMinus

class GameManager(private val gameView: GameView) {

    enum class GameState {
        MENU, PLAYING, PAUSED, GAME_OVER
    }

    private var gameState = GameState.MENU
    private var screenWidth = 0
    private var screenHeight = 0

    private lateinit var player: Player
    private val bullets = mutableListOf<Bullet>()
    private val enemies = mutableListOf<Enemy>()
    private val explosions = mutableListOf<Explosion>()

    // Sistema de disparo automático
    private var shootTimer = 0f
    private val shootInterval = 0.15f
    private var isShooting = false

    private var spawnTimer = 0f
    private var spawnInterval = 2.5f
    private var enemiesPerWave = 1

    private var score = 0
    private var lives = 3
    private var wave = 1
    private var enemiesKilled = 0

    private val startButton = Rect()
    private val continueButton = Rect()
    private val restartButton = Rect()
    private val menuButton = Rect()

    private var lastFrameTime = System.currentTimeMillis()

    fun initialize(width: Int, height: Int) {
        screenWidth = width
        screenHeight = height

        player = Player(screenWidth / 2f, screenHeight - 200f, screenWidth, screenHeight)

        val buttonWidth = 400
        val buttonHeight = 120

        startButton.set(
            screenWidth / 2 - buttonWidth / 2,
            screenHeight / 2,
            screenWidth / 2 + buttonWidth / 2,
            screenHeight / 2 + buttonHeight
        )

        continueButton.set(
            screenWidth / 2 - buttonWidth / 2,
            screenHeight / 2 - 80,
            screenWidth / 2 + buttonWidth / 2,
            screenHeight / 2 + 40
        )

        restartButton.set(
            screenWidth / 2 - buttonWidth / 2,
            screenHeight / 2 + 60,
            screenWidth / 2 + buttonWidth / 2,
            screenHeight / 2 + 180
        )

        menuButton.set(
            screenWidth / 2 - buttonWidth / 2,
            screenHeight / 2 + 200,
            screenWidth / 2 + buttonWidth / 2,
            screenHeight / 2 + 320
        )
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastFrameTime) / 1000f
        lastFrameTime = currentTime

        when (gameState) {
            GameState.PLAYING -> updatePlaying(deltaTime)
            GameState.MENU, GameState.PAUSED, GameState.GAME_OVER -> {}
        }
    }

    private fun updatePlaying(deltaTime: Float) {
        player.update(deltaTime)

        if (isShooting) {
            shootTimer += deltaTime
            if (shootTimer >= shootInterval) {
                shootTimer = 0f
                shoot()
            }
        }

        bullets.removeAll { bullet ->
            bullet.update(deltaTime)
            bullet.y < -bullet.height || bullet.isDestroyed
        }

        enemies.removeAll { enemy ->
            enemy.update(deltaTime)

            if (enemy.y > screenHeight) {
                lives--
                if (lives <= 0) {
                    gameState = GameState.GAME_OVER
                }
                return@removeAll true
            }

            enemy.isDestroyed
        }

        explosions.removeAll { explosion ->
            explosion.update(deltaTime)
            explosion.isFinished
        }

        spawnTimer += deltaTime
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0f
            spawnEnemies()
        }

        checkCollisions()

        if (enemies.isEmpty() && spawnTimer > spawnInterval - 0.5f) {
            nextWave()
        }
    }

    private fun spawnEnemies() {
        for (i in 0 until enemiesPerWave) {
            val x = (Math.random() * (screenWidth - 100) + 50).toFloat()
            val enemy = Enemy(x, -100f, screenWidth, screenHeight)
            enemies.add(enemy)
        }
    }

    private fun nextWave() {
        wave++
        if (wave % 2 == 0) {
            enemiesPerWave++
        }
        spawnInterval = Math.max(1.5f, spawnInterval - 0.15f)
    }

    private fun checkCollisions() {
        val bulletsToRemove = mutableListOf<Bullet>()
        val enemiesToRemove = mutableListOf<Enemy>()

        for (bullet in bullets) {
            for (enemy in enemies) {
                if (checkRectCollision(
                        bullet.x, bullet.y, bullet.width, bullet.height,
                        enemy.x, enemy.y, enemy.width, enemy.height
                    )
                ) {
                    bulletsToRemove.add(bullet)
                    enemiesToRemove.add(enemy)

                    explosions.add(Explosion(enemy.x + enemy.width / 2, enemy.y + enemy.height / 2))

                    score += 10
                    enemiesKilled++
                    break
                }
            }
        }

        bullets.removeAll { it in bulletsToRemove }
        enemies.removeAll { it in enemiesToRemove }

        for (enemy in enemies) {
            if (checkRectCollision(
                    player.x, player.y, player.width, player.height,
                    enemy.x, enemy.y, enemy.width, enemy.height
                )
            ) {
                explosions.add(Explosion(enemy.x + enemy.width / 2, enemy.y + enemy.height / 2))
                enemies.remove(enemy)
                lives--

                if (lives <= 0) {
                    gameState = GameState.GAME_OVER
                }
                break
            }
        }
    }

    private fun checkRectCollision(
        x1: Float, y1: Float, w1: Float, h1: Float,
        x2: Float, y2: Float, w2: Float, h2: Float
    ): Boolean {
        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2
    }

    fun render(canvas: Canvas, paint: Paint) {
        when (gameState) {
            GameState.MENU -> renderMenu(canvas, paint)
            GameState.PLAYING -> renderPlaying(canvas, paint)
            GameState.PAUSED -> renderPaused(canvas, paint)
            GameState.GAME_OVER -> renderGameOver(canvas, paint)
        }
    }

    private fun renderMenu(canvas: Canvas, paint: Paint) {
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("SPACE SHOOTER", screenWidth / 2f, screenHeight / 3f, paint)

        paint.color = Color.GREEN
        canvas.drawRect(startButton, paint)

        paint.color = Color.BLACK
        paint.textSize = 50f
        canvas.drawText(
            "INICIAR JUEGO",
            startButton.centerX().toFloat(),
            startButton.centerY().toFloat() + 15,
            paint
        )

        paint.color = Color.WHITE
    }

    private fun renderPlaying(canvas: Canvas, paint: Paint) {
        player.render(canvas, paint)

        for (bullet in bullets) {
            bullet.render(canvas, paint)
        }

        for (enemy in enemies) {
            enemy.render(canvas, paint)
        }

        for (explosion in explosions) {
            explosion.render(canvas, paint)
        }

        renderHUD(canvas, paint)
    }

    private fun renderHUD(canvas: Canvas, paint: Paint) {
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 40f
        paint.color = Color.WHITE

        canvas.drawText("Score: $score", 30f, 60f, paint)
        canvas.drawText("Lives: $lives", 30f, 120f, paint)
        canvas.drawText("Wave: $wave", 30f, 180f, paint)
    }

    private fun renderPaused(canvas: Canvas, paint: Paint) {
        paint.color = Color.argb(180, 0, 0, 0)
        canvas.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), paint)

        paint.color = Color.WHITE
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("PAUSA", screenWidth / 2f, screenHeight / 3f, paint)

        paint.color = Color.GREEN
        canvas.drawRect(continueButton, paint)

        paint.color = Color.BLACK
        paint.textSize = 50f
        canvas.drawText(
            "CONTINUAR",
            continueButton.centerX().toFloat(),
            continueButton.centerY().toFloat() + 15,
            paint
        )

        paint.color = Color.YELLOW
        canvas.drawRect(restartButton, paint)

        paint.color = Color.BLACK
        canvas.drawText(
            "REINICIAR",
            restartButton.centerX().toFloat(),
            restartButton.centerY().toFloat() + 15,
            paint
        )

        paint.color = Color.WHITE
    }

    private fun renderGameOver(canvas: Canvas, paint: Paint) {
        paint.textSize = 80f
        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.RED
        canvas.drawText("GAME OVER", screenWidth / 2f, screenHeight / 3f, paint)

        paint.color = Color.WHITE
        paint.textSize = 50f
        canvas.drawText("Score Final: $score", screenWidth / 2f, screenHeight / 2.5f, paint)
        canvas.drawText("Wave alcanzada: $wave", screenWidth / 2f, screenHeight / 2.2f, paint)

        paint.color = Color.CYAN
        canvas.drawRect(menuButton, paint)

        paint.color = Color.BLACK
        canvas.drawText(
            "VOLVER AL MENÚ",
            menuButton.centerX().toFloat(),
            menuButton.centerY().toFloat() + 15,
            paint
        )

        paint.color = Color.WHITE
    }

    fun handleTouch(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                when (gameState) {
                    GameState.MENU -> {
                        if (startButton.contains(x.toInt(), y.toInt())) {
                            startGame()
                        }
                    }
                    GameState.PLAYING -> {
                        isShooting = true
                        shootTimer = shootInterval
                        player.moveTo(x)
                    }
                    GameState.PAUSED -> {
                        if (continueButton.contains(x.toInt(), y.toInt())) {
                            gameState = GameState.PLAYING
                        } else if (restartButton.contains(x.toInt(), y.toInt())) {
                            startGame()
                        }
                    }
                    GameState.GAME_OVER -> {
                        if (menuButton.contains(x.toInt(), y.toInt())) {
                            gameState = GameState.MENU
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                when (gameState) {
                    GameState.PLAYING -> {
                        player.moveTo(event.x)
                    }
                    else -> {}
                }
            }
            MotionEvent.ACTION_UP -> {
                when (gameState) {
                    GameState.PLAYING -> {
                        isShooting = false
                        shootTimer = 0f
                    }
                    else -> {}
                }
            }
        }
    }

    private fun startGame() {
        score = 0
        lives = 3
        wave = 1
        enemiesKilled = 0
        spawnInterval = 2.5f
        enemiesPerWave = 1
        spawnTimer = 0f
        shootTimer = 0f
        isShooting = false

        bullets.clear()
        enemies.clear()
        explosions.clear()

        player.reset(screenWidth / 2f, screenHeight - 200f)

        gameState = GameState.PLAYING
        lastFrameTime = System.currentTimeMillis()
    }

    private fun shoot() {
        val bullet = Bullet(
            player.x + player.width / 2 - 5,
            player.y,
            screenWidth,
            screenHeight
        )
        bullets.add(bullet)
    }

    fun pause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED
        }
    }

    fun resume() {
    }

    fun handleBackPressed() {
        when (gameState) {
            GameState.PLAYING -> gameState = GameState.PAUSED
            GameState.PAUSED -> gameState = GameState.PLAYING
            else -> {}
        }
    }
}