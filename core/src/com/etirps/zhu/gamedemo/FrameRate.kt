package com.etirps.zhu.gamedemo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils

/**
 * A nicer class for showing framerate that doesn't spam the console
 * like Logger.log()
 *
 * @author Original Java by William Hartman, Kotlin conversion by Anchu Lee
 */
class FrameRate (debugFont: BitmapFont?) : Disposable {
    private var lastTimeCounted: Long
    private var sinceChange: Float
    private var frameRate: Float
    private val font: BitmapFont?
    private val batch: SpriteBatch
    private var camera: OrthographicCamera

    init {
        lastTimeCounted = TimeUtils.millis()
        sinceChange = 0f
        frameRate = Gdx.graphics.framesPerSecond.toFloat()
        batch = SpriteBatch()
        camera = OrthographicCamera(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        font = debugFont
    }

    fun resize(screenWidth: Float, screenHeight: Float) {
        camera = OrthographicCamera(screenWidth, screenHeight)
        camera.translate((screenWidth / 2), (screenHeight / 2))
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    fun update() {
        if(font != null) {
            val delta = TimeUtils.timeSinceMillis(lastTimeCounted)
            lastTimeCounted = TimeUtils.millis()

            sinceChange += delta.toFloat()
            if (sinceChange >= 1000) {
                sinceChange = 0f
                frameRate = Gdx.graphics.framesPerSecond.toFloat()
            }
        }
    }

    fun render() {
        if(font != null) {
            batch.begin()
            font.draw(batch, frameRate.toInt().toString() + " fps", 5f, (Gdx.graphics.height - 5).toFloat())
            batch.end()
        }
    }

    override fun dispose() {
        font?.dispose()
        batch.dispose()
    }
}

