package com.etirps.zhu.gamedemo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor

class Bullet(posX: Float, posY: Float, angle: Float, var speed: Vector2, var texture: Texture, debugFont: BitmapFont? = null): Actor() {

    private var distanceTraveled: Float
    private val font: BitmapFont?

    init {
        x = posX
        y = posY
        width = 20f
        height = 20f
        rotation = angle
        distanceTraveled = 0f
        font = debugFont
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, 500, 500, false, false)

        font?.draw(batch, "$rotation\n${speed.x} x ${speed.y}", x, y)
    }

    override fun act(delta: Float) {
        x += speed.x
        y += speed.y

        distanceTraveled += 20

        if(distanceTraveled > 2000) {
            this.remove()
        }

        when {
            x + (width / 2f) > Gdx.graphics.width.toFloat()     ->  x = 0f - (width / 2f)
            x + (width / 2f) < 0                                ->  x = Gdx.graphics.width.toFloat() - (width / 2f)

            y + (height / 2f) > Gdx.graphics.height.toFloat()   ->  y = 0f - (height / 2f)
            y + (height / 2f) < 0                               ->  y = Gdx.graphics.height.toFloat() - (height / 2f)
        }
    }
}