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
    private lateinit var bulletImg: Texture
    private lateinit var playerImg: Texture

    private lateinit var player: Player
    private lateinit var rocks: MutableList<Rock>
    private lateinit var bullets: MutableList<Bullet>


    var coolDown = 500
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
        fpsCounter = FrameRate(debugFont)
        fpsCounter.resize(screenWidth, screenHeight)
        val fontGenerator = FreeTypeFontGenerator(Gdx.files.internal("Roboto.TTF"))
        val fontParameters = FreeTypeFontGenerator.FreeTypeFontParameter()
        fontParameters.size = 60
        hud = fontGenerator.generateFont(fontParameters)
        hud.setFixedWidthGlyphs("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz")
        fontGenerator.dispose()

        // ENABLE OR DISABLE DEBUG MODE
        //debugFont = BitmapFont()

        // Initialize textures
        rockImg = Texture("rock5.png")
        explosionImg = Texture("explosion2.png")
        bulletImg = Texture("bullet.png")
        playerImg = Texture("player.png")

        // Initialize game objects
        rocks = mutableListOf()
        bullets = mutableListOf()

        // Start the game
        setupStage()
    }

    private fun setupStage() {
        coolDown = 500

        // Reset input values
        input.origX = 0f
        input.origY = 0f
        input.destX = 0f
        input.destY = 0f

        player = Player(Gdx.graphics.width.toFloat() / 2, Gdx.graphics.height.toFloat() / 2, playerImg, debugFont = debugFont)
        player.addStatusEffect(InvincibleEffect(3f))
        stage.addActor(player)
        spawnRock(5)
    }

    private fun clearStage() {
        // Remove all actors from the stage
        for (rock in rocks) {
            rock.remove()
        }
        rocks.clear()

        for (bullet in bullets) {
            bullet.remove()
        }
        bullets.clear()

        player.remove()
    }

    private fun spawnRock(numberOfRocks: Int) {
        for (i in 1..numberOfRocks){
            // Get x y positions for new rock
            val newPosX = (0..Gdx.graphics.width).random()
            val newPosY = (0..Gdx.graphics.height).random()

            // Get random speeds for x, y, and spin
            var newSpeedX = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()
            if(newSpeedX == 0f) { newSpeedX = 1f }

            var newSpeedY = (Random.nextInt(from = 10, until = 20).toFloat() / 10f) * (-1..1).random()
            if(newSpeedY == 0f) { newSpeedY = 1f }

            var newSpeedSpin = Random.nextInt(from = 10, until = 20).toFloat() / 75f * (-1..1).random()
            if(newSpeedSpin == 0f) { newSpeedSpin = 10f / 75f }

            // Create new rock
            val rock = Rock(newPosX.toFloat(), newPosY.toFloat(), rockImg, speedX = newSpeedX, speedY = newSpeedY, speedSpin = newSpeedSpin, debugFont = debugFont)

            // Add rock to stage
            rocks.add(rock)
            stage.addActor(rock)
        }
    }

    private fun checkCollision() {

        // Create lists for removed rocks, bullets, and created rocks
        val deadBullets = mutableListOf<Bullet>()
        val deadRocks = mutableListOf<Rock>()
        val newRocks = mutableListOf<Rock>()

        // Check if the player hit anything
        for(rock in rocks) {
            if(player.shielded || gameOver) { break }

            // Player collided with rock
            if(Intersector.overlapConvexPolygons(player.polygon, rock.polygon)) {
                // Set game over flag
                gameOver = true

                // Split the rock, add it to new rocks list
                newRocks.addAll(rock.split())
                // Remove the rock from the stage and add it to dead rocks list
                rock.remove()
                deadRocks.add(rock)

                // Create explosion
                stage.addActor(Explosion(player.x - player.width / 2, player.y - player.width / 2, player.width, explosionImg, 1000f, debugFont =  debugFont))
                stage.addActor(Explosion(player.x - player.width * 2f / 2, player.y - player.width * 2f / 2, player.width * 2f, explosionImg, 500f, debugFont))
                stage.addActor(Explosion(player.x - player.width * 3f / 2, player.y - player.width * 3f / 2, player.width * 3f, explosionImg, 250f, debugFont))

                // Remove player
                player.remove()
            }
        }

        // Remove all rocks so removed rocks aren't used for next step
        rocks.removeAll(deadRocks)
        deadRocks.clear()

        // For every bullet on screen
        for(bullet in bullets) {
            // If, for some reason, bullet is not active, remove it and go next
            if(!bullet.active) {
                deadBullets.add(bullet)
                continue
            }

            // For every rock on screen
            for(rock in rocks) {
                // If the bullet and rock collided
                if(Intersector.overlapConvexPolygons(bullet.polygon, rock.polygon)) {
                    // Split the rock and add the result
                    newRocks.addAll(rock.split())

                    // Remove the bullet and rock
                    bullet.remove()
                    rock.remove()

                    // Add them to be removed later
                    deadRocks.add(rock)
                    deadBullets.add(bullet)

                    // Add explosion
                    stage.addActor(Explosion(rock.x - rock.size * 0.5f / 2, rock.y - rock.size * 0.5f / 2, rock.size * 0.5f, explosionImg, 500f, debugFont = debugFont))
                    stage.addActor(Explosion(rock.x - rock.size / 2, rock.y - rock.size / 2, rock.size, explosionImg, 250f, debugFont = debugFont))

                    // Update the score
                    player.score += 250 - rock.size.toInt()
                }
            }
        }

        // Remove all resulting dead rocks and bullets
        bullets.removeAll(deadBullets)
        rocks.removeAll(deadRocks)

        // Add all new rocks
        for(newRock in newRocks) {
            stage.addActor(newRock)
        }
        rocks.addAll(newRocks)

    }

    private fun drawAimingReticule() {
        shapes.begin(ShapeRenderer.ShapeType.Filled)
        shapes.color = Color.WHITE

        if(input.touchedDown) {
            // Draw a circle at initial touch point
            shapes.circle(player.x + player.height / 2, player.y + player.height /2, 10f)
        }

        if(input.dragging) {
            // Draw a line to where finger is currently
            shapes.rectLine(Vector2(player.x + player.height / 2, player.y + player.height /2),
                    Vector2(player.x + player.height / 2 - (input.origX - input.aimingX),
                            player.y + player.height /2 - (input.origY - input.aimingY)), 10f)

            shapes.rectLine(Vector2(player.x + player.height / 2 - (input.origX - input.aimingX),
                    player.y + player.height /2 - (input.origY - input.aimingY)),
                    Vector2(player.x + player.height / 2 - (input.origX - input.destX),
                            player.y + player.height /2 - (input.origY - input.destY)), 3f)

        }

        shapes.end()
    }

    private fun drawHUD() {
        // Get values to display on HUD
        //val speed = String.format("%.1f", sqrt((player.x + player.speedX - player.x).pow(2) + (player.y + player.speedY - player.y).pow(2)))
        val speedX = String.format("%.1f", player.speedX)
        val speedY= String.format("%.1f", player.speedY)

        // Start drawing the hud
        batch.begin()
        // Draw the score display
        hud.draw(batch, "SCORE: ${player.score}", Gdx.graphics.width - 400f, Gdx.graphics.height - 50f)
        // Draw speedometers
        hud.draw(batch, "SPEED X: $speedX", Gdx.graphics.width / 2 - 400f, 75f)
        hud.draw(batch, "SPEED Y: $speedY", Gdx.graphics.width / 2 + 100f, 75f)

        // Draw status effects
        val textOffset = hud.xHeight
        val lineOffset = hud.xHeight + 140f
        var textCounter = 0
        for (statusEffect in player.getStatusEffects()) {
            if(!statusEffect.enabled) { continue }

            val textWidth = statusEffect.toString().toCharArray().size * 30f
            val timeRemaining = String.format("%.1f", statusEffect.timeRemaining)
            val timeRemainingWidth = timeRemaining.toCharArray().size * 30f
            hud.draw(batch, statusEffect.toString(), Gdx.graphics.width / 2f - textWidth, Gdx.graphics.height - ((textCounter * lineOffset) + textOffset + 20f))
            hud.draw(batch, timeRemaining, Gdx.graphics.width / 2f - timeRemainingWidth, Gdx.graphics.height - ((textCounter * lineOffset) + textOffset + 100f))
            textCounter++
        }

        // Check for game over
        if(gameOver) {
            hud.draw(batch, "GAME OVER", Gdx.graphics.width / 2f - 100f, Gdx.graphics.height / 2f)
            if(coolDown > 0) {
                hud.draw(batch, "$coolDown", Gdx.graphics.width / 2f - 5f, Gdx.graphics.height / 2f - 100f)
                coolDown -= 5
            } else {
                hud.draw(batch, "TOUCH TO PLAY AGAIN", Gdx.graphics.width / 2f - 250f, Gdx.graphics.height / 2f - 100f)
            }

        }

        // Finish drawing
        batch.end()
    }

    private fun fire() {
        // Discover power level based on input distance
        val adjacent = cos(input.aimingAngle) * input.aimingDistance
        val opposite = sin(input.aimingAngle) * input.aimingDistance

        // Create new bullet
        val bullet = Bullet(player.x + (player.width / 2), player.y + (player.height / 2), player.rotation, Vector2(opposite / 30f, adjacent / 30f), bulletImg, debugFont)

        // Push the player in the other direction
        player.speedX += -opposite / player.mass
        player.speedY += -adjacent / player.mass

        // Add bullets to stage
        bullets.add(bullet)
        stage.addActor(bullet)
    }

    private fun checkOnTouch() {
        // If flag is set
        if(input.touchedUp) {
            // if game is not over
            if(!gameOver) {
                // Fire bullet
                fire()
                input.touchedUp = false
            // Check if game over cool down is over
            } else if(coolDown <= 0) {
                // Reset input flags
                input.touchedUp = false
                // Reset the game
                gameOver = false
                clearStage()
                setupStage()
                return
            }
        }
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

        if(input.angle.isNaN()) {
            player.rotation = 0f
        } else {
            player.rotation = input.angle
        }

        // Update all actors in stage
        stage.act(Gdx.graphics.deltaTime)

        // Draw stage actors
        stage.draw()

        // Draw aiming reticule
        if(!gameOver) {
            drawAimingReticule()
        }

        checkOnTouch()
        drawHUD()
        checkCollision()

    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        // Set flag
        input.touchedUp = true

        input.touchedDown = false
        input.dragging = false

        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        // Convert screen touch coordinates to game coordinates
        val actualXY = camera.unproject(Vector3(screenX.toFloat(), screenY.toFloat(), 0f))

        // Do not update if no distance was dragged
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
