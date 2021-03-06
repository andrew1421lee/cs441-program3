package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class HeavyEffect(time: Float = 5f): StatusEffect() {

    var applied: Boolean = false

    init {
        duration = time
        type = StatusTypes.HEAVY
    }

    override fun applyEffect(actor: Actor) {

        if(actor is Player && !applied) {
            actor.mass *= 4f
            applied = true
        }

    }

    override fun remove(actor: Actor) {
        if(actor is Player && applied) {
            actor.mass *= 0.25f
        }
    }
}