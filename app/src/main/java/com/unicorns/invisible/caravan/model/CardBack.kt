package com.unicorns.invisible.caravan.model

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.unicorns.invisible.caravan.R
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonNames

enum class CardBack {
    STANDARD,
    TOPS,
    ULTRA_LUXE,
    GOMORRAH,
    LUCKY_38,

    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("SIERRA_MADRE", "VAULT_21")
    VAULT_21,

    DECK_13,
    UNPLAYABLE,
    WILD_WASTELAND;

    fun isSpecialCard(isAlt: Boolean): Boolean {
        return this == WILD_WASTELAND || isAlt && this == UNPLAYABLE
    }

    fun getCardBackAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_standard.webp"
            TOPS -> "FNV_Caravan_card_back_-_Tops.webp"
            ULTRA_LUXE -> "FNV_Caravan_card_back_-_Ultra-Luxe.webp"
            GOMORRAH -> "FNV_Caravan_card_back_-_Gomorrah.webp"
            LUCKY_38 -> "FNV_Caravan_card_back_-_Lucky_38.webp"
            VAULT_21 -> "standard_alt.webp"
            DECK_13 -> "sm_clean.webp"
            UNPLAYABLE -> "ccp_back.webp"
            WILD_WASTELAND -> "ww_back.webp"
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
            DECK_13 -> "madness_back.webp"
            UNPLAYABLE -> "ccp_alt_back.webp"
            WILD_WASTELAND -> "nuclear_back.webp"
        }
    }

    fun getDeckName(): Int = when (this) {
        STANDARD -> R.string.standard_deck_name
        TOPS -> R.string.tops_deck_name
        ULTRA_LUXE -> R.string.ultra_luxe_deck_name
        GOMORRAH -> R.string.gomorrah_deck_name
        LUCKY_38 -> R.string.lucky_38_deck_name
        VAULT_21 -> R.string.vault_21_deck_name
        DECK_13 -> R.string.deck_13_deck_name
        else -> R.string.other_deck_name
    }

    fun getSierraMadreDeckName(): Int = R.string.standard_deck_alt_name
    fun getMadnessDeckName(): Int = R.string.deck_13_deck_alt_name

    fun getOwners(): List<Int> = when (this) {
        STANDARD -> listOf(R.string.no_one, R.string.standard_deck_owners)
        TOPS -> listOf(R.string.pve_enemy_hard, R.string.pve_enemy_queen)
        ULTRA_LUXE -> listOf(R.string.pve_enemy_easy, R.string.johnson_nash)
        GOMORRAH -> listOf(R.string.pve_enemy_medium, R.string.no_bark)
        LUCKY_38 -> listOf(R.string.pve_enemy_better, R.string.pve_enemy_38)
        VAULT_21 -> listOf(R.string.pve_enemy_best, R.string.pve_enemy_cheater)
        DECK_13 -> listOf(R.string.no_one, R.string.priest)
        else -> listOf(R.string.no_one, R.string.no_one)
    }

    fun getFilter(isAlt: Boolean): ColorFilter {
        if (!isAlt) {
            return ColorFilter.colorMatrix(ColorMatrix())
        }
        return when (this) {
            TOPS -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0.9f, 2f, 0f, 0f, 0f,
                            0.3f, 2f, 0f, 0f, 0f,
                            0.15f, 2f, 0.1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, -1f, 0f, 0f,
                            0f, 0f, 1f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 0f, 1f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            1f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })

            ULTRA_LUXE -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            1f, 0f, 0f, 0f, 0f,
                            0f, 1f, 0f, 0f, 0f,
                            0f, 0f, 16f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 0f, 2f, 0f, 0f,
                            0f, 2f, 0f, 0f, 0f,
                            2f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })

            GOMORRAH -> ColorFilter.colorMatrix(ColorMatrix().apply {
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            0f, 1f, 0f, 0f, 0f,
                            0.5f, 0.5f, 0f, 0f, 0f,
                            1f, 0f, 0f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            -1f, 0f, 0f, 0f, 255f,
                            0f, -1f, 0f, 0f, 255f,
                            0f, 0f, -1f, 0f, 255f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
                timesAssign(
                    ColorMatrix(
                        floatArrayOf(
                            2f, 0f, 0f, 0f, 0f,
                            0f, 2f, 0f, 0f, 0f,
                            0f, 0f, 2f, 0f, 0f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            })
            else -> ColorFilter.colorMatrix(ColorMatrix())
        }
    }

    companion object {
        val playableBacks = listOf(
            STANDARD,
            TOPS,
            ULTRA_LUXE,
            GOMORRAH,
            LUCKY_38,
            VAULT_21,
            DECK_13
        )
        val classicDecks = listOf(
            STANDARD,
            TOPS,
            ULTRA_LUXE,
            GOMORRAH,
            LUCKY_38,
            VAULT_21
        )
    }
}