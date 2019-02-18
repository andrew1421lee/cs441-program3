package com.etirps.zhu.gamedemo

import kotlin.math.atan
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

data class InputData(var origX: Float = 0f,             var origY: Float = 0f,
                     var destX: Float = 0f,             var destY: Float = 0f,
                     var touchedDown: Boolean = false,  var dragging: Boolean = false)
{
    val distance: Float
        get() {
            return sqrt((destX - origX).pow(2) + (destY-origY).pow(2))
        }

    val angle: Float
        get() {
            val opposite = origX.toDouble() - destX.toDouble()
            val adjacent = origY.toDouble() - destY.toDouble()

            var angle = Math.toDegrees(atan(opposite / adjacent))

            angle += ceil(-angle / 360) * 360

            return -1 * angle.toFloat()
        }
}