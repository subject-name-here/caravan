package com.unicorns.invisible.caravan.multiplayer

data class RoomResponse(val roomNumber: Int, val enemyDeckString: String, val isCustom: Boolean? = null)