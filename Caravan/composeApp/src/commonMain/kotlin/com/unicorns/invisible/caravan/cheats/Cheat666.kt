package com.unicorns.invisible.caravan.cheats


data object Cheat666 : Cheat {
    override fun getCode() = 666

    override suspend fun onEnter(showAlertDialog: (String, String) -> Unit) {
        // TODO
    }
}