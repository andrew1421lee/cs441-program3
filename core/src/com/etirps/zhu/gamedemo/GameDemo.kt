package com.etirps.zhu.gamedemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import kotlin.math.*
import kotlin.random.Random
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator



class GameDemo : ApplicationAdapter(), InputProcessor {

    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var shapes: ShapeRenderer
    private lateinit var fpsCounter: FrameRate
    private lateinit var input: InputData
    private var debugFont: BitmapFont? = null
    private lateinit var hud: BitmapFont

    private lateinit var rockImg: Texture
    private lateinit var explosionImg: Texture

    private lateinit var player: Player
    private lateinit var rocks: MutableList<Rock>
    private lateinit var bullets: MutableList<Bullet>

    var gameOver = false

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

        // Setup fps counter + hud
        fpsCounter = FrameRate()
        fpsCounter.resize(screenWidth, screenHeight)
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("Roboto.TTF"))
        val fontParameters = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameters.size = 60
        hud = fontGenerator.generateFont(fontParameters)
        fontGenerator.dispose()

        //hud.data.scale(2f)
        //hud.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

        // ENABLE OR DISABLE DEBUG MODE
        debugFont = BitmapFont()

        // Initialize textures
        rockImg = Texture("ref.png")
        explosionImg = Texture("explosion2.png")

        // Initialize game objects
        player = Player(screenWidth / 2, screenHeight / 2, rockImg, debugFont = debugFont)
        stage.addActor(player)
        rocks = mutableListOf()
        bullets = mutableListOf()

        // Start the game
        spawnRock()
        spawnRock()
        spawnRock()
        spawnRock()
        spawnRock()
    }

    private fun spawnRock() {
        // Get x y positions for new rock
        val newPosX = (0..Gdx.graphics.width).random()
        val newPosY = (0..Gdx.graphics.height).random()

        // Get random speeds for x, y, and spin
        var newSpeedX = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()
        if(newSpeedX == 0f) { newSpeedX = 1f }

        var newSpeedY = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()
        if(newSpeedY == 0f) { newSpeedY = 1f }

        val newSpeedSpin = Random.nextInt(from = -20, until = 20).toFloat() / 100f

        // Create new rock
        val rock = Rock(newPosX.toFloat(), newPosY.toFloat(), rockImg, speedX = newSpeedX, speedY = newSpeedY, speedSpin = newSpeedSpin, debugFont = debugFont)

        // Add rock to stage
        rocks.add(rock)
        stage.addActor(rock)
    }

    private fun checkCollision() {

        val deadBullets = mutableListOf<Bullet>()
        val deadRocks = mutableListOf<Rock>()
        val newRocks = mutableListOf<Rock>()

        for(rock in rocks) {
            if(player.shield > 0 || gameOver) { break }

            if(Intersector.overlapConvexPolygons(player.polygon, rock.polygon)) {
                gameOver = true
                newRocks.addAll(rock.split())
                rock.remove()
                deadRocks.add(rock)
                player.remove()
            }
        }

        rocks.removeAll(deadRocks)

        for(bullet in bullets) {
            if(!bullet.active) {
                deadBullets.add(bullet)
                continue
            }

            for(rock in rocks) {
                if(Intersector.overlapConvexPolygons(bullet.polygon, rock.polygon)) {
                    newRocks.addAll(rock.split())

                    bullet.remove()
                    rock.remove()

                    deadRocks.add(rock)
                    deadBullets.add(bullet)

                    stage.addActor(Explosion(rock.x - rock.size / 2, rock.y - rock.size / 2, rock.size * 1.5f, explosionImg, debugFont))
                }
            }
        }

        bullets.removeAll(deadBullets)
        rocks.removeAll(deadRocks)

        for(newRock in newRocks) {
            stage.addActor(newRock)
        }

        rocks.addAll(newRocks)
    }

    private fun actOnInput() {
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
    }

    private fun updateHUD() {
        val speed = sqrt((player.x + player.speedX - player.x).pow(2) + (player.y + player.speedY - player.y).pow(2)).roundToInt()

        batch.begin()
        hud.draw(batch, "SPEED: $speed", 50f, 50f)
        batch.end()
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

        // Update FPS
        fpsCounter.update()
        fpsCounter.render()

        // Update all actors in stage
        stage.act(Gdx.graphics.deltaTime)

        // Draw stage actors
        stage.draw()

        // Draw input shapes
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = Color.WHITE

        if(!gameOver) {
            actOnInput()
        }

        updateHUD()

        shapes.end()

        checkCollision()

    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if(!input.dragging || gameOver) {
            input.touchedDown = false
            return true
        }
        // Discover what direction the bullet should be firing
        val adjacent = cos(input.powerLineAngle) * input.powerLineDistance
        val opposite = sin(input.powerLineAngle) * input.powerLineDistance

        // Create new bullet
        val bullet = Bullet(player.x + (player.width / 2), player.y + (player.height / 2), player.rotation, Vector2(opposite / 30f, adjacent / 30f), rockImg, debugFont)

        player.speedX += -opposite / 30f
        player.speedY += -adjacent / 30f

        bullets.add(bullet)
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
