package com.etirps.zhu.gamedemo

import com.badlogic.gdx.graphics.Texture

class Rock (var x: Float,               var y: Float,
            var speedX: Float = 1f,    var speedY: Float = 1f,
            var angle: Float = 0f,      var rotation: Float = 0f,
            var size: Float = 100f,     var image: Texture) {

    var listener : Rock? = null

    constructor(src: Rock): this(src.x, src.y, image = src.image) {
        speedX = src.speedX
        speedY = src.speedY
        angle = src.angle
        rotation = src.rotation
        size = src.size
        image = src.image
        listener = src.listener
    }

    fun update() {
        x += speedX
        y += speedY

        when {
            x + (size / 2) > 1920 -> x = 0f - (size / 2)
            x + (size / 2) < 0 -> x = 1920f + (size / 2)

            y + (size / 2) > 1080 -> y = 0f - (size / 2)
            y + (size / 2) < 0 -> y = 1080f + (size / 2)
        }
    }
}