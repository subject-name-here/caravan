package com.unicorns.invisible.caravan.model.enemy

sealed class EnemyPvEWithBank : EnemyPve() {
    abstract var curBets: Int
    abstract val maxBets: Int
    abstract val bet: Int

    abstract var winsNoBet: Int
    abstract var winsBet: Int
    abstract var winsBlitzNoBet: Int
    abstract var winsBlitzBet: Int
}