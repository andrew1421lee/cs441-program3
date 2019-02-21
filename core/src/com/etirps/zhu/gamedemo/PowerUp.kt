package com.etirps.zhu.gamedemo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

class PowerUp(posX: Float, posY: Float, val statusEffect: StatusEffect, val texture: Texture, debugFont: BitmapFont? = null): Actor() {

    var bounds: Rectangle
    var polygon: Polygon
    var duration: Float = 5f
    var timeRemaining: Float = duration
    var lastTime: Long = TimeUtils.millis()
    var active = true
    var font: BitmapFont? = debugFont

    init {
        x = posX
        y = posY
        width = 100f
        height = 80f

        bounds = Rectangle(x, y, width, height)
        polygon = Polygon(floatArrayOf( 0f,             0f,
                bounds.width,   0f,
                bounds.width,   bounds.height,
                0f,             bounds.height))
        polygon.setOrigin(bounds.width / 2, bounds.height / 2)
        polygon.setPosition(x, y)
    }

    private fun updateDuration() {
        val timeElapsed = TimeUtils.millis() - lastTime

        timeRemaining =  duration - timeElapsed / 1000f

        if(timeElapsed / 1000 >= duration) {
            this.remove()
            active = false
        }
    }

    override fun act(delta: Float) {
        updateDuration()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width, height)

        font?.draw(batch, "pos:$x x $y\nlife:$timeRemaining\npower:${statusEffect.type}", x, y)
    }
}