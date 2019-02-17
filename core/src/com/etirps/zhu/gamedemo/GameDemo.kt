package com.etirps.zhu.gamedemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch

class GameDemo : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var font: BitmapFont
    private lateinit var fpsCounter: FrameRate
    private lateinit var rockCollection: MutableList<Rock>

    private lateinit var rockImg: Texture

    override fun create() {
        // Setup camera and size of view
        camera = OrthographicCamera()
        camera.setToOrtho(false, 1920f, 1080f)

        batch = SpriteBatch()
        font = BitmapFont()

        fpsCounter = FrameRate()
        fpsCounter.resize(camera.viewportWidth, camera.viewportHeight)

        rockCollection = mutableListOf()

        rockImg = Texture("rock1.png")

        spawnRock()
    }

    fun spawnRock() {
        val newPosX = (0..1920).random()
        val newPosY = (0..1080).random()

        println("$newPosX x $newPosY")

        val rock = Rock(newPosX.toFloat(), newPosY.toFloat(), image = rockImg)

        rockCollection.add(rock)
    }

    override fun render() {
        // Clear screen with black color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell batch to use coordinate system specified by camera
        batch.projectionMatrix = camera.combined

        // Draw all the objects
        batch.begin()

        for (rock in rockCollection) {
            batch.draw(rock.image, rock.x, rock.y, rock.size, rock.size)
            font.draw(batch, "${rock.x} x ${rock.y}", rock.x, rock.y)
        }

        batch.end()

        fpsCounter.update()
        fpsCounter.render()

        for(rock in rockCollection) {
            rock.update()
        }

        //ship.x += 200 * Gdx.graphics.deltaTime
    }

    override fun dispose() {
        batch.dispose()
        rockImg.dispose()
    }
}
