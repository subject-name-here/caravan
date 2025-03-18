package com.unicorns.invisible.caravan.model.enemy

import kotlinx.serialization.Serializable


@Serializable
sealed class EnemyPve : Enemy {
    abstract val nameId: Int
    abstract val isEven: Boolean
}