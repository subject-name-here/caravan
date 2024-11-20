package com.unicorns.invisible.caravan.model

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.unicorns.invisible.caravan.R


enum class CardBack {
    STANDARD,
    TOPS,
    ULTRA_LUXE,
    GOMORRAH,
    LUCKY_38,
    VAULT_21,
    SIERRA_MADRE,
    CHINESE,
    ENCLAVE,
    MADNESS;

    fun getDeckName(): Int = when (this) {
        STANDARD -> R.string.standard_deck_name
        TOPS -> R.string.tops_deck_name
        ULTRA_LUXE -> R.string.ultra_luxe_deck_name
        GOMORRAH -> R.string.gomorrah_deck_name
        LUCKY_38 -> R.string.lucky_38_deck_name
        VAULT_21 -> R.string.vault_21_deck_name
        SIERRA_MADRE -> R.string.sierra_madre_deck_name
        MADNESS -> R.string.madness_deck_name
        CHINESE -> R.string.chinese_deck_name
        ENCLAVE -> R.string.enclave_deck_name
    }

    fun hasAltPlayable(): Boolean = this !in listOf(CHINESE, ENCLAVE, MADNESS)

    fun getCardBackAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_standard.webp"
            TOPS -> "FNV_Caravan_card_back_-_Tops.webp"
            ULTRA_LUXE -> "FNV_Caravan_card_back_-_Ultra-Luxe.webp"
            GOMORRAH -> "FNV_Caravan_card_back_-_Gomorrah.webp"
            LUCKY_38 -> "FNV_Caravan_card_back_-_Lucky_38.webp"
            VAULT_21 -> "standard_alt.webp"
            SIERRA_MADRE -> "FNV_Caravan_card_back_-_Sierra_Madre.webp"
            CHINESE -> "ccp_back.webp"
            ENCLAVE -> "FNV_Caravan_card_back_-_st_alt.webp" // TODO
            MADNESS -> "madness_back.webp"
        }
    }

    fun getCardBackAltAsset(): String {
        return when (this) {
            STANDARD -> "FNV_Caravan_card_back_-_st_alt.webp"
            TOPS -> "tops_alt.webp"
            ULTRA_LUXE -> "ultra_luxe_alt.webp"
            GOMORRAH -> "gomorrah_alt.webp"
            LUCKY_38 -> "lucky_38_alt.webp"
            VAULT_21 -> "sm_alt.webp"
            SIERRA_MADRE -> "sm_clean.webp"
            CHINESE -> "ccp_alt_back.webp"
            ENCLAVE -> "nuclear_back.webp"
            MADNESS -> "ww_back.webp"
        }
    }

    fun getFilter(isAlt: Boolean): ColorFilter {
        if (!isAlt) {
            return ColorFilter.colorMatrix(ColorMatrix())
        }
        return when (this) {
            STANDARD -> ColorFilter.colorMatrix(ColorMatrix(
                floatArrayOf(
                    0f, 0f, 1f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    1f, 0f, 0f, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                )
            ))

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
}