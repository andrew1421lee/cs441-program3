package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class RockEffect(time: Float): StatusEffect() {

    init {
        duration = time
        type = StatusTypes.ROCK
    }

    override fun applyEffect(actor: Actor) {

        if(actor is Player) {
            actor.speedX = 0f
            actor.speedY = 0f
        }

    }

    override fun remove(actor: Actor) {
        if(actor is Player) {
            //actor.shielded = false
            actor.removeStatusEffect(this)
        }
    }
}