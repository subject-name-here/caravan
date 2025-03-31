package com.unicorns.invisible.caravan.cheats

interface Cheat {
    fun getCode(): Int
    suspend fun onEnter(showAlertDialog: (String, String) -> Unit)
}