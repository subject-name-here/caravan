package com.unicorns.invisible.caravan.model.enemy

sealed class EnemyPvENoBank : EnemyPve() {
    abstract var curCards: Int
    val maxCards: Int
        get() = 4

    abstract var wins: Int
    abstract var winsBlitz: Int
}