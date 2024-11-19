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

    fun getFilter(isAlt: Boolean): ColorFilter {
        if (!isAlt) {
            return ColorFilter.colorMatrix(ColorMatrix())
        }
        return when (this) {
            STANDARD -> ColorFilter.colorMatrix(ColorMatrix().apply {
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