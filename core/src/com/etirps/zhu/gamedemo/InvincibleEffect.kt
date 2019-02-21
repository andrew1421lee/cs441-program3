package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class InvincibleEffect(time: Float) : StatusEffect() {


    init {
        duration = time
        type = StatusTypes.INVINCIBLE
    }

    override fun apply(actor: Actor) {

        if(enabled) {
            if(actor is Player) {
                actor.shielded = true
            }
        } else {
            remove(actor)
        }

        updateDuration()
    }

    override fun remove(actor: Actor) {
        if(actor is Player) {
            actor.shielded = false
            actor.removeStatusEffect(this)
        }
    }
}