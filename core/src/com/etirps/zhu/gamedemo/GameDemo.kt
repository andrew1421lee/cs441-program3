package com.etirps.zhu.gamedemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.math.*
import kotlin.random.Random

class GameDemo : ApplicationAdapter(), InputProcessor {

    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var shapes: ShapeRenderer
    private lateinit var fpsCounter: FrameRate
    private lateinit var input: InputData

    private lateinit var rockImg: Texture

    private lateinit var player: Player

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
        shapes = ShapeRenderer()

        // Setup input
        Gdx.input.inputProcessor = this
        input = InputData()

        // Setup fps counter
        fpsCounter = FrameRate()
        fpsCounter.resize(screenWidth, screenHeight)

        // Initialize textures
        rockImg = Texture("ref.png")

        // Initialize game objects
        player = Player(screenWidth / 2, screenHeight / 2, rockImg)
        stage.addActor(player)

        // Start the game
        spawnRock()
        spawnRock()
    }

    private fun spawnRock() {
        // Get x y positions for new rock
        val newPosX = (0..Gdx.graphics.width).random()
        val newPosY = (0..Gdx.graphics.height).random()

        // Get random speeds for x, y, and spin
        val newSpeedX = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()
        val newSpeedY = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()

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
        shapes.projectionMatrix = camera.combined

        // Update all actors in stage
        stage.act(Gdx.graphics.deltaTime)

        // Draw stage actors
        stage.draw()

        // Draw input shapes
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = Color.WHITE
        if(input.touchedDown) {
            // Draw a circle at initial touch point
            shapes.circle(player.x + player.height / 2, player.y + player.height /2, 20f)
        }
        if(input.dragging) {
            // Draw a line to where finger is currently
            shapes.rectLine(Vector2(player.x + player.height / 2, player.y + player.height /2),
                            Vector2(player.x + player.height / 2 - (input.origX - input.powerLineX),
                                    player.y + player.height /2 - (input.origY - input.powerLineY)), 20f)

            shapes.rectLine(Vector2(player.x + player.height / 2 - (input.origX - input.powerLineX),
                                    player.y + player.height /2 - (input.origY - input.powerLineY)),
                            Vector2(player.x + player.height / 2 - (input.origX - input.destX),
                                    player.y + player.height /2 - (input.origY - input.destY)), 3f)

            player.rotation = input.angle
        }
        shapes.end()

        // Update FPS
        fpsCounter.update()
        fpsCounter.render()

    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(!input.dragging) {
            input.touchedDown = false
            return true
        }
        // Discover what direction the bullet should be firing
        val adjacent = cos(input.powerLineAngle) * input.powerLineDistance
        val opposite = sin(input.powerLineAngle) * input.powerLineDistance

        // Create new bullet
        val bullet = Bullet(player.x + (player.width / 2), player.y + (player.height / 2), player.rotation, Vector2(opposite / 20f, adjacent / 20f), rockImg)
        stage.addActor(bullet)

        // Should unflag everything
        input.touchedDown = false
        input.dragging = false

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        // Convert screen touch coordinates to game coordinates
        val actualXY = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        // Do nothing if no distance was dragged
        if(input.origX - actualXY.x == 0f && input.origY - actualXY.y == 0f) {
            input.dragging = false
            return true
        }

        // Set input data to touch data
        input.destX = actualXY.x
        input.destY = actualXY.y
        // Set dragging flag to true, so renderer will draw the line
        input.dragging = true
        return true
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // Convert screen touch coordinates to game coordinates
        val actualXY = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))
        // Set input data to touch data
        input.origX = actualXY.x
        input.origY = actualXY.y
        // set touched flag to true, so renderer will draw circle
        input.touchedDown = true
        return true
    }

    override fun dispose() {
        batch.dispose()
        rockImg.dispose()
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }
}
