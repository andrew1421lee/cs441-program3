package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class RockEffect(time: Float): StatusEffect() {

    init {
        duration = time
        type = StatusTypes.ROCK
    }

    override fun applyEffect(actor: Actor) {

        if(actor is Player) {
            actor.mass = 320f
        }

    }

    override fun remove(actor: Actor) {
        if(actor is Player) {
            actor.mass = 80f
        }
    }
}