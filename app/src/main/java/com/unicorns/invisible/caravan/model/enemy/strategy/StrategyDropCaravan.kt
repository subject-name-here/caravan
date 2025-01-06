package com.unicorns.invisible.caravan.model.enemy.strategy

import com.unicorns.invisible.caravan.model.Game


class StrategyDropCaravan(private val selection: DropSelection) : Strategy {
    override fun move(game: Game): Boolean {
        val overWeightCaravans = game.enemyCaravans.filter { it.getValue() > 26 }

        if (overWeightCaravans.isNotEmpty()) {
            when (selection) {
                DropSelection.RANDOM -> {
                    overWeightCaravans.random().dropCaravan()
                }
                DropSelection.MAX_WEIGHT -> {
                    overWeightCaravans.maxBy { it.getValue() }.dropCaravan()
                }
            }
        }

        return overWeightCaravans.isNotEmpty()
    }
}

enum class DropSelection {
    RANDOM,
    MAX_WEIGHT
}