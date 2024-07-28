package com.unicorns.invisible.caravan.model.challenge

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.Card
import kotlinx.serialization.Serializable


@Serializable
sealed interface Challenge {
    fun processMove(move: Move, game: Game)
    fun processGameResult(game: Game)

    fun getName(activity: MainActivity): String
    fun getDescription(activity: MainActivity): String
    fun getProgress(): String

    fun isCompleted(): Boolean

    data class Move(
        val moveCode: Int,
        val handCard: Card? = null,
    )
}