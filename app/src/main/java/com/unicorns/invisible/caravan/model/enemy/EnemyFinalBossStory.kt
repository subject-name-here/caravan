package com.unicorns.invisible.caravan.model.enemy

import com.unicorns.invisible.caravan.AnimationSpeed
import com.unicorns.invisible.caravan.model.CardBack
import com.unicorns.invisible.caravan.model.Game
import com.unicorns.invisible.caravan.model.primitives.CResources
import com.unicorns.invisible.caravan.model.primitives.CustomDeck


class EnemyFinalBossStory : Enemy {
    override fun createDeck(): CResources = CResources(CustomDeck(CardBack.CHINESE, 0))
    override fun isSpeedOverriding() = true

    override suspend fun makeMove(game: Game, speed: AnimationSpeed) {}
}