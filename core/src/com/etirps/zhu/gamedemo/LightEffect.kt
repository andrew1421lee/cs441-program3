package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class LightEffect(time: Float = 5f): StatusEffect() {

    var applied: Boolean = false

    init {
        duration = time
        type = StatusTypes.LIGHT
    }

    override fun applyEffect(actor: Actor) {

        if(actor is Player && !applied) {
            actor.mass *= 0.25f
            applied = true
        }

    }

    override fun remove(actor: Actor) {
        if(actor is Player && applied) {
            actor.mass *= 4f
        }
    }
}