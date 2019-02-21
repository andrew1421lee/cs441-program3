package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.TimeUtils

abstract class StatusEffect {

    var duration: Float = 5f
    var timeRemaining: Float = duration
    var type: StatusTypes = StatusTypes.DEFAULT
    var lastTime: Long = TimeUtils.millis()
    var enabled: Boolean = true

    private fun updateDuration() {
        val timeElapsed = TimeUtils.millis() - lastTime

        timeRemaining = duration - timeElapsed / 1000f

        if(timeElapsed / 1000 >= duration) {
            enabled = false
        }
    }

    fun apply(actor: Actor) {
        if(enabled) {
            applyEffect(actor)
        } else {
            remove(actor)
        }

        updateDuration()
    }

    override fun toString(): String {
        return type.toString()
    }

    abstract fun applyEffect(actor: Actor)

    abstract fun remove(actor: Actor)

}