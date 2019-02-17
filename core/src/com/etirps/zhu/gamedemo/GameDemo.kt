package com.etirps.zhu.gamedemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.random.Random

class GameDemo : ApplicationAdapter() {

    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var fpsCounter: FrameRate

    private lateinit var rockImg: Texture

    override fun create() {
        // Get screen size for easier use
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // Create camera and set to size of screen
        camera = OrthographicCamera()
        camera.setToOrtho(false, screenWidth, screenHeight)

        // Create viewport and stage for correctly viewing the game
        batch = SpriteBatch()
        stage = Stage(FitViewport(screenWidth, screenHeight, camera), batch)

        // Setup fps counter
        fpsCounter = FrameRate()
        fpsCounter.resize(screenWidth, screenHeight)

        // Initialize textures
        rockImg = Texture("ref.png")

        // Start the game
        spawnRock()
        spawnRock()
    }

    private fun spawnRock() {
        // Get x y positions for new rock
        val newPosX = (0..Gdx.graphics.width).random()
        val newPosY = (0..Gdx.graphics.height).random()

        // Get random speeds for x, y, and spin
        val newSpeedX = Random.nextInt(from = -20, until = 20).toFloat() / 10f
        val newSpeedY = Random.nextInt(from = -20, until = 20).toFloat() / 10f
        val newSpeedSpin = Random.nextInt(from = -20, until = 20).toFloat() / 100f

        // Create new rock
        val rock = Rock(newPosX.toFloat(), newPosY.toFloat(), rockImg, speedX = newSpeedX, speedY = newSpeedY, speedSpin = newSpeedSpin)

        // Add rock to stage
        stage.addActor(rock)
    }

    override fun render() {
        // Clear screen with black color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell batch to use coordinate system specified by camera
        batch.projectionMatrix = camera.combined

        // Update all actors in stage
        stage.act(Gdx.graphics.deltaTime)

        // Draw stage actors
        stage.draw()

        // Update FPS
        fpsCounter.update()
        fpsCounter.render()

    }

    override fun dispose() {
        batch.dispose()
        rockImg.dispose()
    }
}
