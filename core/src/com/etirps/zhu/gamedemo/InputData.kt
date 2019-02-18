package com.etirps.zhu.gamedemo

data class InputData(var origX: Float = 0f,             var origY: Float = 0f,
                     var destX: Float = 0f,             var destY: Float = 0f,
                     var touchedDown: Boolean = false,  var dragging: Boolean = false)