package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.MainActivity
import com.unicorns.invisible.caravan.R
import com.unicorns.invisible.caravan.save.Save

enum class CardBack {
    STANDARD,
    TOPS,
    ULTRA_LUXE,
    GOMORRAH,
    LUCKY_38,
    VAULT_21;

    fun getCardBackAssetSplit(activity: MainActivity): String {
        return if (activity.save!!.altDecks[this] == Save.AltDeckStatus.CHOSEN) {
            getCardBackAltAsset()
        } else {
            getCardBackAsset()
        }
    }

    fun getCardBackAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_standard.webp"
            TOPS -> "FNV_Caravan_card_back_-_Tops.webp"
            ULTRA_LUXE -> "FNV_Caravan_card_back_-_Ultra-Luxe.webp"
            GOMORRAH -> "FNV_Caravan_card_back_-_Gomorrah.webp"
            LUCKY_38 -> "FNV_Caravan_card_back_-_Lucky_38.webp"
            VAULT_21 -> "standard_alt.webp"
        }
    }

    fun getCardBackAltAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_Sierra_Madre.webp"
            TOPS -> "tops_alt.webp"
            ULTRA_LUXE -> "ultra_luxe_alt.webp"
            GOMORRAH -> "gomorrah_alt.webp"
            LUCKY_38 -> "lucky_38_alt.webp"
            VAULT_21 -> "sm_alt.webp"
        }
    }

    fun getDeckName(): Int = when (this) {
        STANDARD -> R.string.standard_deck_name
        TOPS -> R.string.tops_deck_name
        ULTRA_LUXE -> R.string.ultra_luxe_deck_name
        GOMORRAH -> R.string.gomorrah_deck_name
        LUCKY_38 -> R.string.lucky_38_deck_name
        VAULT_21 -> R.string.vault_21_deck_name
    }
    fun getSierraMadreDeckName(): Int = R.string.standard_deck_alt_name

    fun getOwners(): List<Int> = when (this) {
        STANDARD -> listOf(R.string.standard_deck_owners)
        TOPS -> listOf(R.string.pve_enemy_hard, R.string.pve_enemy_queen)
        ULTRA_LUXE -> listOf(R.string.pve_enemy_easy, R.string.johnson_nash)
        GOMORRAH -> listOf(R.string.pve_enemy_medium, R.string.no_bark)
        LUCKY_38 -> listOf(R.string.pve_enemy_better, R.string.pve_enemy_38)
        VAULT_21 -> listOf(R.string.pve_enemy_best, R.string.pve_enemy_cheater)
    }
}