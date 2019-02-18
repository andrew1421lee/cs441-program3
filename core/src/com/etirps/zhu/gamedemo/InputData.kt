package com.etirps.zhu.gamedemo

import kotlin.math.*

data class InputData(var origX: Float = 0f,             var origY: Float = 0f,
                     var destX: Float = 0f,             var destY: Float = 0f,
                     var touchedDown: Boolean = false,  var dragging: Boolean = false)
{
    val distance: Float
        get() {
            return sqrt((destX - origX).pow(2) + (destY-origY).pow(2))
        }

    val powerLineDistance: Float
        get() {
            return sqrt((powerLineX - origX).pow(2) + (powerLineY-origY).pow(2))
        }

    val angle: Float
        get() {
            val opposite = origX.toDouble() - destX.toDouble()
            val adjacent = origY.toDouble() - destY.toDouble()

            var angle = Math.toDegrees(atan(opposite / adjacent))

            angle += ceil(-angle / 360) * 360

            return -angle.toFloat()
        }

    val powerLineAngle: Float
        get() {
            val opposite = powerLineX.toDouble() - origX.toDouble()
            val adjacent = powerLineY.toDouble() - origY.toDouble()

            val angle = atan2(opposite, adjacent)

            //angle += ceil(-angle / 360) * 360

            return angle.toFloat()
        }

    val powerLineX: Float
        get() {
            return if(distance < 400) {
                destX
            } else {
                val ratio = 400 / distance
                ((1 - ratio) * origX) + (ratio * destX)
            }
        }

    val powerLineY: Float
        get() {
            return if(distance < 400) {
                destY
            } else {
                val ratio = 400 / distance
                ((1 - ratio) * origY) + (ratio * destY)
            }
        }
}