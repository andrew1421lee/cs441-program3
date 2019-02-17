package com.etirps.zhu.gamedemo

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle

class GameDemo : ApplicationAdapter() {

    private lateinit var camera: OrthographicCamera
    private lateinit var batch: SpriteBatch
    private lateinit var img: Texture
    private lateinit var ship: Rectangle
    private lateinit var fpsCounter: FrameRate

    override fun create() {
        camera = OrthographicCamera()
        camera.setToOrtho(false, 1920f, 1080f)

        batch = SpriteBatch()

        fpsCounter = FrameRate()
        fpsCounter.resize(camera.viewportWidth, camera.viewportHeight)

        img = Texture("badlogic.jpg")

        ship = Rectangle()
        ship.x = 50f
        ship.y = 50f
        ship.width = 25f
        ship.height = 25f
    }

    override fun render() {
        // Clear screen with black color
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Update camera matrices
        camera.update()

        // Tell batch to use coordinate system specified by camera
        batch.projectionMatrix = camera.combined

        // Draw sprites
        batch.begin()
        batch.draw(img, ship.x, ship.y, 25f, 25f)
        batch.end()

        fpsCounter.update()
        fpsCounter.render()


        ship.x += 200 * Gdx.graphics.deltaTime
    }

    override fun dispose() {
        batch.dispose()
        img.dispose()
    }
}
