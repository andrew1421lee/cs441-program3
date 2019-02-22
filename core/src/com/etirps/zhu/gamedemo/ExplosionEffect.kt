package com.etirps.zhu.gamedemo

import com.badlogic.gdx.scenes.scene2d.Actor

class ExplosionEffect: StatusEffect() {

    init {
        duration = 1f
        type = StatusTypes.EXPLOSION
    }

    override fun applyEffect(actor: Actor) {
        // Only happens once
        if(actor is Player){
            for (i in (0..10)){
                val angle = i * 36f
                actor.fire(angle, 400f)
            }
        }
        enabled = false
    }

    override fun remove(actor: Actor) {
        // Nothing to remove
    }
}