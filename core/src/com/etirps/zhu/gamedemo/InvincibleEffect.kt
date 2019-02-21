package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class InvincibleEffect(time: Float = 3f) : StatusEffect() {


    init {
        duration = time
        type = StatusTypes.INVINCIBLE
    }

    override fun applyEffect(actor: Actor) {
        if(actor is Player) {
            actor.shielded = true
        }
    }

    override fun remove(actor: Actor) {
        if(actor is Player) {
            actor.shielded = false
        }
    }
}