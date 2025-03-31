package com.unicorns.invisible.caravan.model.enemy

sealed class EnemyPvENoBank : EnemyPve() {
    abstract var wins: Int
    abstract var winsBlitz: Int
}