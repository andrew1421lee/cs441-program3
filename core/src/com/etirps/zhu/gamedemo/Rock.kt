package com.etirps.zhu.gamedemo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor

class Rock(posX: Float,             posY: Float,
           var texture: Texture,    var size: Float = 200f,
           var speedX: Float = 1f,  var speedY: Float = 1f,
           var speedSpin: Float = 0.1f): Actor() {

    // Set actor x,y to given by constructor
    init {
        x = posX
        y = posY
    }

    // Draw rock on screen
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, size / 2, size / 2, size, size, 1f, 1f, rotation, 0, 0, 500, 500, false, false)
    }

    // Update rock positions and rotation
    override fun act(delta: Float) {
        x += speedX
        y += speedY
        rotation += speedSpin

        when {
            x + (size / 2) > Gdx.graphics.width.toFloat() -> x = 0f - (size / 2)
            x + (size / 2) < 0 -> x = Gdx.graphics.width.toFloat() + (size / 2)

            y + (size / 2) > Gdx.graphics.height.toFloat() -> y = 0f - (size / 2)
            y + (size / 2) < 0 -> y = Gdx.graphics.height.toFloat() + (size / 2)
        }
    }
}