package com.etirps.zhu.gamedemo

import kotlin.math.*

data class InputData(var origX: Float = 0f,             var origY: Float = 0f,
                     var destX: Float = 0f,             var destY: Float = 0f,
                     var touchedDown: Boolean = false,  var dragging: Boolean = false,
                     var touchedUp: Boolean = false)
{
    /**
     * Returns the distance between orig points and dest points
     */
    val distance: Float
        get() {
            return sqrt((destX - origX).pow(2) + (destY-origY).pow(2))
        }

    /**
     * Returns the angle between the two points
     */
    val angle: Float
        get() {
            val opposite = origX.toDouble() - destX.toDouble()
            val adjacent = origY.toDouble() - destY.toDouble()

            var angle = Math.toDegrees(atan(opposite / adjacent))

            angle += ceil(-angle / 360) * 360

            return -angle.toFloat()
        }

    /**
     * Returns the distance of the aiming line
     */
    val aimingDistance: Float
        get() {
            return sqrt((aimingX - origX).pow(2) + (aimingY-origY).pow(2))
        }

    /**
     * Returns the angle of the aiming line
     */
    val aimingAngle: Float
        get() {
            val opposite = aimingX.toDouble() - origX.toDouble()
            val adjacent = aimingY.toDouble() - origY.toDouble()

            val angle = atan2(opposite, adjacent)

            return angle.toFloat()
        }

    /**
     * Returns the end point (x) of the aiming line
     */
    val aimingX: Float
        get() {
            return if(distance < 400) {
                destX
            } else {
                val ratio = 400 / distance
                ((1 - ratio) * origX) + (ratio * destX)
            }
        }

    /**
     * Returns the end point (y) of the aiming line
     */
    val aimingY: Float
        get() {
            return if(distance < 400) {
                destY
            } else {
                val ratio = 400 / distance
                ((1 - ratio) * origY) + (ratio * destY)
            }
        }
}