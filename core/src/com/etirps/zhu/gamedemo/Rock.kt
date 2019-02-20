package com.etirps.zhu.gamedemo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor

class Rock(posX: Float,             posY: Float,
           var texture: Texture,    var size: Float = 200f,
           var speedX: Float = 1f,  var speedY: Float = 1f,
           var speedSpin: Float = 0.1f,
           debugFont: BitmapFont? = null): Actor() {

    private val font: BitmapFont?
    var bounds: Rectangle
    var polygon: Polygon

    // Set actor x,y to given by constructor
    init {
        x = posX
        y = posY
        font = debugFont

        bounds = Rectangle(x, y, size, size)
        polygon = Polygon(floatArrayOf( 0f,             0f,
                                        bounds.width,   0f,
                                        bounds.width,   bounds.height,
                                        0f,             bounds.height))
        polygon.setOrigin(bounds.width / 2, bounds.height / 2)
        polygon.setPosition(x, y)
    }

    fun split(): List<Rock> {
        val newSize = size / 2
        if(newSize < 50f) {
            return listOf()
        }

        var dirModifierX = (1..2).random() * (-1..1).random()
        if(dirModifierX == 0) { dirModifierX = 1 }

        var dirModifierY = (1..2).random() * (-1..1).random()
        if(dirModifierY == 0) { dirModifierY = 1 }

        return listOf(  Rock(x, y, texture, size = size / 2, speedX = speedX * dirModifierX, speedY = speedY * dirModifierY, speedSpin = speedSpin, debugFont = font),
                        Rock(x, y, texture, size = size / 2, speedX = speedX * -dirModifierX, speedY = speedY * -dirModifierY, speedSpin = -speedSpin, debugFont = font))
    }

    // Draw rock on screen
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, size / 2, size / 2, size, size, 1f, 1f, rotation, 0, 0, 500, 500, false, false)
        font?.draw(batch, "angle:$rotation\npos:$x x $y\nspeed:$speedX x $speedY\nspin:$speedSpin", x, y)
    }

    // Update rock positions and rotation
    override fun act(delta: Float) {
        x += speedX
        y += speedY
        rotation += speedSpin

        bounds.x = x
        bounds.y = y
        polygon.setPosition(x, y)
        polygon.rotation = rotation

        // teleport rock back on screen
        when {
            x + (size / 2f) > Gdx.graphics.width.toFloat()    ->  x = 0f - (size / 2f)
            x + (size / 2f) < 0                               ->  x = Gdx.graphics.width.toFloat() - (size / 2f)

            y + (size / 2f) > Gdx.graphics.height.toFloat()   ->  y = 0f - (size / 2f)
            y + (size / 2f) < 0                               ->  y = Gdx.graphics.height.toFloat() - (size / 2f)
        }
    }
}