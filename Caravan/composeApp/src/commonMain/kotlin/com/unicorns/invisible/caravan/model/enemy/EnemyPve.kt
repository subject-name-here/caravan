package com.unicorns.invisible.caravan.model.enemy

import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource


@Serializable
sealed class EnemyPve : Enemy {
    abstract val nameId: StringResource
    abstract val isEven: Boolean
    open val level: Int = 0
    open val isAvailable = false
}