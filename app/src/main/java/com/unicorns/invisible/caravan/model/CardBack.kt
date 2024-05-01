package com.unicorns.invisible.caravan.model

import com.unicorns.invisible.caravan.R

enum class CardBack {
    STANDARD,
    TOPS,
    ULTRA_LUXE,
    GOMORRAH,
    LUCKY_38,
    SIERRA_MADRE;

    fun getCardBackAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_standard.webp"
            TOPS -> "FNV_Caravan_card_back_-_Tops.webp"
            ULTRA_LUXE -> "FNV_Caravan_card_back_-_Ultra-Luxe.webp"
            GOMORRAH -> "FNV_Caravan_card_back_-_Gomorrah.webp"
            LUCKY_38 -> "FNV_Caravan_card_back_-_Lucky_38.webp"
            SIERRA_MADRE -> "FNV_Caravan_card_back_-_Sierra_Madre.webp"
        }
    }

    fun getDeckName(): Int = when (this) {
        STANDARD -> R.string.standard_deck_name
        TOPS -> R.string.tops_deck_name
        ULTRA_LUXE -> R.string.ultra_luxe_deck_name
        GOMORRAH -> R.string.gomorrah_deck_name
        LUCKY_38 -> R.string.lucky_38_deck_name
        SIERRA_MADRE -> R.string.sierra_madre_deck_name
    }
}