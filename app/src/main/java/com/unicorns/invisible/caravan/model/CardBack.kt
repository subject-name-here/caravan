package com.unicorns.invisible.caravan.model

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

    fun getName(): String = when (this) {
        STANDARD -> "standard deck"
        TOPS -> "Tops deck"
        ULTRA_LUXE -> "Ultra-Luxe deck"
        GOMORRAH -> "Gomorrah deck"
        LUCKY_38 -> "Lucky 38 deck"
        SIERRA_MADRE -> "Sierra Madre deck"
    }
}