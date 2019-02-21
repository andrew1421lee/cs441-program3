package com.etirps.zhu.gamedemo

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont

enum class StatusTypes { DEFAULT, INVINCIBLE, HEAVY, LIGHT }

data class PowerUpData (val type: StatusTypes, val texture: Texture)

class PowerUpBuilder(debugFont: BitmapFont? = null) {
    private val powerUpList: MutableList<PowerUpData> = mutableListOf()
    private val font: BitmapFont? = debugFont

    fun loadPowerUp(powerUpData: PowerUpData) {
        if(!powerUpList.contains(powerUpData)) {
            powerUpList.add(powerUpData)
        }
    }

    fun createPowerUp(posX: Float, posY: Float, type: StatusTypes): PowerUp? {
        return when(type) {
            StatusTypes.INVINCIBLE -> PowerUp(posX, posY, InvincibleEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            StatusTypes.HEAVY -> PowerUp(posX, posY, HeavyEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            StatusTypes.LIGHT -> PowerUp(posX, posY, LightEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            else -> null
        }
    }

    fun createPowerUp(posX: Float, posY: Float): PowerUp? {
        val randomIndex = (1 until StatusTypes.values().size).random()
        val type: StatusTypes = StatusTypes.values()[randomIndex]

        return when(type) {
            StatusTypes.INVINCIBLE -> PowerUp(posX, posY, InvincibleEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            StatusTypes.HEAVY -> PowerUp(posX, posY, HeavyEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            StatusTypes.LIGHT -> PowerUp(posX, posY, LightEffect(10f), powerUpList.filter { x -> x.type == type }[0].texture, font)
            else -> null
        }
    }
}