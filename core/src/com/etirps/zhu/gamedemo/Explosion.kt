package com.etirps.zhu.gamedemo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor

class Explosion(posX: Float, posY: Float, size: Float, var texture: Texture, duration: Float = 500f, debugFont: BitmapFont? = null): Actor() {

    private var life: Float
    private val font: BitmapFont?

    init {
        x = posX
        y = posY
        width = size
        height = size

        originX = width /2
        originY = height /2

        font = debugFont

        life = duration
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, 628, 628, false, false)

        font?.draw(batch, "angle:$rotation\npos:$x x $y\nlife:$life", x, y)
    }

    override fun act(delta: Float) {

        life -= 20
        if(life < 0) {
            this.remove()
        }
    }
}