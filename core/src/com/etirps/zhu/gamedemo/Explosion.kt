package com.etirps.zhu.gamedemo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor

class Explosion(posX: Float, posY: Float, size: Float, var texture: Texture, duration: Float = 25f, debugFont: BitmapFont? = null): Actor() {

    private var life: Float
    private var initLife: Float
    private val font: BitmapFont?

    init {
        x = posX
        y = posY
        width = size
        height = size

        originX = width / 2f
        originY = height / 2f

        font = debugFont

        life = duration
        initLife = life
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width / 2f, height / 2f, width, height, 1f, 1f, rotation, 0, 0, 628, 628, false, false)

        font?.draw(batch, "angle:$rotation\npos:$x x $y\nlife:$life", x, y)
    }

    override fun act(delta: Float) {

        life -= 1f

        // Reduce size of explosion at half life
        if(life < initLife / 2) {
            x += width / 4f
            y += height / 4f

            width *= 0.5f
            height *= 0.5f
            initLife = 0f
        }

        if(life < 0) {
            this.remove()
        }
    }
}