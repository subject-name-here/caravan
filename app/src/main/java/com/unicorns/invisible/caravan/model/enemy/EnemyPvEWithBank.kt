package com.unicorns.invisible.caravan.model.enemy

sealed class EnemyPvEWithBank : EnemyPve() {
    abstract var bank: Int
    abstract val maxBank: Int
    abstract val bet: Int

    abstract var winsNoBet: Int
    abstract var winsBet: Int
    abstract var winsBlitzNoBet: Int
    abstract var winsBlitzBet: Int
}