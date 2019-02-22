package com.etirps.zhu.gamedemo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor

class Player (posX: Float, posY: Float,
              var texture: Texture,
              var speedX: Float = 0f,
              var speedY: Float = 0f,
              debugFont: BitmapFont? = null): Actor() {

    private val font: BitmapFont?
    var shielded: Boolean
    var bounds: Rectangle
    var polygon: Polygon
    var mass: Float

    private var statusEffects: MutableList<StatusEffect>
    private var removedStatusEffects: MutableList<StatusEffect>

    init {
        x = posX
        y = posY
        width = 100f
        height = 100f
        font = debugFont
        shielded = false
        mass = 80f

        statusEffects = mutableListOf()
        removedStatusEffects = mutableListOf()

        bounds = Rectangle(x, y, width, height)
        polygon = Polygon(floatArrayOf( 0f,             0f,
                                        bounds.width,   0f,
                                        bounds.width,   bounds.height,
                                        0f,             bounds.height))
        polygon.setOrigin(bounds.width / 2, bounds.height / 2)
        polygon.setPosition(x, y)
    }

    fun addStatusEffect(statusEffect: StatusEffect) {
        // If status does not exist, add status
        if(statusEffects.none { x -> x.type == statusEffect.type }) {
            statusEffect.apply(this)
            statusEffects.add(statusEffect)
        } else {
            // status already exists, increment timer
            val effect = statusEffects.filter { x -> x.type == statusEffect.type }[0]
            effect.duration += statusEffect.duration
        }

    }

    /*
    fun removeStatusEffect(statusEffect: StatusEffect) {
        statusEffects.remove(statusEffect)
    }*/

    fun getStatusEffects(): List<StatusEffect> {
        return statusEffects.toList()
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.draw(texture, x, y, width / 2, height / 2, width, height, 1f, 1f, rotation, 0, 0, 500, 500, false, false)

        font?.draw(batch, "angle:$rotation\npos:$x x $y\nspeed:$speedX x $speedY\nshield:$shielded", x, y)
    }

    override fun act(delta: Float) {
        x += speedX
        y += speedY
        //rotation += speedSpin

        for(effect in statusEffects) {
            effect.apply(this)
            if(!effect.enabled) {
                removedStatusEffects.add(effect)
                effect.remove(this)
            }
        }

        statusEffects.removeAll(removedStatusEffects)
        removedStatusEffects.clear()

        bounds.x = x
        bounds.y = y
        polygon.setPosition(x, y)
        polygon.rotation = rotation

        // teleport player back on screen
        when {
            x + (width / 2f) > Gdx.graphics.width.toFloat()     ->  x = 0f - (width / 2f)
            x + (width / 2f) < 0                                ->  x = Gdx.graphics.width.toFloat() - (width / 2f)

            y + (height / 2f) > Gdx.graphics.height.toFloat()   ->  y = 0f - (height / 2f)
            y + (height / 2f) < 0                               ->  y = Gdx.graphics.height.toFloat() - (height / 2f)
        }
    }
}